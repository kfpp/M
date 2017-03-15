package com.qqonline.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qqonline.conmon.DATA;

/**
 * 图片播放界面PicPlayActivity的Reciever，主要用于当有新图片进来，并且由AsyncImageTask异步任务下载完成时，触发该Reciever，重新载入并显示图片列表
 * @author YE
 *
 */
public class PlayReceiver extends BroadcastReceiver {
	private String openID;
	public PlayReceiver(String openID){
		this.openID = openID;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String openId = bundle.getString(DATA.bundleKey[1]);
		String name = bundle.getString(DATA.bundleKey[2]);
		if(this.openID.equals(openId) || "".equals(this.openID)){
			//activity.loadPic(false);
		}
		else
		{
			
		}
	}

}
