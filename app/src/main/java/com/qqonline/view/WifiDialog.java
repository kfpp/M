package com.qqonline.view;

import java.util.Collections;
import java.util.List;

import com.qqonline.conmon.MyAdaptViewHolder;
import com.qqonline.conmon.MyComparaScanResult;
import com.qqonline.conmon.MyWifiAdapter;
import com.qqonline.mpf.R;
import com.qqonline.Manager.db.MyWifiManager;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class WifiDialog {
	private LayoutInflater inflater;
	// private View view;
	// private Builder dlg;
	private MyWifiManager manager;
	private List<ScanResult> data;
	private Activity context;
	private MyWifiAdapter adapter;
	private Handler handler;
	private Switch switchMain;
	private ListView lvMain;
	private Runnable runnable;

	public WifiDialog(Activity context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// inflater=LayoutInflater.from(context);
		// view=inflater.inflate(R.layout.activity_main, null);

	}

	public Builder getWifiDialog() {
		Builder dlg = new AlertDialog.Builder(context)
			//	.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("选择WIFI")
				.setView(getWifiMainView())			
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							handler.removeCallbacks(runnable);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
		return dlg;
	}

	private View getWifiMainView() {
		View view = inflater.inflate(R.layout.activity_wifi_main, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		initData(view);
		initControls(view);
	}

	private void initControls(View view) {
		lvMain = (ListView) view.findViewById(R.id.lv_main_wifi);
		lvMain.setOnItemClickListener(new MyItemClickListener());
		switchMain = (Switch) view.findViewById(R.id.swhMain);
		switchMain.setOnCheckedChangeListener(new MyCheckChangeListener());

		int state = manager.getManager().getWifiState();
		if (state == WifiManager.WIFI_STATE_DISABLED) {
			switchMain.setChecked(false);
		} else if (state == WifiManager.WIFI_STATE_ENABLED) {
			switchMain.setChecked(true);
			lvMain.setAdapter(adapter);
			handler.postDelayed(runnable, 3000);
		}

	}
	
	private void initData(View view) {
		manager = new MyWifiManager(context);
		data = manager.getManager().getScanResults();
		// adapterSimple=new SimpleAdapter(this, data,
		// R.layout.item_main_wifilist, new String[]{}, new int[]{});
		adapter = new MyWifiAdapter(context, data);
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				List<ScanResult> newResult = manager.getManager()
						.getScanResults();
				// adapter = new MyAdapter(MainActivity.this, data);
				// lvMain.setAdapter(adapter);
				addNewNetWork(newResult);
				removeNoSignalNetWork(newResult);
				Collections.sort(data, new MyComparaScanResult(context));
				adapter.notifyDataSetChanged();
				handler.postDelayed(this, 3000);
			}
		};

	}

	private void addNewNetWork(List<ScanResult> temp) {

		for (int i = 0; i < temp.size(); i++) {
			// if (!data.contains(temp.get(i))) {
			// data.add(temp.get(i));
			// }
			boolean isNetWorkExist = false; // 默认新的临时扫描列表中全部的网络都是新的
			for (int j = 0; j < data.size(); j++) {
				if (data.get(j).BSSID.equals(temp.get(i).BSSID)) {
					isNetWorkExist = true; // 如果有相同的地址，说明应用列表中已有该网络，不用再添加
				}
			}
			if (!isNetWorkExist) { // 如果该网络地址不存在，则说明是新扫描到的网络，添加到应用列表中
				data.add(temp.get(i));
			}
		}
	}

	private void removeNoSignalNetWork(List<ScanResult> temp) {
		// ArrayList<Integer> removeList=new ArrayList<Integer>();

		for (int i = 0; i < data.size(); i++) {
			// if (!temp.contains(data.get(i))) {
			// data.remove(i);
			// }
			boolean isNetWorkExist = false; // 默认应用列表中全部的网络都已不存在
			for (int j = 0; j < temp.size(); j++) {
				if (temp.get(j).BSSID.equals(data.get(i).BSSID)) {
					isNetWorkExist = true; // 在新的临时扫描列表中还能找到data列表中的第i项，说明第i项这个网络依然存在
				}
			}
			if (!isNetWorkExist) { // 如果整个扫描列表中都没有这个第i项，则说明第i项网络已关闭或没信号，从列表中去除
				data.remove(i);
				// removeList.add(i);
			}
		}
		// for (int i = 0; i < removeList.size(); i++) {
		// int num=removeList.get(i);
		// data.remove(num);
		// }
	}
	private final class MyItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, final View view, int position,
				long id) {
			final String ssid = ((MyAdaptViewHolder) view.getTag()).tvName
					.getText().toString();
			
			final EditText tvPass=new EditText(context);
			
	//		manager.Connect(ssid, "tyty@888", MyWifiManager.WIFI_WPA);
			new AlertDialog.Builder(context)
			.setTitle(ssid)
			.setMessage("请输入密码：")
			.setView(tvPass)
			.setPositiveButton("连接", new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int type = -1;
					String passwordType = ((MyAdaptViewHolder) view.getTag()).tvInfo
							.getText().toString();
					if (passwordType.contains("WPA")) {
						type=MyWifiManager.WIFI_WPA;
					}
					else if(passwordType.contains("WEP"))
					{
						type=MyWifiManager.WIFI_WEP;
					}
					else {
						type=MyWifiManager.WIFI_NOPASS;
					}
					manager.Connect(ssid, tvPass.getText().toString().trim(), type);
				}
			})
			.setNegativeButton("取消", null)
			.show();
		}		
	}
	private final class MyCheckChangeListener implements
			OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.swhMain:
				changeWifiState(isChecked);
				break;
			default:
				break;
			}
		}

	}

	private void changeWifiState(boolean open) {
		boolean flag = manager.changeWifiState(open);
		if (flag) {
			if (open) { // 打开Wifi
				handler.postDelayed(runnable, 3000);
				lvMain.setAdapter(adapter);

			} else {
				handler.removeCallbacks(runnable);
				lvMain.setAdapter(null);
			}
		}
	}

}
