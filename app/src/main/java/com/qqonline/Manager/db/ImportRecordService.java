package com.qqonline.Manager.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qqonline.db.MPF_Db;
import com.qqonline.domain.ImportRecord;

public class ImportRecordService {

	private MPF_Db mpfDb;
	public ImportRecordService(Context context,int version) {
		mpfDb=new MPF_Db(context, version);
	}
	public long addRecord(ImportRecord record)
	{
		SQLiteDatabase db=mpfDb.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(ImportRecord.PATH, record.getPath());
		cv.put(ImportRecord.COUNT, record.getCount());
		long count=0;
		try {
			count= db.insert(ImportRecord.TABLENAME, null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
		return count;
	}
	public long addRecord(ArrayList<ImportRecord> recordList)
	{
		SQLiteDatabase db=mpfDb.getWritableDatabase();
		long count=0;
		db.beginTransaction();
		try {
			for (int i = 0; i < recordList.size(); i++) {
				ContentValues cv=new ContentValues();
				cv.put(ImportRecord.PATH, recordList.get(i).getPath());
				cv.put(ImportRecord.COUNT, recordList.get(i).getCount());				
				count += db.insert(ImportRecord.TABLENAME, null, cv);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			db.endTransaction();		
		}				
		db.close();
		return count;
	}

	public boolean hasRecord(String path) {
		boolean exist = false;
		SQLiteDatabase db = mpfDb.getReadableDatabase();
		Cursor cursor = db.query(ImportRecord.TABLENAME, new String[]{ImportRecord.PATH}, ImportRecord.PATH+"=?", new String[]{path}, null, null, null, null);
		if(cursor.moveToFirst()){
			exist = true;
		}
		db.close();
		return exist;
	}
	public int deleteRecord(String path)
	{
		SQLiteDatabase db=mpfDb.getWritableDatabase();
		int count=0;
		try {
			count=db.delete(ImportRecord.TABLENAME, ImportRecord.PATH+"=?", new String[]{path});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return count;
	}
	public int updateRecord(ImportRecord newRecord)
	{
		
		SQLiteDatabase db=mpfDb.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(ImportRecord.PATH, newRecord.getPath());
		cv.put(ImportRecord.COUNT, newRecord.getCount());
		int count=0;
		try {
			count= db.update(ImportRecord.TABLENAME, cv, ImportRecord.PATH+"=?", new String[]{newRecord.getPath()});
		} catch (Exception e) {
			e.printStackTrace();
		}
		db.close();
		return count;
	}
	public ArrayList<ImportRecord> getAllRecord()
	{
		ArrayList<ImportRecord> dataList=new ArrayList<ImportRecord>();
		SQLiteDatabase db=mpfDb.getWritableDatabase();
		Cursor c= db.query(ImportRecord.TABLENAME, new String[]{ "*"}, null, null, null, null, null);
		while (c.moveToNext()) {
			ImportRecord record=new ImportRecord();
			String path=c.getString(c.getColumnIndex(ImportRecord.PATH));
			int count=c.getInt(c.getColumnIndex(ImportRecord.COUNT));
			record.setPath(path);
			record.setCount(count);
			dataList.add(record);			
		}
		c.close();
		db.close();
		return dataList;
	}
}
