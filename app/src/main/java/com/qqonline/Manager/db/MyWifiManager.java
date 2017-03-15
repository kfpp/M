package com.qqonline.Manager.db;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class MyWifiManager {

	private final static String TAG = "MyWifiManager";
	public final static int WIFI_NOPASS = 1;
	public final static int WIFI_WEP = 2;
	public final static int WIFI_WPA = 3;
	private WifiManager wifiManager;

	public MyWifiManager(Context activity) {
		wifiManager = (WifiManager) activity
				.getSystemService(activity.WIFI_SERVICE);
	}

	public boolean changeWifiState(boolean open) {
		if (wifiManager.isWifiEnabled() && !open) {
			wifiManager.setWifiEnabled(open);
			return true;
		} else if (!wifiManager.isWifiEnabled() && open) {
			wifiManager.setWifiEnabled(open);
			return true;
		}
		return false;
	}

	public WifiManager getManager() {
		return wifiManager;
	}

	public void release() {
		wifiManager = null;
	}

	private void addNetwork(WifiConfiguration wcg) { // 添加一个网络配置并连接
		int wcgID = wifiManager.addNetwork(wcg);
		boolean b = wifiManager.enableNetwork(wcgID, true);
		System.out.println("addNetwork--" + wcgID);
		System.out.println("enableNetwork--" + b);
	}

	public void Connect(String SSID, String Password, int Type) {
		WifiConfiguration temp=IsExsits(SSID);
		if (temp != null) {
			wifiManager.enableNetwork(temp.networkId, true);
		}
		else {
			temp=CreateWifiInfo(SSID, Password, Type);
			addNetwork(temp);
		}
	}

	private WifiConfiguration CreateWifiInfo(String SSID, String Password,
			int Type) {
		Log.i(TAG, "SSID:" + SSID + ",password:" + Password);
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);

		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		} else {
			Log.i(TAG, "IsExsits is null.");
		}

		if (Type == WIFI_NOPASS) // WIFICIPHER_NOPASS
		{
			Log.i(TAG, "Type =WIFI_NOPASS");
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WIFI_WEP) // WIFICIPHER_WEP
		{
			Log.i(TAG, "Type =WIFI_WEP");
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == WIFI_WPA) // WIFICIPHER_WPA
		{

			Log.i(TAG, "Type =WIFI_WPA");
			config.preSharedKey = "\"" + Password + "\"";

			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	private WifiConfiguration IsExsits(String SSID) { // 查看以前是否已经配置过该SSID
		List<WifiConfiguration> existingConfigs = wifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}
}
