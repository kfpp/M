package com.qqonline.broadcast;

import com.qqonline.mpf.PicClockActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
public class ScreenOffReciever extends BroadcastReceiver {

	public ScreenOffReciever() {
		Log.i("PingBao","ScreenOffReciever created");
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("PingBao",intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			try {
				Intent i = new Intent(context,PicClockActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundle = new Bundle();
				bundle.putBoolean("isWeatherReady", true);   //��ʾ��תʱ�����Ƿ��Ѿ�������			
				bundle.putString("checkOpenId", "");  //��һ�û���ID �����Ϊ�ձ�ʾ��ʾȫ���û���ͼƬ
				bundle.putBoolean("acti", false);  //��ʾ�Ǽ���ɹ�����ת�����Ѽ���ֱ����ת
				bundle.putBoolean("pingbao",true);
				i.putExtras(bundle);
				context.startActivity(i);
				Log.i("PingBao","startActivity");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}

}
