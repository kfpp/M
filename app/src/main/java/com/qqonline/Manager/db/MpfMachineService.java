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
 * ���ô洢���е�
 * MpfMachine�������ҵ���߼�
 * <br/>�������������addMpfMachine����ȡ������ϢgetMpfMachine����ѯ�Ƿ����豸MpfMachineIsHave
 *
 *  *<br/>
 * ��Ϊ�ڲ����ⲿ����Ҫԭ���Ǿ��㻻��SD�����ʽ����SD������������Ҳ���ᶪʧ
 * �ⲿ�������ݵ���Ҫ�����Ǳ���
 */
public class MpfMachineService {
    private static final String TAG="MpfMachineService";
    /**
     * ����SD���ϵ����ݿ⣨�������Ҫ�õ�������ݿ���ı��ݼ������ݣ�
     */
    private MPF_Db Mpfdb;
    /**
     * �ڲ��洢�е����ݿ⣨��Ҫ�ż������ݣ�
     */
    private Internal_Db internal_db;
    public MpfMachineService(Context context, int version) {
        this.Mpfdb = new MPF_Db(context, version);
        this.internal_db=new Internal_Db(context,version);
    }

    /**
     * fengcheng.ye 2014-7-18 17:24
     * ��ӻ�����Ϣ
     *
     * @param machine,������
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
        //������ô洢����Ҳû�м������ݣ���ͬʱҲдһ�ݼ������ݵ����ô洢���ݿ⣬��������
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
     * ��ȡ������Ϣ
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
     * ������ⲿ���ݿ⣬�豸��Ϣ�Ƿ����
     *
     * @Return
     */
    public boolean MpfMachineIsHave() {
        return (isInternalMachineHave() || isExternalMachineHave());
    }
    /**
     * �ڲ��洢���ݿ��Ƿ���ڼ�����Ϣ
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
     * �ⲿ�洢���ݿ��Ƿ���ڼ�����Ϣ
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
        //����������ݿ⣨�������ݿ⣩Ҳ�м������ݣ�ͬʱ������Χ���ݿ�
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
     * ���Ȼ�ȡ�ڲ����ݿ⣬���û�У��Ż�ȡ�������ݿ�
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
     * ���Ȼ�ȡ�ڲ����ݿ⣬���û�У��Ż�ȡ�������ݿ�
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
