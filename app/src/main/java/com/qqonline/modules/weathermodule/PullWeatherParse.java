package com.qqonline.modules.weathermodule;


import android.util.Xml;

import com.qqonline.conmon.LunarUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PullWeatherParse {

	public static Map<String,String> parse(InputStream input) throws Exception {
		final InputStream is=input;
		final int saveDays=3;
		Map<String,String> map=null;
		String cityName = null;
		boolean isDay=false;
		int currentDay=0;
		String tempHigh=null;
		String tempLow=null;
		String[] temp=new String[saveDays];
		String[] weathers=new String[saveDays];
		
		XmlPullParser parse = Xml.newPullParser();
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
					map=new HashMap<String ,String>();
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
					map.put("city", cityName);
					map.put("temp0",temp[0]);
					map.put("temp1",temp[1]);
					map.put("temp2",temp[2]);
					map.put("weather0",weathers[0]);
					map.put("weather1",weathers[1]);
					map.put("weather2",weathers[2]);
					Calendar today=Calendar.getInstance();
					LunarUtil lunar=new LunarUtil(today);
					map.put("lunar", lunar.toString());
					map.put("data", new Date().toString());
					return map;
				}
				break;
			case XmlPullParser.END_DOCUMENT:break;
			default:
				break;
			}
			eventType=parse.next();
		}
		input.close();
		return map;
	}

}
