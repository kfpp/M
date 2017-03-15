package com.qqonline.domain;

public class Weather {

	private String _city;
	private String _updateData;
	private String _lunarCalendar;
	private String[] _temp;
	private String[] _weather;
	
	public Weather(int saveDays) {
		_temp=new String[saveDays];
		_weather=new String[saveDays];
	}
	public String get_city() {
		return _city;
	}
	public void set_city(String _city) {
		this._city = _city;
	}
	public String get_updateData() {
		return _updateData;
	}
	public void set_updateData(String _updateData) {
		this._updateData = _updateData;
	}
	public String get_lunarCalendar() {
		return _lunarCalendar;
	}
	public void set_lunarCalendar(String _lunarCalendar) {
		this._lunarCalendar = _lunarCalendar;
	}
	public String[] get_temp() {
		return _temp;
	}
	public void set_temp(String[] _temp) {
		this._temp = _temp;
	}
	public String[] get_weather() {
		return _weather;
	}
	public void set_weather(String[] _weather) {
		this._weather = _weather;
	}

	
}
