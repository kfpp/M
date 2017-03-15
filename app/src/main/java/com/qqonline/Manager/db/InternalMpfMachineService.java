package com.qqonline.Manager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.qqonline.db.Internal_Db;
import com.qqonline.domain.MpfMachine;

/**
 * �ڲ��洢�е�
 * MpfMachine�������ҵ���߼�
 * <br/>�������������addMpfMachine����ȡ������ϢgetMpfMachine����ѯ�Ƿ����豸MpfMachineIsHave
 *
 * ��Ϊ�ڲ����ⲿ����Ҫԭ���Ǿ��㻻��SD�����ʽ����SD������������Ҳ���ᶪʧ
 * �ⲿ�������ݵ���Ҫ�����Ǳ���
 */
public class InternalMpfMachineService {
    private static final String TAG="InternalMpfMachineService";
    private Internal_Db internal_db;

    public InternalMpfMachineService(Context context, int version) {
        this.internal_db = new Internal_Db(context, version);
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
        db.insert("MPF_Machine", null, cv);
        db.close();
    }

    /**
     * fengcheng.ye 2014-7-21
     * ��ȡ������Ϣ
     *
     * @return MpfMachine
     */
    public MpfMachine getMpfMachine() {
        SQLiteDatabase db = internal_db.getReadableDatabase();
        MpfMachine result = null;
        Cursor cursor = db.query("MPF_Machine", new String[]{"DbId", "MachineCode", "MachineSerialNumber", "BindingPassword"}, null, null, null, null, null, null);
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

    public int getinternal_dbID() {
        SQLiteDatabase db = internal_db.getReadableDatabase();
        int dbID = -1;
        Cursor cursor = db.query("MPF_Machine", new String[]{"DbId"}, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            dbID = cursor.getInt(cursor.getColumnIndex("DbId"));
        }
        cursor.close();
        db.close();
        return dbID;
    }

    /**
     * fengcheng.ye 2014-7-21
     * �豸��Ϣ�Ƿ����
     *
     * @Return
     */
    public boolean MpfMachineIsHave() {
        boolean result = false;
        SQLiteDatabase db = internal_db.getReadableDatabase();
        Cursor cursor = db.query("MPF_Machine", new String[]{"DbId", "MachineCode", "MachineSerialNumber", "BindingPassword"}, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            result = true;
        }
        return result;
    }

    public long MpfMachineUpdateBindCode(String bindCodes) {
        Log.i(TAG, bindCodes);
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
            e.printStackTrace();
        }

        cv = null;
        db.close();
        return result;
    }

    public void release() {
        internal_db.close();
    }
}
