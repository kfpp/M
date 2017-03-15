package com.qqonline.conmon;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.qqonline.domain.Weather;
import com.qqonline.interfaces.IXmlParse;

public class PullWeatherParse implements IXmlParse {

	@Override
	public Weather parse(InputStream input) throws Exception {
		final InputStream is=input;
		final int saveDays=3;
		Weather weather=null;
		String cityName = null;
		boolean isDay=false;
		int currentDay=0;
		String tempHigh=null;
		String tempLow=null;
		String[] temp=new String[saveDays];
		String[] weathers=new String[saveDays];
		
		XmlPullParser parse =Xml.newPullParser();
		parse.setInput(is,"utf-8");
		
		int eventType=parse.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (parse.getName().equals("city")) {
					cityName=parse.nextText();
				}
				else if(parse.getName().equals("weather")){
					weather=new Weather(saveDays);
				}
				else if (parse.getName().equals("high")) {
					tempHigh=parse.nextText();
				}
				else if (parse.getName().equals("low")) {
					tempLow=parse.nextText();
				}
				else if (parse.getName().equals("day")) {
					isDay=true;
				}
				else if (parse.getName().equals("type") && isDay && currentDay < saveDays) {
					weathers[currentDay]=parse.nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				if (parse.getName().equals("day")) {
					isDay=false;
				}
				else if (parse.getName().equals("weather") && currentDay < saveDays) {
					temp[currentDay]=tempLow.split(" ")[1]+" ~ "+tempHigh.split(" ")[1];									
					currentDay++;
					
				}
				else if(parse.getName().equals("resp"))
				{
					weather.set_city(cityName);	
					weather.set_temp(temp);
					weather.set_weather(weathers);
					Calendar today=Calendar.getInstance();
					LunarUtil lunar=new LunarUtil(today);
					weather.set_lunarCalendar(lunar.toString());
					weather.set_updateData(new Date().toString());
					return weather;
				}
				break;
			case XmlPullParser.END_DOCUMENT:break;
			default:
				break;
			}
			eventType=parse.next();
		}
		input.close();
		return weather;
	}

}
