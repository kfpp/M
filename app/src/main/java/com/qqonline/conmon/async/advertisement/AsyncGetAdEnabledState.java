package com.qqonline.conmon.async.advertisement;

import com.qqonline.conmon.MPFApp;
import com.qqonline.mpf.getIsPlay;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncGetAdEnabledState extends AsyncTask<String, Integer, String> {

	private final static String TAG="AsyncGetAdEnabledState";
	private getIsPlay get;
	private MPFApp app;
	public AsyncGetAdEnabledState(MPFApp app) {
		this.app=app;
	}
	public void stop()
	{
		Log.i(TAG,"stop");
		if (get != null) {
			get.stop();
		}
	}
	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG,"doInBackground");
		 get=new getIsPlay(app);
		 get.run();
		return null;
	}
}
