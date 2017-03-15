package com.qqonline.Manager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.qqonline.db.Internal_Db;
import com.qqonline.db.MPF_Db;
import com.qqonline.domain.MpfMachine;

/**
 * 外置存储卡中的
 * MpfMachine机器表的业务逻辑
 * <br/>基本操作：添加addMpfMachine，获取机器信息getMpfMachine，查询是否有设备MpfMachineIsHave
 *
 *  *<br/>
 * 分为内部和外部的主要原因是就算换了SD卡或格式化了SD卡，激活数据也不会丢失
 * 外部激活数据的主要作用是备份
 */
public class MpfMachineService {
    private static final String TAG="MpfMachineService";
    /**
     * 外置SD卡上的数据库（这个类主要用到这个数据库里的备份激活数据）
     */
    private MPF_Db Mpfdb;
    /**
     * 内部存储中的数据库（主要放激活数据）
     */
    private Internal_Db internal_db;
    public MpfMachineService(Context context, int version) {
        this.Mpfdb = new MPF_Db(context, version);
        this.internal_db=new Internal_Db(context,version);
    }

    /**
     * fengcheng.ye 2014-7-18 17:24
     * 添加机器信息
     *
     * @param machine,机器类
     */
    public void addMpfMachine(MpfMachine machine) {
        SQLiteDatabase db = internal_db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DbId", machine.getDbId());
        cv.put("MachineCode", machine.getMachineCode());
        cv.put("MachineSerialNumber", machine.getMachineSerialNumber());
        cv.put("BindingPassword", machine.getBindingPassword());
        try {
            db.insert("MPF_Machine", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        //如果外置存储卡中也没有激活数据，则同时也写一份激活数据到外置存储数据库，作备份用
        if (!isExternalMachineHave()) {
            db=Mpfdb.getWritableDatabase();
            try {
                db.insert("MPF_Machine", null, cv);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }

        }
    }
    /**
     * fengcheng.ye 2014-7-21
     * 获取机器信息
     *
     * @return MpfMachine
     */
    public MpfMachine getMpfMachine() {
        SQLiteDatabase db = getReadableDB();
        if (db == null) {
            return null;
        }
        MpfMachine result = null;
        Cursor cursor = null;
        try {
            cursor = db.query("MPF_Machine", new String[]{"DbId", "MachineCode", "MachineSerialNumber", "BindingPassword"}, null, null, null, null, null, null);
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return null;
        }
        if (cursor.moveToFirst()) {
            result = new MpfMachine();
            result.setDbId(cursor.getInt(cursor.getColumnIndex("DbId")));
            result.setMachineSerialNumber(cursor.getString(cursor.getColumnIndex("MachineSerialNumber")));
            result.setBindingPassword(cursor.getString(cursor.getColumnIndex("BindingPassword")));
            result.setMachineCode(cursor.getString(cursor.getColumnIndex("MachineCode")));
        }

        cursor.close();
        db.close();
        return result;
    }

    public int getMPFDBID() {
        SQLiteDatabase db = getReadableDB();
        if (db == null) {
            return -1;
        }
        int dbID = -1;
        Cursor cursor = null;
        try {
            cursor = db.query("MPF_Machine", new String[]{"DbId"}, null, null, null, null, null, null);
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return -1;
        }
        if (cursor.moveToFirst()) {
            dbID = cursor.getInt(cursor.getColumnIndex("DbId"));
        }
        cursor.close();
        db.close();
        return dbID;
    }

    /**
     * fengcheng.ye 2014-7-21
     * 检查内外部数据库，设备信息是否存在
     *
     * @Return
     */
    public boolean MpfMachineIsHave() {
        return (isInternalMachineHave() || isExternalMachineHave());
    }
    /**
     * 内部存储数据库是否存在激活信息
     * @return
     */
    public boolean isInternalMachineHave() {
        boolean result = false;
        SQLiteDatabase db = internal_db.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("MPF_Machine", null, null, null, null, null, null, null);
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return false;
        }
        if (cursor.moveToFirst() && cursor.getCount() >0) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 外部存储数据库是否存在激活信息
     * @return
     */
    public boolean isExternalMachineHave() {
        boolean result = false;
        SQLiteDatabase db = Mpfdb.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("MPF_Machine", null, null, null, null, null, null, null);
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
            return  false ;
        }
        if (cursor.moveToFirst() && cursor.getCount()>0) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public long MpfMachineUpdateBindCode(String bindCodes) {
        Log.i("MpfMachineUpdateBindCode", bindCodes);
        long result = 0;
        if (!MpfMachineIsHave()) {
            return 0;
        }
        MpfMachine temp = getMpfMachine();
        String id = temp.getMachineSerialNumber();
        String oldBindCode = temp.getBindingPassword();
        temp = null;
        if (oldBindCode.equals(bindCodes)) {
            oldBindCode = null;
            return 0;
        }
        SQLiteDatabase db = internal_db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("BindingPassword", bindCodes);
        //	db.insert("MPF_Machine", null, cv);
//		MachineSerialNumber
        try {
            result = db.update("MPF_Machine", cv, "ID=?", new String[]{"1"});
        } catch (Exception e) {
            db.close();
            e.printStackTrace();
        }
        //如果外置数据库（备份数据库）也有激活数据，同时更新外围数据库
        if (isExternalMachineHave()) {
            if (db.isOpen()) {
                db.close();
            }
            db = Mpfdb.getWritableDatabase();
            long tempEf = -1;
            try {
                tempEf = db.update("MPF_Machine", cv, "ID=?", new String[]{"1"});
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result == 0 && tempEf != -1) {
                result = tempEf;
            }
        }
        cv .clear();
        db.close();
        return result;
    }

    /**
     * 优先获取内部数据库，如果没有，才获取外置数据库
     * @return
     */
    private synchronized SQLiteDatabase getWritableDB() {
        SQLiteDatabase db=null;
        if (isInternalMachineHave()) {
            db = internal_db.getWritableDatabase();
        } else if (isExternalMachineHave()){
            db=Mpfdb.getWritableDatabase();
        }
        return db;
    }

    /**
     * 优先获取内部数据库，如果没有，才获取外置数据库
     * @return
     */
    private synchronized SQLiteDatabase getReadableDB() {
        SQLiteDatabase db=null;
        if (isInternalMachineHave()) {
            db = internal_db.getReadableDatabase();
        } else if (isExternalMachineHave()){
            db=Mpfdb.getReadableDatabase();
        }
        return db;
    }
    public void copyMachineDataToInternalDatabase() {
        if (isInternalMachineHave()) {
            return;
        }
        MpfMachine machine = getMpfMachine();
        SQLiteDatabase dbInternal = internal_db.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DbId", machine.getDbId());
        cv.put("MachineCode", machine.getMachineCode());
        cv.put("MachineSerialNumber", machine.getMachineSerialNumber());
        cv.put("BindingPassword", machine.getBindingPassword());
        try {
            dbInternal.insert("MPF_Machine", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbInternal.close();
        }
    }
    public void release() {
        Mpfdb.close();
        internal_db.close();
    }
}
