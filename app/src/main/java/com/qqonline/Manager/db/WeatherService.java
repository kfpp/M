package com.qqonline.Manager.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.LunarUtil;
import com.qqonline.db.MPF_Db;
import com.qqonline.domain.Weather;

/**
 * 天气工具类
 * @author YE
 *
 */
public class WeatherService {
	private final static String DBName="WeatherInfo";
	private MPF_Db db;
	/**
	 * 天气实体类的一个实例
	 */
	private Weather _weather;
	/**
	 * 要保存几天的天气
	 */
	private int _saveDays;
	/**
	 * 要解析的天气JSON
	 */
	private String _jsonString;
	
	public WeatherService(Context context,int Version) {		
		db=new MPF_Db(context, Version);
		this._saveDays=3;
		_weather=new Weather(_saveDays);
	}
	/**
	 * 初始化数据，并开始解析数据
	 * @param jsonString 要解析的数据
	 * @param saveDays 要保存几天的天气数据
	 */
	public WeatherService(Context context,String jsonString,int saveDays,int Version) {
		db=new MPF_Db(context, Version);
		this._jsonString=jsonString;		
		this._saveDays=saveDays;
		if (_saveDays > 6) {
			_saveDays=6;
		}
		_weather=new Weather(_saveDays);
		
	//	AnalizeJson();
	}
	/**
	 * 解析JSON数据
	 */
	private void AnalizeJson()
	{
		JSONObject jsonObject=null,weatherObject=null;
		if (_jsonString==null || _jsonString.equals("")) {
			return;
		}
		try {
			jsonObject=new JSONObject(_jsonString);
			weatherObject=jsonObject.getJSONObject("data");
		} catch (Exception e) {
			return;
		}
		if (weatherObject == null) {
			return;
		}
		SetDate(weatherObject);
	}
	/**
	 * 将解析完成的数据写入实体类
	 * @param weatherObj 解析完成后的JSON对象
	 */
	private void SetDate(JSONObject weatherObj)
	{
		try {
			_weather.set_city(weatherObj.getString("city"));
//			String lunarCalendar=weatherObj.getString("date");
			Calendar today=Calendar.getInstance();
			LunarUtil lunar=new LunarUtil(today);
//			int index=lunarCalendar.indexOf("（");
//			if (index > 0) {
//				_weather.set_lunarCalendar(lunarCalendar.substring(0, index));
//			}
//			else {
//				_weather.set_lunarCalendar(lunarCalendar);
//			}
			_weather.set_lunarCalendar(lunar.toString());
//			_weather.set_weekDay(weatherObj.getString("week"));
			_weather.set_updateData(weatherObj.getString("date_1"));
			String[] temp=new String[_saveDays];
			for (int i = 0; i < _saveDays; i++) {
				String tempNum="temp_"+(i+1);
				temp[i]=weatherObj.getString(tempNum);			
			}
			_weather.set_temp(temp);
			String[] weather=new String[_saveDays];
			for (int i = 0; i < _saveDays; i++) {
				String tempNum="weather_"+(i+1);
				weather[i]=weatherObj.getString(tempNum);
			}
			_weather.set_weather(weather);
			setWeatherToDatebase(_weather);
		} catch (Exception e) {
			return;
		}
	}
	/**
	 * 保存天气信息到数据库
	 * @param weather
	 */
	public void setWeatherToDatebase(Weather weather)
	{
		if (db == null) {
			return;
		}
		if (isCityExist(weather.get_city())) {
			update(weather);
		}
		else {
			addDate(weather);
		}
		_weather=weather;
	}
	/**
	 * 从数据库获取天气数据
	 * @return 成功则返回天气实体，失败则或天气过期则返回null（当天气数据超过3小时则表示过期）
	 * @throws Exception 
	 */
	public Weather getWeatherFromDatebase(String city) throws Exception {
		if (city == null) {
			//TODO:这里的city值在启动的时候会传进NULL值，请检查！
			return null;
		}
		SQLiteDatabase sdb = db.getReadableDatabase();
		Cursor c = sdb.query(DBName, null, "City=?", new String[]{city}, null, null, null);
		if (!c.moveToFirst()) {
			sdb.close();
			return null;
		} else {
			String date = c.getString(c.getColumnIndex("ClientUpdateDate"));
			if (date.equals("-1")) {
				sdb.close();
				return null;
			}
			boolean isOut = isRecordOutDated(date);
			if (!isOut) {
				sdb.close();
				return null;
			} else {
				Weather temp = new Weather(_saveDays);
				temp.set_city(c.getString(c.getColumnIndex("City")));
				temp.set_lunarCalendar(new LunarUtil(Calendar.getInstance()).toString());
				temp.set_temp(c.getString(c.getColumnIndex("Temperature")).split(","));
				temp.set_weather(c.getString(c.getColumnIndex("Weather")).split(","));
				temp.set_updateData(c.getString(c.getColumnIndex("ServerUpdateDate")));
				sdb.close();
				return temp;
			}
		}
//		return null;
	}
	/**
	 * 判断该城市记录是否存在
	 * @param 城市名
	 * @return 
	 */
	private boolean isCityExist(String city)
	{
		SQLiteDatabase sdb=db.getReadableDatabase();
		Cursor c=sdb.query(DBName, new String[]{" * "}, "City=?", new String[]{city}, null, null, null);
		if (c.moveToFirst()) {
			sdb.close();
			return true;
		}
		sdb.close();
		return false;
	}
	/**
	 * 添加记录
	 * @param weather 记录实体
	 */
	private void addDate(Weather weather)
	{
		String city=weather.get_city();
		SQLiteDatabase sdb=db.getWritableDatabase();
		ContentValues cv=new ContentValues();
		StringBuilder sbWeather,sbTemperature;
		sbTemperature=new StringBuilder();
		sbWeather=new StringBuilder();
		for (int i = 0; i < weather.get_temp().length; i++) {
			sbTemperature.append(weather.get_temp()[i]+",");
			sbWeather.append(weather.get_weather()[i]+",");
		}
		sbTemperature.deleteCharAt(sbTemperature.length()-1); //去掉最后一个逗号
		sbWeather.deleteCharAt(sbWeather.length()-1);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date=sdf.format(Calendar.getInstance().getTime());
		cv.put("City", city);
		cv.put("ServerUpdateDate", weather.get_updateData());
		cv.put("Weather", sbWeather.toString());
		cv.put("Temperature", sbTemperature.toString());
		cv.put("ClientUpdateDate", date);
		sdb.insert(DBName, null, cv);
		sdb.close();
	}
	/**
	 * 更新记录
	 * @param weather 记录实体
	 */
	private void update(Weather weather)
	{
		String city=weather.get_city();
		SQLiteDatabase sdb=db.getWritableDatabase();
		ContentValues cv=new ContentValues();
		StringBuilder sbWeather,sbTemperature;
		sbTemperature=new StringBuilder();
		sbWeather=new StringBuilder();
		for (int i = 0; i < weather.get_temp().length; i++) {
			sbTemperature.append(weather.get_temp()[i]+",");
			sbWeather.append(weather.get_weather()[i]+",");
		}
		sbTemperature.deleteCharAt(sbTemperature.length()-1); //去掉最后一个逗号
		sbWeather.deleteCharAt(sbWeather.length()-1);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date=sdf.format(Calendar.getInstance().getTime());
		cv.put("City", city);
		cv.put("ServerUpdateDate", weather.get_updateData());
		cv.put("Weather", sbWeather.toString());
		cv.put("Temperature", sbTemperature.toString());
		cv.put("ClientUpdateDate", date);
		sdb.update(DBName, cv, "City=?", new String[]{city});
		sdb.close();
	}
	/**
	 * 判断天气数据是否已过时
	 * @param weather 要判断是实体
	 * @return true:末过时，false，已过时
	 * @throws Exception 
	 */
	private boolean isRecordOutDated(String weatherDate) throws Exception
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date old=sdf.parse(weatherDate);
		Date now=Calendar.getInstance().getTime();
		long timeLong=now.getTime()-old.getTime();
		if (timeLong < 1000 * 60 * 60 * DATA.AutoUpdataWeatherIntevalHours) {
			return true;
		}
		return false;
		
	}
	public void setCurrentCity(String cityName)
	{
		if(cityName == null || cityName.trim().equals(""))
		{
			return ;
		}
		SQLiteDatabase sdb=db.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("IsCurrentCity", false);
		sdb.update(DBName, cv, null, null);
		cv=new ContentValues();
		cv.put("IsCurrentCity", true);
		sdb.update(DBName, cv, "City=?", new String[]{cityName});
		sdb.close();
	}
	/**
	 * 外部接口，返回已保存了天气数据的天气实体类
	 * @return
	 */
	public Weather GetEntity()
	{
		return _weather;
	}
	public void release() {
		if (db != null) {
			db.close();
		}
	}
}
