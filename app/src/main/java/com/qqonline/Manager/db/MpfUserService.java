package com.qqonline.Manager.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qqonline.db.MPF_Db;
import com.qqonline.domain.MpfPicture;
import com.qqonline.domain.MpfUser;
/**
 *MpdUser绑定用户表表的业务逻辑
 *<br/>基本操作：getUserList,AddMpfUser
 **/
public class MpfUserService {
	private MPF_Db Mpfdb;
	public MpfUserService(Context context,int version)
	{
		this.Mpfdb=new MPF_Db(context,version);
	}
	/**
	 * 返回机器对应的用户列表
	 * @return List<MpfUser>
	 */
	public List<MpfUser> getUserList(){
		List<MpfUser> list = new ArrayList<MpfUser>();
		SQLiteDatabase db = Mpfdb.getReadableDatabase();
		Cursor cursor = db.query("MPF_User", new String[]{" * "}, null, null, null, null, "AddTime");
		while(cursor.moveToNext()){
			MpfUser mu = new MpfUser();
			mu.setDbId(cursor.getInt(cursor.getColumnIndex("DbId")));
			mu.setName(cursor.getString(cursor.getColumnIndex("Name")));
			mu.setOpenId(cursor.getString(cursor.getColumnIndex("OpenId")));
			MpfPicture firstPicture = new MpfPicture();
			firstPicture.setSdCardAdd("");
			Cursor c = db.query("MPF_Picture", new String[]{" * "}, " OpenId=?", new String[]{String.valueOf(cursor.getString(cursor.getColumnIndex("OpenId")))}, null, null, null);
			if(c.moveToFirst())
			{
				firstPicture.setSdCardAdd(c.getString(c.getColumnIndex("SdCardAdd")));
				firstPicture.setPicUrl(c.getString(c.getColumnIndex("PicUrl")));
			}
			mu.setFirstPicture(firstPicture);
			list.add(mu);
		}
		db.close();
		return list;
	}
	/**
	 * 返回用户
	 * @param  OpenId
	 * @return
	 */
	public MpfUser GetUserByOpenId(String OpenId){
		MpfUser mu = new MpfUser();
		SQLiteDatabase db = Mpfdb.getReadableDatabase();
		Cursor cursor = db.query("MPF_User", new String[]{" * "}, "OpenId=?", new String[]{OpenId}, null, null, null, null);
		if(cursor.moveToFirst()){
			mu.setDbId(cursor.getInt(cursor.getColumnIndex("DbId")));
			mu.setName(cursor.getString(cursor.getColumnIndex("Name")));
			mu.setOpenId(cursor.getString(cursor.getColumnIndex("OpenId")));
		}
		db.close();
		return mu;
	}
	public void deleteUser(String openId)
	{
		SQLiteDatabase db = Mpfdb.getReadableDatabase();
		db.delete("MPF_User",  "OpenId=?", new String[]{openId});
		db.close();
	}
	public boolean UserIsHaved(String OpenId){
		boolean Haved = false;
		SQLiteDatabase db = Mpfdb.getReadableDatabase();
		Cursor cursor = db.query("MPF_User", new String[]{"OpenId"}, "OpenId=?", new String[]{OpenId}, null, null, null, null);
		if(cursor.moveToFirst()){
			Haved = true;
		}
		db.close();
		return Haved;
	}
	/**
	 * 添加绑定用户
	 * @param user 添加的用户实例
	 */
	public void AddMpfUser(MpfUser user)
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("DbId", user.getDbId());
		cv.put("OpenId", user.getOpenId());
		cv.put("Name", user.getName());
		cv.put("AddTime", user.getAddTime().toString());
		db.insert("MPF_User", null, cv);
		db.close();
	}
	/**
	 * 添加绑定用户
	 * @param user 添加的用户实例
	 */
	public void AddMpfUser(ArrayList<MpfUser> user)
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		db.beginTransaction();
		try {
			for (int i = 0; i < user.size(); i++) {
				ContentValues cv = new ContentValues();
				cv.put("DbId", user.get(i).getDbId());
				cv.put("OpenId", user.get(i).getOpenId());
				cv.put("Name", user.get(i).getName());
				cv.put("AddTime", user.get(i).getAddTime().toString());
				db.insert("MPF_User", null, cv);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			db.endTransaction();
		}
		db.close();
	}
	/**
	 * 更新用户的名称
	 */
	public void updatempfUser(String Name,String OpenId)
	{
		SQLiteDatabase db = Mpfdb.getWritableDatabase();
		db.execSQL("update MPF_User set Name=? where OpenId=?", new String[]{Name,OpenId});
		db.close();
	}
	public void release()
	{
		Mpfdb.close();
	}
}
