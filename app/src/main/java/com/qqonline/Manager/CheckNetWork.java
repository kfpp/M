package com.qqonline.Manager;

import com.qqonline.mpf.R;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.provider.Settings;

import java.lang.ref.WeakReference;

public class CheckNetWork {

//	private Activity context;
	private WeakReference<Activity> activityWeakReference;
	public CheckNetWork(Activity context) {

//		this.context = context;
		activityWeakReference=new WeakReference<Activity>(context);
	}

	public void check()
	{
		boolean flag=isNetWorkEnabled();
		if (!flag && activityWeakReference.get() != null) {
			
			new AlertDialog.Builder(activityWeakReference.get())
			.setTitle(R.string.no_network)
			.setIcon(android.R.drawable.ic_menu_info_details)
			.setMessage(R.string.set_your_network)
			.setPositiveButton(R.string.setting, new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (activityWeakReference.get() != null) {
						activityWeakReference.get().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					}					
					//自定义WIFI连接界面
				//	WifiDialog dlg=new WifiDialog(context);
				//	dlg.getWifiDialog().show();
				}
			})
			.setNegativeButton(R.string.Cancle, null)
			.show();
			
		}
	}
	private boolean isNetWorkEnabled()
	{
		if(this.activityWeakReference.get() == null) return true;
		ConnectivityManager manager=(ConnectivityManager) activityWeakReference.get().getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean isWifi=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		boolean isMobileNetWork=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		return (isMobileNetWork || isWifi)?true:false;
	}
	public void release()
	{
//		this.context=null;
	}
	
}
