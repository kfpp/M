package com.qqonline.broadcast;

import com.qqonline.conmon.MPFApp;
import com.qqonline.mpf.PicClockActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 * 天气信息监听Reciever,当收到天气信息改变的Intent时，从app缓存中刷新天气数据
 */
public class PicClockActivity_CityChangeReciever extends BroadcastReceiver {

	private PicClockActivity _activity;
	private MPFApp _mApp;
	public PicClockActivity_CityChangeReciever(PicClockActivity activity,MPFApp mApp) {
		_activity=activity;
		_mApp=mApp;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		_activity.SetWeather(_mApp.get_weatherInfoCache());
	}

}
