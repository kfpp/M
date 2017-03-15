package com.qqonline.mpf;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.qqonline.Manager.BKMusicManager;
import com.qqonline.Manager.BKMusicManager.OnDownloadFinished;
import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.Manager.db.MpfPictureService;
import com.qqonline.Manager.db.ShieldListService;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.SharedPreferenceUtil;
import com.qqonline.conmon.async.AsyncGet;
import com.qqonline.conmon.async.AsyncGet.MyDoPostBack;
import com.qqonline.conmon.async.AsyncTaskPost;
import com.qqonline.conmon.async.fileCopy.AsyncAssertsToSDCard;
import com.qqonline.conmon.async.scanSDCard.AsyncScanSDCardPicture;
import com.qqonline.domain.MpfMachine;
import com.qqonline.domain.ShieldInfo;
import com.qqonline.modules.QRCodeModule.QRCodeAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitySetting extends AppCompatActivity {
	public static final String TAG="CitySetting";
	public final class MusicInfo {
		private String musicName;
		private String musicURL;
		private long musicSize;

		public String getMusicName() {
			return musicName;
		}

		public void setMusicName(String musicName) {
			this.musicName = musicName;
		}

		public String getMusicURL() {
			return musicURL;
		}

		public void setMusicURL(String musicURL) {
			this.musicURL = musicURL;
		}

		public long getMusicSize() {
			return musicSize;
		}

		public void setMusicSize(long musicSize) {
			this.musicSize = musicSize;
		}

	}

	/**
	 * ����Ĭ�ϵ������б�ûѡ��ʱ����ɫ�����ڴ�ѡ�еĻ�ɫ�ָ�Ϊûѡ�еĺ�ɫ
	 */
	private Drawable defaultMusicListBkg;
	// private static String[] arr = { "ѡ�����", "��������" };
	private List<Map<String, Object>> _map;
	private MPFApp _mApp;
	private MpfMachineService mms = null;
	private SharedPreferences sharedPreference;
	private ArrayList<MusicInfo> musicInfo;
	/**
	 * �����������ֵĲ�����
	 */
	// private MediaPlayer player;
	// �������ֿ���
	private Switch switch_music;
	/**
	 * ���ڶ�λ�����б��еĵ�ǰѡ����
	 */
	private int musicSelectedIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_setting);

		initData();
		initControls();
	}

	@Override
	protected void onDestroy() {
		_mApp.removeActivity(this);
		super.onDestroy();
	}

	/**
	 * ��ʼ
	 */
	private void initControls() {
		ListView _lvCity = (ListView) findViewById(R.id.lvCity);
		SimpleAdapter adapt = new SimpleAdapter(this, _map,
				R.layout.setting_activity_item,
				new String[] { "icon", "text" }, new int[] {
						R.id.setting_item_icon, R.id.setting_item_text });
		_lvCity.setAdapter(adapt);
		_lvCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				switch (position) {
				case 0:
					ShowCityDialog();
					break;
				case 1:
					// ShowBKMusicDialog();
					showInternetMusicDialog();
					break;
				case 2:
					showBindList();
					break;
				case 3:
					// deleteAllPicture();
					showUserPlayActivity();
					break;
				case 4:
					importPictures();
					break;
				case 5:
					showTimer();
					break;
				case 6:
					showQRCodeDialog();
					break;
				case 7:
					ShowRegisterInfoDialog();
					break;
				default:
					break;
				}
			}
		});
	}

	protected void showQRCodeDialog() {
		/*final ImageView tempView=new ImageView(this);
		tempView.setPadding(0, 30, 0, 30);
		new AlertDialog.Builder(this)
		.setTitle(R.string.clock_activity_alert_dialog_title)
		.setView(tempView)
		.setNegativeButton(R.string.OK, null)
		.show();
		MpfMachineService service=new MpfMachineService(this, DATA.DATAVERSION);
		int dbID=service.getMPFDBID();
		QRCodeUtil qrUtil=new QRCodeUtil(this, dbID, new QRCodeUtil.IDoPostBack() {				
			@Override
			public void BitmapLoaded(Bitmap bitmap) {
				tempView.setImageBitmap(bitmap);
			}

			@Override
			public void OnError(String error) {

			}
		});
		qrUtil.asyncGetBitmap();*/
		QRCodeAPI.show(this);
	}

	/**
	 * ��ʾ��ʱ���ػ����
	 */
	protected void showTimer() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		ComponentName cn = new ComponentName("com.qqonline.timerswitch",
				"com.qqonline.timerswitch.MainActivity");
		intent.setComponent(cn);
		try {
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			// if (getTimerAPK()) {
			// installTimerAPK();
			// }
			String path = Environment.getExternalStorageDirectory()
					+ File.separator + "MPFCache";
			String name = "TimeSwitcher.apk";
			String assertsFileName = "TimeSwitcher.apk";
			AsyncAssertsToSDCard move = new AsyncAssertsToSDCard(
					CitySetting.this,
					new AsyncAssertsToSDCard.AsyncAssertsToSDCardDone() {
						@Override
						public void onFinished(String filePath) {
							installTimerAPK(filePath);
						}
					});
			move.execute(path, name, assertsFileName);
		}
	}

	/**
	 * ��װ��ʱ��APK
	 */
	private void installTimerAPK(String filePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(filePath)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * ����ͼƬ
	 */
	protected void importPictures() {
		// Toast.makeText(CitySetting.this, "����ͼƬ", Toast.LENGTH_SHORT).show();
		// Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
		// intent.setType("image/*");
		// startActivityForResult(intent, 1);
		AsyncScanSDCardPicture scan = new AsyncScanSDCardPicture(
				CitySetting.this);
		scan.execute("");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) { // ����ͼƬ����ķ�������
				int i = 0;
				i++;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showUserPlayActivity() {
		Intent intent = new Intent(CitySetting.this, PicActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("OpenId", "");
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void deleteAllPicture() {
		MpfPictureService service = new MpfPictureService(CitySetting.this,
				DATA.DATAVERSION);
		service.deleteAllPicture();

	}

	private void initData() {
		InitMap();
		initPreference();
		mms = new MpfMachineService(CitySetting.this, DATA.DATAVERSION);
		_mApp = (MPFApp) getApplication();
		_mApp.addActivity(this);
		// player=_mApp.getListenPlayer();
		musicInfo = new ArrayList<CitySetting.MusicInfo>();
	}

	/**
	 * ��ʼ�������ý������
	 */
	private void InitMap() {
		_map = new ArrayList<Map<String, Object>>();
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("icon", R.drawable.i1);
		temp.put("text", "ѡ�����");
		_map.add(temp);

		temp = new HashMap<String, Object>();
		temp.put("icon", R.drawable.i2);
		temp.put("text", "��������");
		_map.add(temp);

		temp = new HashMap<String, Object>();
		temp.put("icon", R.drawable.i3);
		temp.put("text", "���б�");
		_map.add(temp);

		temp = new HashMap<String, Object>();
		temp.put("icon", R.drawable.i4);
		temp.put("text", "ͼƬ����");
		_map.add(temp);

		temp = new HashMap<String, Object>();
		temp.put("icon", R.drawable.i5);
		temp.put("text", "����ͼƬ");
		_map.add(temp);

		temp = new HashMap<String, Object>();
		temp.put("icon", R.drawable.i6);
		temp.put("text", "��ʱ���ػ�");
		_map.add(temp);

		temp = new HashMap<String, Object>();
		temp.put("icon", R.drawable.i7);
		temp.put("text", "�󶨶�ά��");
		_map.add(temp);
		
		temp = new HashMap<String, Object>();
		temp.put("icon", R.drawable.i8);
		temp.put("text", "ע����Ϣ");
		_map.add(temp);

		// temp = new HashMap<String, Object>();
		// temp.put("icon", android.R.drawable.ic_dialog_info);
		// temp.put("text", "���ͼƬ");
		// _map.add(temp);

	}

	/**
	 * ��ʼ��ƫ�����õ�ȫ������
	 */
	private void initPreference() {
		sharedPreference=SharedPreferenceUtil.getDefaultSharedPreferences(this);
	}

	/**
	 * ��ʾ�����б�Ի���
	 */
	private void showBindList() {

		final ListView view = new ListView(CitySetting.this);
		final Resources resources=getResources();
		final int colorBlue=resources.getColor(R.color.bule);
		final int colorBlack=resources.getColor(R.color.black);
		final String shieldAppendText=resources.getString(R.string.be_shield);
		final ShieldListService shieldService=new ShieldListService(CitySetting.this, DATA.DATAVERSION);
		view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final ViewHolder holder=(ViewHolder)view.getTag();
				final String openId=holder.getOpenId();
				boolean isShield=holder.isShield();
				if (isShield) {       //����һ���ѱ����ε��û��������������
					TextView tv=((TextView)view);
					tv.setTextColor(colorBlue);
					tv.setText(tv.getText().toString().replace(shieldAppendText, ""));
					holder.setShield(false);
					if (_mApp.getShieldOpenIdList().contains(openId)) {
						_mApp.getShieldOpenIdList().remove(openId);
					}
					ShieldInfo shield=new ShieldInfo();
					shield.setOpenId(openId);
					shieldService.deleteShield(shield);
				}
				else {               //����һ��û�����ε��û��������������
					TextView tv=((TextView)view);
					tv.setTextColor(colorBlack);
					tv.setText(tv.getText().toString()+shieldAppendText);
					holder.setShield(true);
					if (!_mApp.getShieldOpenIdList().contains(openId)) {
						_mApp.getShieldOpenIdList().add(openId);
					}
					ShieldInfo shield=new ShieldInfo();
					shield.setOpenId(openId);
					shieldService.addShield(shield);
				}
			}
		});
		new AlertDialog.Builder(CitySetting.this).setTitle("���б�")
				.setIcon(android.R.drawable.ic_menu_my_calendar)
				// .setMessage("��������")
				.setView(view).setNegativeButton("�ر�", null).show();
		AsyncGet get = new AsyncGet(CitySetting.this, new MyDoPostBack() {
			@Override
			public void DoPostBack(String json) {
				ArrayList<String> bindArray = null,openIdList=null;
				try {
					bindArray = getBindList(json);
					openIdList=getBindListOpenId(json);
					int num = bindArray.size();
					bindArray.add(0, "��������" + num);
					openIdList.add(0, "��������" + num);
					setListData(view, bindArray,openIdList);
				} catch (Exception e) {
					
					e.printStackTrace();
					Toast.makeText(CitySetting.this, "��ȡ���ݳ���",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		get.execute(DATA.BINDLISTURL
				+ mms.getMpfMachine().getMachineSerialNumber());

	}
	
	/**
	 * ����JSON���ݲ����ذ����б�
	 * 
	 * @param json
	 *            Ҫ����������Դ
	 * @return �����б�
	 * @throws Exception
	 *             JSON��������
	 */
	private ArrayList<String> getBindList(String json) throws Exception {
		ArrayList<String> bindArray = new ArrayList<String>();
		JSONArray jsonArray = new JSONArray(json);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = (JSONObject) jsonArray.get(i);
			String name = object.getString("Name");
			bindArray.add(name);
		}
		return bindArray;
	}
	/**
	 * ����JSON���ݲ����ذ����б�
	 * 
	 * @param json
	 *            Ҫ����������Դ
	 * @return �����б�
	 * @throws Exception
	 *             JSON��������
	 */
	private ArrayList<String> getBindListOpenId(String json) throws Exception {
		ArrayList<String> bindArray = new ArrayList<String>();
		JSONArray jsonArray = new JSONArray(json);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = (JSONObject) jsonArray.get(i);
			String name = object.getString("OpenId");
			bindArray.add(name);
		}
		return bindArray;
	}

	/**
	 * Ϊָ���б�ؼ�����Adapter����
	 * 
	 * @param view
	 *            ָ���ؼ�
	 * @param data
	 *            ����Դ
	 */
	private void setListData(ListView view, ArrayList<String> data,ArrayList<String> openId) {
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//				CitySetting.this, android.R.layout.simple_list_item_1, data);
		BindListAdapter adapter=new BindListAdapter(CitySetting.this, data,openId);
		view.setAdapter(adapter);
	}

	/**
	 * ��ʾ����ѡ��Ի���
	 */
	private void ShowCityDialog() {
		final EditText _tvCitySetting = new EditText(CitySetting.this);
		new AlertDialog.Builder(CitySetting.this)
				.setIcon(android.R.drawable.ic_dialog_info).setTitle("�������������")
				.setMessage("��ֱ������ط�����ע�ⲻҪ���С�ʡ���С��򡱵�����")
				.setView(_tvCitySetting)
				.setPositiveButton("ȷ��", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String cityname=_tvCitySetting.getText().toString();
						_mApp.set_cityNameCache(cityname);

						SharedPreferenceUtil.setPreference(
								sharedPreference,
								getResources().getStringArray(
										R.array.PreferenceArray)[0],
								cityname);
						Toast.makeText(CitySetting.this,R.string.setting_successed,Toast.LENGTH_SHORT).show();

					}
				}).setNegativeButton("ȡ��", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * ��ʾ�������ֿ��ضԻ��򣨾ɰ棩
	 */
	private void ShowBKMusicDialog() {
		final String musicSwitch = getResources().getStringArray(
				R.array.PreferenceArray)[1];
		final SharedPreferences shared = getSharedPreferences(getResources()
				.getStringArray(R.array.PreferenceFileNameArray)[0],
				CitySetting.MODE_PRIVATE);
		String flagStr = shared.getString(musicSwitch, "true");
		int flag = flagStr.equals("true") ? 0 : 1;

		new AlertDialog.Builder(CitySetting.this)
				.setTitle("��������")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(new String[] { "��", "�ر�" }, flag,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									if (_mApp.getBkPlayer() != null) {
										_mApp.setBkgMusicEnabled(true);
										_mApp.getBkPlayer().start();
										// bkgMusicManager.changeBkgMusicAndPlay(R.raw.bkmusic);
										SharedPreferenceUtil.setPreference(
												shared, musicSwitch, "true");

									}
									break;
								case 1:
									if (_mApp.getBkPlayer() != null) {
										_mApp.getBkPlayer().pause();
										SharedPreferenceUtil.setPreference(
												shared, musicSwitch, "false");
										_mApp.setBkgMusicEnabled(false);
									}
									break;
								default:
									break;
								}

							}
						}).setNegativeButton("ȷ��", null).show();
	}

	/**
	 * ��ʾ���米�������б�ѡ��Ի���
	 */
	private void showInternetMusicDialog() {

		if (_mApp.getBkPlayer().isPlaying()) {
			_mApp.getBkPlayer().pause();
		}
		asyncGetMusicList();
	}

	private void asyncGetMusicList() {
		AsyncGet get = new AsyncGet(CitySetting.this, new MyDoPostBack() {
			@Override
			public void DoPostBack(String json) {
				aysncGetMusicListPostBack(json);
			}
		});
		get.execute(DATA.MUSICLISTURL);
	}

	/**
	 * �첽ժȡ�������б��Ļص�����
	 * 
	 * @param json
	 *            �����б�JSON����
	 */
	private void aysncGetMusicListPostBack(String json) {
		ArrayList<String> musicName = new ArrayList<String>();

		final String musicSwitch = getResources().getStringArray(
				R.array.PreferenceArray)[1];
		final SharedPreferences shared = getSharedPreferences(getResources()
				.getStringArray(R.array.PreferenceFileNameArray)[0],
				CitySetting.MODE_PRIVATE);
		// ƫ�������б���ı����������֣�falseΪ�رձ�������
		String flagStr = shared.getString(musicSwitch, "false");
		// �жϱ��������Ƿ���
		boolean flag = flagStr.equals("false") ? false : true;

		LayoutInflater inflater = LayoutInflater.from(CitySetting.this);
		View view = inflater.inflate(R.layout.view_setting_musiclist, null);

		switch_music = (Switch) view
				.findViewById(R.id.view_setting_musiclist_musicswitch);
		switch_music.setChecked(flag);

		final ListView list = (ListView) view
				.findViewById(R.id.view_setting_musiclist_list);
		list.setOnItemClickListener(new MyMusicSingleChoiceItems());
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				CitySetting.this,
				android.R.layout.simple_list_item_single_choice, musicName);
		if (flag) {
			list.setAdapter(adapter);
		}
		switch_music.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!isChecked) {
					list.setAdapter(null);
					if (_mApp.getBkPlayer() != null) {
						_mApp.getBkPlayer().pause();
						SharedPreferenceUtil.setPreference(shared, musicSwitch,
								"false");
						_mApp.setBkgMusicEnabled(false);
					}
				} else {
					list.setAdapter(adapter);
					list.setItemChecked(musicSelectedIndex, true);
				}
			}
		});
		try {
			// ����JSON����Ϊ������Ϣ�б�
			musicInfo = getMusicInfoList(json);
		} catch (Exception e) {
			e.printStackTrace();
			// return;
		}
		for (int i = 0; i < musicInfo.size(); i++) {
			// ��������Ϣ�б�ת��Ϊ�б�ؼ�������Դ
			String tempMusicName = musicInfo.get(i).getMusicName();
			musicName.add(tempMusicName);
			if (tempMusicName.equals(flagStr)) {
				// ������������ֺ�ƫ�������б��������������ͬ�����ʾ��ǰ�ı����������б��е�λ��
				musicSelectedIndex = i;
			}
		}
		adapter.notifyDataSetChanged();
		list.setItemChecked(musicSelectedIndex, true);
		new AlertDialog.Builder(CitySetting.this)
				.setTitle("��ѡ�񱳾�����(����)")
				// .setSingleChoiceItems(
				// (String[]) musicName.toArray(new String[musicName
				// .size()]), 0, new MyMusicSingleChoiceItems())
				.setView(view)
				.setOnCancelListener(new MyMusicSingleChoiceItems())
				.setPositiveButton(R.string.Select, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						musicListPositiveBtnClicked(musicSwitch, shared);
					}
				}).setNegativeButton(R.string.Close, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						musicListNegativeBtnClicked();
					}
				}).show();
	}

	/**
	 * �����б�ѡ���¼���Ӧ�ӿ���
	 * 
	 * @author YE
	 *
	 */
	private final class MyMusicSingleChoiceItems implements
			AdapterView.OnItemClickListener, DialogInterface.OnDismissListener,
			DialogInterface.OnCancelListener,
			CompoundButton.OnCheckedChangeListener {
		/**
		 * ListView ��ѡ�������Ӧ����
		 * 
		 * @param parent
		 * @param view
		 * @param position
		 * @param id
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			((ListView) parent).getChildAt(musicSelectedIndex).setBackground(
					defaultMusicListBkg); // ������һ��ѡ�е��ԭ��ɫΪ��ɫ
			musicSelectedIndex = position; // ���浱ǰλ��

			BKMusicManager manager = new BKMusicManager(CitySetting.this);
			if (!manager.fileExistLocation(musicInfo.get(position))) {
				Toast.makeText(CitySetting.this, R.string.buffering,
						Toast.LENGTH_SHORT).show();
			}
			manager.playMusicWithoutDownload(musicInfo.get(position));
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (_mApp.getListenPlayer() != null) {
				_mApp.getListenPlayer().release(); // ֹͣ��������
				_mApp.setListenPlayer(null);
			}
			if (_mApp.isBkgMusicEnabled() && !_mApp.getBkPlayer().isPlaying()) {
				_mApp.getBkPlayer().start(); // �ָ���������
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			if (_mApp.getListenPlayer() != null) {
				_mApp.getListenPlayer().release(); // ֹͣ��������
				_mApp.setListenPlayer(null);
			}
			if (_mApp.isBkgMusicEnabled() && !_mApp.getBkPlayer().isPlaying()) {
				_mApp.getBkPlayer().start();
			}
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * ���ֵ�����ѡ��ť��Ӧ����
	 * 
	 * @param musicSwitch
	 * @param shared
	 */
	private void musicListPositiveBtnClicked(final String musicSwitch,
			final SharedPreferences shared) {
		/**
		 * ֹͣ��������������
		 */
		if (_mApp.getListenPlayer() != null) {
			_mApp.getListenPlayer().release(); // ֹͣ��������
			_mApp.setListenPlayer(null);
		}
		/**
		 * ������ֿ���ѡ���˹ر� ��ô�˳�
		 */
		if (!switch_music.isChecked()) {
			return;
		}
		BKMusicManager manager = new BKMusicManager(CitySetting.this);
		if (!manager.fileExistLocation(musicInfo.get(musicSelectedIndex))) {
			Toast.makeText(CitySetting.this, "��ʼ���أ����Ժ�...", Toast.LENGTH_SHORT)
					.show();
		}
		manager.setBkGMusic(musicInfo.get(musicSelectedIndex),
				new OnDownloadFinished() {
					@Override
					public void onDownloadFinished(String path) {
						// Toast.makeText(CitySetting.this, "�������",
						// Toast.LENGTH_SHORT).show();
						SharedPreferenceUtil.setPreference(shared, musicSwitch,
								musicInfo.get(musicSelectedIndex)
										.getMusicName());
						_mApp.setBkgMusicEnabled(true);

						try {
							_mApp.getBkPlayer().release();
							MediaPlayer tempPlayer = new MediaPlayer();
							tempPlayer.setDataSource(getApplicationContext(),
									Uri.parse(path));
							tempPlayer.setLooping(true);
							tempPlayer.prepare();
							tempPlayer.start();
							_mApp.setBkPlayer(tempPlayer);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * �����б�����ȡ����ť��Ӧ����
	 */
	private void musicListNegativeBtnClicked() {
		if (_mApp.getListenPlayer() != null) {
			_mApp.getListenPlayer().release();
			_mApp.setListenPlayer(null);
		}
		if (_mApp.isBkgMusicEnabled() && !_mApp.getBkPlayer().isPlaying()) {
			_mApp.getBkPlayer().start();
		}
	}

	/**
	 * ����ָ��URL����������
	 */
	private void playListeningMusic(String url) {

	}

	/**
	 * ��JSOn���ݽ���Ϊ�����б�����
	 * 
	 * @param json
	 *            Ҫ������JSON
	 * @return �����б�
	 * @throws Exception
	 *             ����JSON����
	 */
	private ArrayList<MusicInfo> getMusicInfoList(String json) throws Exception {
		ArrayList<MusicInfo> tempList = new ArrayList<CitySetting.MusicInfo>();
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			MusicInfo tempInfo = new MusicInfo();
			tempInfo.setMusicName(((JSONObject) array.get(i)).getString("Name"));
			tempInfo.setMusicURL(((JSONObject) array.get(i)).getString("Path"));
			tempInfo.setMusicSize(((JSONObject) array.get(i)).getLong("Size"));
			tempList.add(tempInfo);
		}
		return tempList;
	}

	/**
	 * ��ʾע����Ϣ�Ի���
	 */
	private void ShowRegisterInfoDialog() {
		MpfMachine tempMachine = mms.getMpfMachine();
		final TextView tvSerialCode;
		final EditText edtBindingCode;
		Button btnChange;
		// �޸�ǰ�İ��룬��Ҫ�����ж��Ƿ��޸Ĺ�
		final String textBefor;

		LayoutInflater inflater = LayoutInflater.from(CitySetting.this);
		final View registerInfoView = inflater.inflate(
				R.layout.setting_activity_register_info, null);

		tvSerialCode = (TextView) registerInfoView
				.findViewById(R.id.tv_setting_activity_registerinfo_serialscode);		
		edtBindingCode = (EditText) registerInfoView
				.findViewById(R.id.tv_setting_activity_registerinfo_bindingcode);
		textBefor = edtBindingCode.getText().toString();
		btnChange = (Button) registerInfoView
				.findViewById(R.id.btn_setting_activity_registerinfo_bindingcode_change);
		btnChange.setVisibility(View.GONE);
		btnChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});

		if (tempMachine != null) {
			tvSerialCode.setText(tempMachine.getMachineSerialNumber());
			edtBindingCode.setText(tempMachine.getBindingPassword());
		}
		new AlertDialog.Builder(CitySetting.this)
				.setIcon(android.R.drawable.ic_dialog_info).setTitle("ע����Ϣ")
				.setView(registerInfoView).setNegativeButton(R.string.Cancle, null)
				.setPositiveButton(R.string.OK, new OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String textNew = edtBindingCode.getText().toString()
								.trim();
						if (!textBefor.equals(textNew)) {
							String postURL = DATA.CHANGEBINDCODE.replace("���к�",
									tvSerialCode.getText().toString().trim()) + textNew;
							AsyncTaskPost post = new AsyncTaskPost(
									new AsyncTaskPost.DoPostBack() {
										@Override
										public void PostBack(String result) {
											if (result.equals("true")) {
												writeNewBindCodeToDataBase(textNew);
												Toast.makeText(CitySetting.this,
														"�޸ĳɹ�", Toast.LENGTH_SHORT)
														.show();
											} else {
												Toast.makeText(CitySetting.this,
														"�޸�ʧ��,�����Ѵ��ڻ���������ַ�", Toast.LENGTH_SHORT)
														.show();
											}
										}

									});
							post.execute(postURL);
						}
					}
				}).show();
	}

	private void writeNewBindCodeToDataBase(String newBindCode) {
		MpfMachineService service = new MpfMachineService(CitySetting.this,
				DATA.DATAVERSION);
		service.MpfMachineUpdateBindCode(newBindCode);
	}

	private final class BindListAdapter extends BaseAdapter
	{
		private Context context;
		private List<String> text,openId;
		private String beShield;
		public BindListAdapter(Context context,List<String> text,List<String> openId) {
			this.context=context;
			this.text=text;
			this.openId=openId;
			beShield=context.getResources().getString(R.string.be_shield);
		}
		@Override
		public int getCount() {
			return  text.size();
		}

		@Override
		public Object getItem(int position) {
			return text.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv=null;
			if (convertView == null) {
				tv=new TextView(context);
				convertView=tv;			
				tv.setPadding(30, 15, 30, 15);
				tv.setTextColor(context.getResources().getColor(R.color.bule));
//				tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));				
			}
			else {
				tv=(TextView)convertView;
			}
			final String openID=openId.get(position);
			String append="";
			ViewHolder holder=new ViewHolder(openID);
			if (_mApp.getShieldOpenIdList().contains(openID)) {  //����һ�������ڵ�����
				holder.setShield(true);
				append=beShield;
				tv.setTextColor(context.getResources().getColor(R.color.black));
			}
			tv.setText(text.get(position)+append);						
			tv.setTag(holder);
			return tv;
		}
		
	}
	private final class ViewHolder
	{
		private String openId;
		private boolean shield;
		public ViewHolder(String openId) {
			this.openId=openId;
			this.shield=false;
		}
		public String getOpenId()
		{
			return openId;
		}
		public boolean isShield() {
			return shield;
		}
		public void setShield(boolean shield) {
			this.shield = shield;
		}
		
	}
	// /**
	// * ���ݴ���������ظ������ڵ�ѡ���ж�Ӧ��λ��
	// * @param str �����ļ��е�ֵ
	// * @return 4Ϊ�ر�
	// */
	// private int getDefaultSettingMusicPosition(String str)
	// {
	// if (str.equals("music1")) {
	// return 0;
	// }
	// else if (str.equals("music2")) {
	// return 1;
	// }
	// else if (str.equals("music3")) {
	// return 2;
	// }
	// else if(str.equals("false"))
	// {
	// return 3;
	// }
	// return 1;
	// }

}
