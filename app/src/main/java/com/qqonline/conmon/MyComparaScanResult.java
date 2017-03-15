package com.qqonline.conmon;

import java.util.Comparator;




import com.qqonline.Manager.db.MyWifiManager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;

public class MyComparaScanResult implements Comparator<ScanResult> {

	private MyWifiManager manager;
	public MyComparaScanResult(Context context) {
		manager=new MyWifiManager(context);
	}

	@Override
	public int compare(ScanResult lhs, ScanResult rhs) {
		WifiInfo info=manager.getManager().getConnectionInfo();
		int levelLeft=lhs.level;
		int levelRight=rhs.level;
		int result=0;
		if (lhs.BSSID.equals(info.getBSSID())) {
			return -1;
		}
		else if(rhs.BSSID.equals(info.getBSSID()))
		{
			return 1;
		}
		else if (levelLeft > levelRight) {
			result = -1;
		}
		else if( levelLeft < levelRight) {
			result = 1;
		}
		return result;
	}

}
