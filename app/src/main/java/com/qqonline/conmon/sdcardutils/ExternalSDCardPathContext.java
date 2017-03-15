package com.qqonline.conmon.sdcardutils;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class ExternalSDCardPathContext extends ContextWrapper {

	private String path;
	public ExternalSDCardPathContext(Context base,String path) {
		super(base);
		this.path=path;
	}
	@Override
	public File getDatabasePath(String name) {
		File file=new File(path+File.separator+name);
//		File file=new File("/storage/extsd/MPFCache"+File.separator+name);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		return file;
	}
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		SQLiteDatabase temp=null;
		try {
			temp = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name),factory);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return temp;
	}
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		SQLiteDatabase temp=null;
		try {
			temp = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), factory, errorHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}
}
