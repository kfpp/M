package com.qqonline.Manager.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.qqonline.db.MPF_Db;
import com.qqonline.domain.MpfPicture;

/**
 *MPF_Picture��Ƭ���ҵ���߼�
 *<br/>����������
 **/
public class MpfPictureService {
	private final static String TAG="MpfPictureService";
	private MPF_Db Mpfdb;
	public MpfPictureService(Context context,int version)
	{
		this.Mpfdb=new MPF_Db(context,version);
	}
	/**
	 * ��ȡ��Ӧ�˺ŵ�ͼƬ
	 * @param OpenId ��Ӧ�˵�OpenId��Ϊ�ն�ȡȫ����Ƭ
	 * @return List<MpfPicture>
	 */
	public List<MpfPicture> getPicture(String OpenId){
		if(OpenId == null) return null;
		List<MpfPicture> list = new ArrayList<MpfPicture>();
		SQLiteDatabase db = Mpfdb.getReadableDatabase();
		Cursor cursor;
		if(OpenId.equals("")){
			cursor = db.query("MPF_Picture", new String[]{" * "}, null, null, null, null, "DbId desc",null);
		}
		else
		{
			cursor = db.query("MPF_Picture", new String[]{" * "}, " OpenId=?", new String[]{OpenId}, null, null, "DbId desc",null);
		}
		while(cursor.moveToNext()){
			MpfPicture pic = new MpfPicture();
			if(cursor.getString(cursor.getColumnIndex("PicUrl")).equals("")){
				//Ϊ�������
			}
			else{
				pic.setDbId(cursor.getInt(cursor.getColumnIndex("DbId")));
				pic.setSdCardAdd(cursor.getString(cursor.getColumnIndex("SdCardAdd")));
				pic.setPicUrl(cursor.getString(cursor.getColumnIndex("PicUrl")));
				pic.setOpenId(cursor.getString(cursor.getColumnIndex("OpenId")));
				int mediaIdIndex = cursor.getColumnIndex("MediaId");
				if (mediaIdIndex > 0) {
					pic.setMediaId(cursor.getString(mediaIdIndex));
				}
				list.add(pic);
			}
		}
		db.close();
		return list;
	}
	public String getPictureOpenIDByName(String name)
	{
		SQLiteDatabase db = Mpfdb.getReadableDatabase();
		Cursor c=db.query("MPF_Picture", new String[]{"*"}, "PicUrl=?", new String[]{name}, null, null, null);
		String openId=null;
		if (c.moveToFirst()) {
			openId=c.getString(c.getColumnIndex("OpenId"));
		}
		db.close();
		return openId;
	}
	public int getPictureCount(String openId)
	{
		SQLiteDatabase db = Mpfdb.getReadableDatabase();
		int count=0;
		Cursor c=null;
		if(openId == null || openId.equals(""))
		{
			c=db.query("MPF_Picture", new String[]{"count(*)"}, null, null, null, null, null,null);
		}
		else {
			c=db.query("MPF_Picture", new String[]{"count(*)"}, " OpenId=?", new String[]{openId}, null, null, null,null);
		}
		if(c.moveToFirst())
		{
			count=c.getInt(0);
		}
		db.close();
		return count;
	}
	/**
	 * �����Ƭ
	 * @param pic ��ӵ���Ƭʵ��
	 */
	public void AddMpfPicture(MpfPicture pic)
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		try {
			ContentValues cv = new ContentValues();
			cv.put("DbId", pic.getDbId());
			cv.put("OpenId",pic.getOpenId());
			cv.put("PicUrl", pic.getPicUrl());
			cv.put("SdCardAdd", pic.getSdCardAdd());
			cv.put("AddTime", pic.getAddTime().toString());
			if (pic.getMediaId() != null) {
				cv.put("MediaId",pic.getMediaId());
			}
			db.insert("MPF_Picture", null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			db.close();
		}
		
	}
	/**
	 * �����Ƭ
	 * @param pic ��ӵ���Ƭʵ��
	 * @param isStop ��־�Ƿ�ֹͣ���ع�
	 */
	public void AddMpfPicture(ArrayList<MpfPicture>  pictureList,Boolean isStop)
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		db.beginTransaction();		 
		try {
			for (int i = 0; i < pictureList.size(); i++) {
				if (isStop.booleanValue()) {
					Log.i(TAG,"��������ͼƬֹͣ,��ǰ���뵽�� "+i+" ��");
					break;
				}
				ContentValues cv = new ContentValues();
				cv.put("DbId", pictureList.get(i).getDbId());
				cv.put("OpenId",pictureList.get(i).getOpenId());
				cv.put("PicUrl", pictureList.get(i).getPicUrl());
				cv.put("SdCardAdd", pictureList.get(i).getSdCardAdd());
				cv.put("AddTime", pictureList.get(i).getAddTime().toString());
				db.insert("MPF_Picture", null, cv);
			}		
			if (!isStop.booleanValue()) {
				db.setTransactionSuccessful();
			}			
			else {
				Log.i(TAG,"��������ͼƬֹͣ,����ع�");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
		
	}
	/**
	 * ��ȡ���һ����¼��ID
	 * @return
	 */
	public int getLastRecordID()
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		//db.delete("MPF_Picture", " DbId = ?", new String[]{String.valueOf(id)});
		Cursor c= db.query("MPF_Picture", new String[]{"ID"}, null, null, null, null, "ASC",null);
		if (c.moveToLast()) {
			int id=c.getInt(0);
			c.close();
			db.close();
			return id;
		}
		c.close();
		db.close();
		return 0;
	}	
	/**
	 * ��ȡ���һ����¼
	 * @return
	 */
	public MpfPicture getLastRecord()
	{
		SQLiteDatabase db = Mpfdb.getReadableDatabase();
		//db.delete("MPF_Picture", " DbId = ?", new String[]{String.valueOf(id)});
		Cursor c= db.query("MPF_Picture", null, null, null, null, null, " ID",null);
		if (c.moveToLast()) {
			MpfPicture pic = new MpfPicture();
			if(c.getString(c.getColumnIndex("PicUrl")).equals("")){
				return null;
			}
			else{
				pic.setDbId(c.getInt(c.getColumnIndex("DbId")));
				pic.setSdCardAdd(c.getString(c.getColumnIndex("SdCardAdd")));
				pic.setPicUrl(c.getString(c.getColumnIndex("PicUrl")));
			}
			c.close();
			db.close();
			return pic;
		}
		c.close();
		db.close();
		return null;
	}

	/**
	 * ����DbIdɾ��ͼƬ��¼
	 * @param id DbId
	 */
	public void delMpfPicture(int id)
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		db.delete("MPF_Picture", " DbId = ?", new String[]{String.valueOf(id)});
		db.close();
	}

	/**
	 * ����OpenIdɾ��ͼƬ��¼
	 * @param openId OpenId
	 */
	public void delMpfPicture(String openId)
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		db.delete("MPF_Picture", " OpenId = ?", new String[]{openId});
		db.close();
	}
	public void delMpfPictureByPicUrl(String picUrl)
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		db.delete("MPF_Picture", " PicUrl = ?", new String[]{picUrl});
		db.close();
	}
//	public void delMpfPciture(int[] id)
//	{
//		SQLiteDatabase db = Mpfdb.getWritableDatabase();
//		db.beginTransaction();
//		for (int i = 0; i < id.length; i++) {
//			db.delete("MPF_Picture", " ID = ?", new String[]{String.valueOf(id[i])});
//		}		
//		db.endTransaction();
//		db.close();
//	}
	public void deleteAllPicture()
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		db.delete("MPF_Picture", null, null);
		db.close();
	}
	public void release()
	{
		Mpfdb.close();
	}
}
