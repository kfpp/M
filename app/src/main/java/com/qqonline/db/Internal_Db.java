package com.qqonline.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 * Created by Administrator on 2015/7/21 0021.
 */
public class Internal_Db extends SQLiteOpenHelper {
    public Internal_Db(Context context, int version) {
        super(context, "Internal_Db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table MPF_Machine(ID integer primary key autoincrement,DbId int,MachineCode varchar(128) ,MachineSerialNumber varchar(50),BindingPassword varchar(128))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
