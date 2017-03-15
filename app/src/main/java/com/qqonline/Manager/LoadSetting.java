package com.qqonline.Manager;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.domain.Weather;
import com.qqonline.mpf.R;
import com.qqonline.mpf.getIsPlay;
import com.qqonline.Manager.db.ShieldListService;
import com.qqonline.Manager.db.WeatherService;

public class LoadSetting {

	private MPFApp mApp;
	private Activity context;
	private SharedPreferences preference;
	private Resources resource;
	private String[] preferenceFile;
	private String[] setting;
	public LoadSetting(Activity context) {
		this.context = context;
		mApp=(MPFApp)this.context.getApplication();
		resource=this.context.getResources();
		preferenceFile=resource.getStringArray(R.array.PreferenceFileNameArray);
		setting=resource.getStringArray(R.array.PreferenceArray);
		preference=this.context.getSharedPreferences(preferenceFile[0], Context.MODE_PRIVATE);
	}
	public void load()
	{
		loadCity();
		loadBkgMusicSwitch();
		loadAdvertisementSwitch();
		loadShiledList();
	}
	/**
	 * 加载拉黑列表
	 */
	private void loadShiledList() {
		ShieldListService service=new ShieldListService(context, DATA.DATAVERSION);
		List<String> list=service.getAllShiledOpenId();
		mApp.setShieldOpenIdList(list);
		service.release();
	}
	/**
	 * 载入城市设定
	 */
	private void loadCity()
	{
		String city=preference.getString(setting[0], null);
		mApp.set_cityNameCache(city);
		initWeatherCache(city);
	}
	/**
	 * 载入背景音乐设置
	 */
	private void loadBkgMusicSwitch()
	{
		String musicSwitch=preference.getString(setting[1],"false");
		boolean flag=musicSwitch.equals("false")?false:true;
		mApp.setBkgMusicEnabled(flag);
		if (!musicSwitch.equals("false")) {
			mApp.setBkgMusicName(musicSwitch);
		}
	}
	/**
	 * 载入广告设置
	 */
	private void loadAdvertisementSwitch()
	{
		new Thread(new getIsPlay(mApp)).start();//检查设置播放照片广告
	}
	/**
	 * 从数据库载入天气到缓存
	 */
	private void initWeatherCache(String city)
	{
		WeatherService service=new WeatherService(context, DATA.DATAVERSION);
		Weather weather=null;
		try {
			weather=service.getWeatherFromDatebase(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (weather!=null) {
			mApp.set_weatherInfoCache(weather);
		}
	}
	
}
