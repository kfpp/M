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
				.setTitle("ѡ��WIFI")
				.setView(getWifiMainView())			
				.setPositiveButton("ȷ��", new OnClickListener() {
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
			boolean isNetWorkExist = false; // Ĭ���µ���ʱɨ���б���ȫ�������綼���µ�
			for (int j = 0; j < data.size(); j++) {
				if (data.get(j).BSSID.equals(temp.get(i).BSSID)) {
					isNetWorkExist = true; // �������ͬ�ĵ�ַ��˵��Ӧ���б������и����磬���������
				}
			}
			if (!isNetWorkExist) { // ����������ַ�����ڣ���˵������ɨ�赽�����磬��ӵ�Ӧ���б���
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
			boolean isNetWorkExist = false; // Ĭ��Ӧ���б���ȫ�������綼�Ѳ�����
			for (int j = 0; j < temp.size(); j++) {
				if (temp.get(j).BSSID.equals(data.get(i).BSSID)) {
					isNetWorkExist = true; // ���µ���ʱɨ���б��л����ҵ�data�б��еĵ�i�˵����i�����������Ȼ����
				}
			}
			if (!isNetWorkExist) { // �������ɨ���б��ж�û�������i���˵����i�������ѹرջ�û�źţ����б���ȥ��
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
			.setMessage("���������룺")
			.setView(tvPass)
			.setPositiveButton("����", new OnClickListener() {			
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
			.setNegativeButton("ȡ��", null)
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
			if (open) { // ��Wifi
				handler.postDelayed(runnable, 3000);
				lvMain.setAdapter(adapter);

			} else {
				handler.removeCallbacks(runnable);
				lvMain.setAdapter(null);
			}
		}
	}

}
