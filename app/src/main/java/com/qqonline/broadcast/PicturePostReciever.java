package com.qqonline.broadcast;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.async.AsyncTaskPost;
import com.qqonline.conmon.async.AsyncTaskPost.DoPostBack;
import com.qqonline.Manager.db.MpfMachineService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PicturePostReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle=intent.getExtras();
		final String url=bundle.getString(DATA.bundleKey[0]);
		final String openId=bundle.getString(DATA.bundleKey[1]);
		MpfMachineService service=new MpfMachineService(context, DATA.DATAVERSION);		
		final String machineId=String.valueOf(service.getMpfMachine().getDbId());
		service=null;
		AsyncTaskPost post=new AsyncTaskPost(new DoPostBack() {			
			@Override
			public void PostBack(String result) {
				
			}
		});
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		BasicNameValuePair param=new BasicNameValuePair("url", url);
		params.add(param);
		param=new BasicNameValuePair("machineId", machineId);
		params.add(param);
		param=new BasicNameValuePair("openId", openId);
		params.add(param);
		post.setParams(params);
		post.execute(DATA.PicturePostUrl);
	}

}
