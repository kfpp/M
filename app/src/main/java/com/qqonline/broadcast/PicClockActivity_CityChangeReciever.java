package com.qqonline.broadcast;

import com.qqonline.conmon.MPFApp;
import com.qqonline.mpf.PicClockActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 * ������Ϣ����Reciever,���յ�������Ϣ�ı��Intentʱ����app������ˢ����������
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
