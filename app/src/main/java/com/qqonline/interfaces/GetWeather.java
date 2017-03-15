package com.qqonline.interfaces;

import android.app.Activity;
import android.content.Intent;

public interface GetWeather {
//	public Activity activity=null;
	public void AsyncGet(Activity activity);
	public void setParams(Intent intent);
	public void release();
}
