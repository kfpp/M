package com.qqonline.db;


import com.qqonline.conmon.sdcardutils.ExternalSDCardPathContext;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//数据库
public class MPF_Db extends SQLiteOpenHelper {

    protected static final String TAG = "MPF_Db";
    public MPF_Db(Context context, int Version) {
        super(new ExternalSDCardPathContext(context, getExternalSDCardPath()), "MPF.db", null, Version);    //自定义Context类，主要自定了数据库的保存路径
    }

    private static String getExternalSDCardPath() {
        return GetExtSDCardPath.getSDCardPath();
    }

    //第一次执行
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table MPF_Machine(ID integer primary key autoincrement,DbId int,MachineCode varchar(128) ,MachineSerialNumber varchar(50),BindingPassword varchar(128))");
        db.execSQL("create table MPF_User(ID integer primary key autoincrement,DbId int,OpenId varchar(128) ,Name varchar(50),AddTime DATETIME)");
        db.execSQL("create table MPF_Picture(ID integer primary key autoincrement,DbId int,OpenId varchar(128),PicUrl varchar(128),SdCardAdd varchar(128),AddTime DATETIME,MediaId varchar(128))");
        db.execSQL("create table WeatherInfo(ID integer primary key autoincrement,City varchar(20),ServerUpdateDate DATETIME,Temperature varchar(20),Weather varchar(20),ClientUpdateDate DATETIME,IsCurrentCity bool)");
        db.execSQL("create table ImportRecord(ID integer primary key autoincrement,Path varchar(128),Count integer)");
        db.execSQL("create table ShieldList(ID integer primary key autoincrement,OpenId varchar(128))");
    }

    //对比版本不同执行
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion + 1) {
            case 2:
            case 3:
                db.execSQL("drop table if exists WeatherInfo");
                db.execSQL("create table WeatherInfo(ID integer primary key autoincrement,City varchar(20),ServerUpdateDate DATETIME,Temperature varchar(20),Weather varchar(20),ClientUpdateDate DATETIME)");
            case 4:
                db.execSQL("drop table if exists ImportRecord");
                db.execSQL("create table ImportRecord(ID integer primary key autoincrement,Path varchar(128),Count integer)");
            case 5:
                db.execSQL("drop table if exists ShieldList");
                db.execSQL("create table ShieldList(ID integer primary key autoincrement,OpenId varchar(128))");
            case 6:
                db.execSQL("drop table if exists WeatherInfo");
                db.execSQL("create table WeatherInfo(ID integer primary key autoincrement,City varchar(20),ServerUpdateDate DATETIME,Temperature varchar(20),Weather varchar(20),ClientUpdateDate DATETIME,IsCurrentCity bool)");
            case 7:
            case 8:
                /**
                 * 修复在更新时添加字段，而创建时没有加上的Bug
                 */
                update7(db);
                break;
            default:
                break;
        }
    }
    private void update7(SQLiteDatabase db) {
        if (!isColumnExist(db, "MPF_Picture", "MediaId")) {
            db.execSQL("ALTER TABLE MPF_Picture  ADD COLUMN MediaId varchar(128)");
        }
    }

    private boolean isColumnExist(SQLiteDatabase db,String tableName, String columnName) {
        boolean result = false ;
        Cursor cursor = null ;
        try{
            //查询一行
            cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0"
                    , null );
            result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
        }catch (Exception e){
            Log.e(TAG, "checkColumnExists1..." + e.getMessage()) ;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }

        return result ;
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
