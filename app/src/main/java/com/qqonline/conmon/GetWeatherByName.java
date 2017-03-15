package com.qqonline.conmon;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;

import com.qqonline.conmon.net.GetByHttpClient;
import com.qqonline.conmon.net.GetByHttpUrlConnection;
import com.qqonline.domain.Weather;
import com.qqonline.interfaces.GetWeather;
import com.qqonline.interfaces.IUrlGetable;
import com.qqonline.interfaces.IXmlParse;
import com.qqonline.mpf.R;
import com.qqonline.Manager.db.WeatherService;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.util.EntityUtils;

public class GetWeatherByName  implements GetWeather {

	private static final String TAG="GetWeatherByName";
	public static final String BUNDLE_KEY_CITYNAME="cityName";
	private String cityName=null;
	private WeatherService service;
	private AsyncGetWeatherByName get;
	private MPFApp app;

	public void setCityName(String cityName)
	{
		this.cityName=cityName;
	}
	@Override
	public void AsyncGet(Activity activit) {
		app=MPFApp.getInstance();
		service=new WeatherService(app.getApplicationContext(), DATA.DATAVERSION);
		get=new AsyncGetWeatherByName();
		String cityName=null;
		if (this.cityName == null) {
			cityName = app.get_cityNameCache();
		}
		else {
			cityName=this.cityName;
		}
		Weather weather = null;
		try {
			weather = service.getWeatherFromDatebase(cityName); // �鿴��ݿ���û�ж�Ӧ���еĿ������
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (weather != null) {
			saveAndSendBoardcast(weather);
		} else {
			get.execute(DATA.WeatherByCityName.replace("%s1", URLEncoder.encode(cityName)));
		}

	}

	@Override
	public void setParams(Intent intent) {
		String cityName=intent.getExtras().getString(BUNDLE_KEY_CITYNAME,null);
		if (cityName != null) {
			this.cityName=cityName;
		}
	}

	private void saveWeather(String xmlString)
	{
		InputStream is=new ByteArrayInputStream(xmlString.getBytes());
		IXmlParse parse=new PullWeatherParse();
		Weather weather=null;
		try {
			weather=parse.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (weather == null) {
			return ;
		}
		service.setWeatherToDatebase(weather);
		saveAndSendBoardcast(weather);
		Toast.makeText(app.getActivityList().get(0), R.string.weather_updata,
				 Toast.LENGTH_SHORT).show();
		app.set_weatherInfoCache(weather);
		  Intent intent=new Intent();
		  intent.setAction(DATA.BroadcastPicClockActionName);
		app.sendBroadcast(intent);
	}
	private void saveAndSendBoardcast(Weather weather) {
		app.set_weatherInfoCache(weather);
		Intent intent = new Intent();
		intent.setAction(DATA.BroadcastPicClockActionName);
		app.sendBroadcast(intent);
	}
	@Override
	public void release() {
		get.release();
		service.release();
		Log.w(TAG, "release GetWeatherByName");
	}

	private class AsyncGetWeatherByName extends AsyncTask<String, Integer, String> {
//		private static final int TIMEOUT=3000;
		private IUrlGetable get;
		private boolean isStop;
		public AsyncGetWeatherByName() {
			/**
			 * �����HTTP�������HttpUrlConnection��������
			 * �����HttpClient�Ļ������룬��ת��Ҳ��Ч��
			*/
			get=new GetByHttpUrlConnection();
//			get=new GetByHttpClient();
			isStop=false;
			//TODO:�ӳ�ʱ
		}

		@Override
		protected String doInBackground(String... params) {
			String url=params[0];
			byte[] bytes=null;
			String result=null;
			try {
				if (isStop) {
					return null;
				}
				bytes = get.getUrl(url);
				if (bytes != null) {
					//byte[] bytes=content.getBytes("ISO-8859-1");
					result=ByteUtil.bytesToString(bytes, "utf-8");
				}
//				if (bytes != null) {
//					result = ByteUtil.bytesToString(bytes, "gbk");
//				}
			} catch (Exception e) {
				Log.e(TAG,url);
				e.printStackTrace();
			}

			return result;
		}
		@Override
		protected void onPostExecute(String s) {
			if (s != null) {
				saveWeather(s);
			}
		}
		public  void release() {
				get.release();
				Log.w(TAG, "release AsyncGetWeatherByName");
				get.release();
		}

	}

}
