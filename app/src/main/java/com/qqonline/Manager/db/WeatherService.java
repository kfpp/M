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
 * ����������
 * @author YE
 *
 */
public class WeatherService {
	private final static String DBName="WeatherInfo";
	private MPF_Db db;
	/**
	 * ����ʵ�����һ��ʵ��
	 */
	private Weather _weather;
	/**
	 * Ҫ���漸�������
	 */
	private int _saveDays;
	/**
	 * Ҫ����������JSON
	 */
	private String _jsonString;
	
	public WeatherService(Context context,int Version) {		
		db=new MPF_Db(context, Version);
		this._saveDays=3;
		_weather=new Weather(_saveDays);
	}
	/**
	 * ��ʼ�����ݣ�����ʼ��������
	 * @param jsonString Ҫ����������
	 * @param saveDays Ҫ���漸�����������
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
	 * ����JSON����
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
	 * ��������ɵ�����д��ʵ����
	 * @param weatherObj ������ɺ��JSON����
	 */
	private void SetDate(JSONObject weatherObj)
	{
		try {
			_weather.set_city(weatherObj.getString("city"));
//			String lunarCalendar=weatherObj.getString("date");
			Calendar today=Calendar.getInstance();
			LunarUtil lunar=new LunarUtil(today);
//			int index=lunarCalendar.indexOf("��");
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
	 * ����������Ϣ�����ݿ�
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
	 * �����ݿ��ȡ��������
	 * @return �ɹ��򷵻�����ʵ�壬ʧ��������������򷵻�null�����������ݳ���3Сʱ���ʾ���ڣ�
	 * @throws Exception 
	 */
	public Weather getWeatherFromDatebase(String city) throws Exception {
		if (city == null) {
			//TODO:�����cityֵ��������ʱ��ᴫ��NULLֵ�����飡
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
	 * �жϸó��м�¼�Ƿ����
	 * @param ������
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
	 * ��Ӽ�¼
	 * @param weather ��¼ʵ��
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
		sbTemperature.deleteCharAt(sbTemperature.length()-1); //ȥ�����һ������
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
	 * ���¼�¼
	 * @param weather ��¼ʵ��
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
		sbTemperature.deleteCharAt(sbTemperature.length()-1); //ȥ�����һ������
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
	 * �ж����������Ƿ��ѹ�ʱ
	 * @param weather Ҫ�ж���ʵ��
	 * @return true:ĩ��ʱ��false���ѹ�ʱ
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
	 * �ⲿ�ӿڣ������ѱ������������ݵ�����ʵ����
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
