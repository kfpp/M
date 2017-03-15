package com.qqonline.Manager.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qqonline.db.MPF_Db;
import com.qqonline.domain.ShieldInfo;

public class ShieldListService {

	
	private MPF_Db db;
	public ShieldListService(Context context,int version) {
		db=new MPF_Db(context, version);
	}
	public long addShield(final ShieldInfo shield)
	{
		final String openId=shield.getOpenId();
		final SQLiteDatabase sql=db.getWritableDatabase();
		if (openId == null || openId.trim().equals("")) {
			return -1;
		}
		ContentValues cv=new ContentValues();
		cv.put(ShieldInfo.OPENID, openId);
		long result= sql.insert(ShieldInfo.SHIELDLIST, null, cv);
		sql.close();
		return result;
	}
	public long deleteShield(final ShieldInfo shield)
	{
		final String openId=shield.getOpenId();
		final SQLiteDatabase sql=db.getWritableDatabase();
		final String where=ShieldInfo.OPENID+" = ?";
		if (openId == null || openId.trim().equals("")) {
			return -1;
		}
		long result= sql.delete(ShieldInfo.SHIELDLIST, where, new String[]{openId});
		sql.close();
		return result;
	}
	public List<ShieldInfo> getAllShiledInfo()
	{
		final SQLiteDatabase sql=db.getWritableDatabase();
		final List<ShieldInfo> shieldList=new ArrayList<ShieldInfo>();
		Cursor c= sql.query(ShieldInfo.SHIELDLIST, new String[]{ "*" }, null, null, null, null, null);
		while (c.moveToNext()) {
			final String openId=c.getString(c.getColumnIndex(ShieldInfo.OPENID));
			final ShieldInfo shiledTemp=new ShieldInfo();
			shiledTemp.setOpenId(openId);
			shieldList.add(shiledTemp);
		}
		c.close();
		sql.close();
		return shieldList;
	}
	public List<String> getAllShiledOpenId()
	{
		final SQLiteDatabase sql=db.getWritableDatabase();
		final List<String> shieldList=new ArrayList<String>();
		Cursor c= sql.query(ShieldInfo.SHIELDLIST, new String[]{ "*" }, null, null, null, null, null);
		while (c.moveToNext()) {
			final String openId=c.getString(c.getColumnIndex(ShieldInfo.OPENID));
			shieldList.add(openId);
		}
		c.close();
		sql.close();
		return shieldList;
	} 
	public void release()
	{
		db.close();
	}
}
