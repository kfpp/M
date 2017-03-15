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
				bundle.putBoolean("isWeatherReady", true);   //表示跳转时天气是否已经加载完			
				bundle.putString("checkOpenId", "");  //其一用户的ID ，如果为空表示显示全部用户的图片
				bundle.putBoolean("acti", false);  //表示是激活成功后跳转还是已激活直接跳转
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
