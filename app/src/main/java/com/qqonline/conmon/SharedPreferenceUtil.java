package com.qqonline.conmon;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.qqonline.mpf.R;

/**
 * 配置信息工具类
 * @author YE
 *
 */
public class SharedPreferenceUtil {
	private static SharedPreferences sharedPreference;
	public static SharedPreferences getDefaultSharedPreferences(Context context) {
		if (sharedPreference == null) {
			String preFileName = context.getResources().getStringArray(
					R.array.PreferenceFileNameArray)[0];
			sharedPreference = context.getSharedPreferences(preFileName,
					Context.MODE_PRIVATE);
		}
		return sharedPreference;
	}
	/**
	 * 设置多组值对
	 * @param sf
	 * @param keys
	 * @param values
	 */
	public static void setPreference(SharedPreferences sf,String[] keys,String[] values)
	{
		Editor editor=sf.edit();
		if (keys.length <1 || values.length <1 || keys.length != values.length) {
			return;
		}
		for (int i = 0; i < values.length; i++) {
			editor.putString(keys[i], values[i]);
		}
		editor.commit();
	}
	/**
	 * 设置单个值对
	 * @param sf
	 * @param key
	 * @param value
	 */
	public static void setPreference(SharedPreferences sf,String key,String value)
	{
		if (key == null || value == null) {
			return;
		}
		Editor editor=sf.edit();		
		editor.putString(key, value);
		editor.commit();
	}
	/**
	 * 获取全部参数数据
	 * @param sf
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, String>  getPreference(SharedPreferences sf) throws Exception
	{
		HashMap<String, String> temp=null;
		temp=(HashMap<String, String>) sf.getAll();
		return temp;
		
	}
	/**
	 * 获取多个参数数据
	 * @param sf
	 * @param keys
	 * @return
	 */
	public static Map<String, String>  getPreference(SharedPreferences sf,String[] keys)
	{
		if (keys==null) {
			return null;
		}
		Map<String, String> temp=new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			String value=sf.getString(keys[i], null);
			temp.put(keys[i], value);
		}
		return temp;
	}
	public static String  getPreference(SharedPreferences sf,String key)
	{
		if (key==null) {
			return null;
		}
		String value=sf.getString(key, null);

		return value;
	}
}
