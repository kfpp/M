package com.qqonline.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qqonline.conmon.DATA;

/**
 * ͼƬ���Ž���PicPlayActivity��Reciever����Ҫ���ڵ�����ͼƬ������������AsyncImageTask�첽�����������ʱ��������Reciever���������벢��ʾͼƬ�б�
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
