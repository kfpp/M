package com.qqonline.broadcast;

import com.qqonline.conmon.DATA;
import com.qqonline.mpf.PicClockActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ClockReciever extends BroadcastReceiver {

	private PicClockActivity _active;
	public ClockReciever(PicClockActivity active) {
		_active=active;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String openId = bundle.getString(DATA.bundleKey[1]);
		String name = bundle.getString(DATA.bundleKey[2]);
		if(_active._openID.equals(openId) || "".equals(_active._openID)){
			_active.change();
		}
		else
		{
			
		}
	}

}
