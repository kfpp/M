package com.qqonline.broadcast;

import com.qqonline.conmon.DATA;
import com.qqonline.mpf.ActiActivity;
import com.qqonline.mpf.MainActivity;
import com.qqonline.mpf.PicActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PicReceiver extends BroadcastReceiver {
	private PicActivity activity;
	public PicReceiver(PicActivity activity){
		this.activity = activity;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String picUrl = bundle.getString(DATA.bundleKey[0]);
		String openId = bundle.getString(DATA.bundleKey[1]);
		
		if(activity.OpenId.equals(openId)){
			activity.addPic(picUrl);//发送照片的人跟当前打开的文件夹一致，则把图片添加进去
		}
	}
}
