package com.qqonline.conmon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import android.R.integer;

public class MyJSONUtil {

	private String _jsonString;
	private StringBuilder _values;
	public MyJSONUtil(String jsonStr) {
		_jsonString=jsonStr;
		_values=new StringBuilder();
	}
	public Map<String, String > Deal()
	{
		JSONObject jsonObj=null;
		Map<String, String >map=null;
		try {
			jsonObj=new JSONObject(_jsonString);
			map= ParseJsonToMap(jsonObj, _values);
		} catch (Exception e) {
			return null;
		}
		return map;
	}
	public String[] GetValues()
	{
		String temp=_values.toString();
		String[] tempArr=temp.split(",");
		return tempArr;
		
	}
	private static Map<String, String> ParseJsonToMap(JSONObject jsonObject,StringBuilder sb)
	{
		Map<String, String> map=new HashMap<String, String>();
		Iterator it=jsonObject.keys();
		while (it.hasNext()) {
			try {
				String key=String.valueOf(it.next());
				String value=(String)jsonObject.get(key);
				sb.append(value);
				sb.append(",");
				map.put(value, key);
			} catch (Exception e) {
				return null;
			}
			
		}
		sb.deleteCharAt(sb.length()-1);
		return map;
		
	}
}
