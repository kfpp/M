package com.qqonline.broadcast;

import com.qqonline.conmon.DATA;
import com.qqonline.Manager.db.MpfMachineService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 *  ¸üÐÂ°ó¶¨Âë
 */
public class BindCodeUpdateReciever extends BroadcastReceiver {

	private Context context;
	public BindCodeUpdateReciever(Context context) {
		this.context = context;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String bindCode = bundle.getString(DATA.bundleKey[3]);
		if (bindCode!=null || !"".equals(bindCode)) {
			MpfMachineService service=new MpfMachineService(context, DATA.DATAVERSION);
			service.MpfMachineUpdateBindCode(bindCode);
			service=null;
			return;
		}
	}

}
