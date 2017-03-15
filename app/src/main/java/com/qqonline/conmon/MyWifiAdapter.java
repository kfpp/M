package com.qqonline.conmon;

import java.util.List;

import com.qqonline.mpf.R;
import com.qqonline.Manager.db.MyWifiManager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyWifiAdapter extends BaseAdapter {

	private List<ScanResult> list;
	private LayoutInflater inflater;
	private MyWifiManager manager;
	private WifiInfo currentInfo;
	public MyWifiAdapter(Context context, List<ScanResult> list) {
		inflater=LayoutInflater.from(context);
		this.list = list;
		manager = new MyWifiManager(context);	
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (position <= list.size()) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyAdaptViewHolder holder;
		//View view=null;
		if (convertView == null) {
			convertView=inflater.inflate(R.layout.item_main_wifilist, null);
			TextView tvName=(TextView) convertView.findViewById(R.id.item_main_wifilist_wifiname);
			TextView tvInfo=(TextView) convertView.findViewById(R.id.item_main_wifilist_wifiinfo);
			ImageView ivIcon=(ImageView) convertView.findViewById(R.id.item_main_wifilist_wifiicon);
			holder=new MyAdaptViewHolder();
			holder.tvName=tvName;
			holder.tvInfo=tvInfo;
			holder.ivIcon=ivIcon;
			convertView.setTag(holder);
		}
		else {
			holder=(MyAdaptViewHolder)convertView.getTag();
		}
		
		ScanResult scanResult=list.get(position);
		currentInfo= manager.getManager().getConnectionInfo();
		TextView tvName=holder.tvName;
		TextView tvInfo=holder.tvInfo;
		ImageView ivIcon=holder.ivIcon;
		tvName.setText(scanResult.SSID);
		String infoText=null;
		if (currentInfo.getBSSID() != null  && currentInfo.getBSSID().equals(scanResult.BSSID)) {
			infoText="已连接";
		}
		else if (scanResult.capabilities.contains("WPA")) {
			infoText="通过WPA/WPA2进行保护";
		}
		else if (scanResult.capabilities.contains("WEP")) {
			infoText="通过WEP进行保护";
		}
		else {
			infoText="无密码";
		}
		tvInfo.setText(infoText);
		ivIcon.setImageResource(getIconID(Math.abs( scanResult.level)));
		
		return convertView;
	}
	private int getIconID(int level)
	{
		int id=0;
		if (level > 100) {
			id=R.drawable.wifisignallevel0;
		}
		else if (level > 80) {
			id=R.drawable.wifisignallevel1;
		}
		else if (level > 70) {
			id=R.drawable.wifisignallevel2;
		}
		else if (level > 60) {
			id=R.drawable.wifisignallevel3;
		}
		else if (level > 50){
			id=R.drawable.wifisignallevel4;
		}
		else {
			id=R.drawable.wifisignallevel4;
		}
		return id;
	}
	public void release()
	{
		list=null;
		inflater=null;
	}

}
