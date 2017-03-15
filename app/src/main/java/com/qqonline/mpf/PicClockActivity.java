package com.qqonline.mpf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.qqonline.Manager.ActivationSyncManager;
import com.qqonline.Manager.ActivityManager;
import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.broadcast.ClockReciever;
import com.qqonline.broadcast.PicClockActivity_CityChangeReciever;
import com.qqonline.calendarcontrol.CalendarGridView;
import com.qqonline.calendarcontrol.CalendarGridViewAdapter;
import com.qqonline.calendarcontrol.NumberHelper;
import com.qqonline.conmon.AdvertisementAdapter;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.LunarUtil;
import com.qqonline.conmon.async.advertisement.AsyncAdPicURLTask;
import com.qqonline.conmon.async.advertisement.AsyncGetAdEnabledState;
import com.qqonline.conmon.async.weather.AsyncTaskAutoGetWeather;
import com.qqonline.domain.Weather;
import com.qqonline.modules.QRCodeModule.QRCodeUtil;
import com.qqonline.mpf.baseActivity.BaseActivity;
import com.qqonline.update.update;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * ʱ�ӽ�����
 * @author YE
 *
 */
public class PicClockActivity extends BaseActivity {
	/**
	 * ͬ�����ݿⵯ���ı�־��ֻ����һ�Σ���ʾ����ʱ���ж����ֵ�Ƿ����1������ʾ�������ֵ+1��
	 */
	public static int FLAG_ONCE_SHOW_SYNCDATABASE=1;
	public static final String[] INTENT_PARAMS={
			"isWeatherReady",
			"acti",
			"pingbao",
			"OpenId",
			"type"
	};
	private Window window;
	public String _openID="";
	private static final int[] _imageList={R.drawable.index};
	/**
	 * ����ʱ��ؼ��Ĵ������;�첽���������ؼ���Ϣ�������;�����Ը��������ؼ��������;
	 */
	private Handler _timeHandler;
	private MpfMachineService mms = null;
	//	private Handler handlerDelay;
//	public MPFApp app;
	private Runnable _timeRunnable;
	private TextView _tvTime,_tvLunarDate,_tvWeekDay,_tvDate,_tvCity;
	private TextView _tvTempToday,_tvTempTomorrow,_tvTempDayAfterTomorrow,_tvWeatherToday,_tvWeatherTomorrow,_tvWeatherDayAfterTomorrom;
	private LinearLayout _weatherLayout;
	private ImageView _ivWeatherToday,_ivWeatherTomorrow,_ivWeatherDayAfterTomorrom;
	public ImageView _ivAdvertisement;
	private ClockReciever clockReciever;
	private PicClockActivity_CityChangeReciever _cityChangeReciever;
	private int _width,_height;
	private AdvertisementAdapter _advertisementAdapter;
	private boolean _isFullscreen=false;
	private Handler _updataWeatherFirstStartHandler;
	private Runnable _updateWeatherFirstStartRunnable;
	/**
	 * ��ǰ���ţ��������ֵ���ж��Ƿ��ѹ�ȥһ�죬��ˢ��������ʱ��
	 * */
	private int nowDay=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	/**
	 * ��������ӳ��
	 * */
	private Map<Integer,String> _weakDayMap;
	/**
	 * �������ֺ�����ͼ���ӳ��
	 * */
	private Map<String, Integer> _weatherIcon;
	/**
	 * ������Activityʱ��������Bundle
	 */
	private Bundle _bundle;
	/**
	 * ��־����������ʱ��û�д�������Ϣ����
	 */
	private boolean _isWeatherReady;
	/**
	 * ������Ϣʵ���࣬�첽����������Ϣʱʹ��
	 */
	private Weather _weatherEntity;
	/**
	 * ���ڼ�������ҳ��Ŀؼ������ؼ������ص�������
	 * */
	public WebView _wvTemp;


	/**
	 * ��������ID
	 */
	private static final int CAL_LAYOUT_ID = 55;

	//����
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;
	GestureDetector mGesture = null;

	/**
	 * ���찴ť
	 */
	private Button mTodayBtn;

	/**
	 * ��һ���°�ť
	 */
	private ImageView mPreMonthImg;

	/**
	 * ��һ���°�ť
	 */
	private ImageView mNextMonthImg;

	/**
	 * ������ʾ��������� */
	private TextView mDayMessage;

	/**
	 * ����װ�������View
	 */
	private RelativeLayout mCalendarMainLayout;

	// ��������
	private PicClockActivity mContext = PicClockActivity.this;
//    /**
//     * ��һ����View
//     */
//    private GridView firstGridView;
//
//    /**
//     * ��ǰ��View
//     */
//    private GridView currentGridView;
//
//    /**
//     * ��һ����View
//     */
//    private GridView lastGridView;

	/**
	 * ��ǰ��ʾ������
	 */
	private Calendar calStartDate = Calendar.getInstance();

	/**
	 * ѡ�������
	 * */
	private Calendar calSelected = Calendar.getInstance();

	/**
	 * ����
	 */
	private Calendar calToday = Calendar.getInstance();

	/**
	 * ��ǰ����չʾ������Դ
	 * */
	private CalendarGridViewAdapter currentGridAdapter;

	/**
	 * Ԥװ����һ����չʾ������Դ
	 * */
	private CalendarGridViewAdapter firstGridAdapter;

	/**
	 * Ԥװ����һ����չʾ������Դ
	 * */
	private CalendarGridViewAdapter lastGridAdapter;
	//
	/**
	 * ��ǰ��ͼ��
	 */
	private int mMonthViewCurrentMonth = 0;

	/**
	 * ��ǰ��ͼ��
	 */
	private int mMonthViewCurrentYear = 0;

	/**
	 * ��ʼ��
	 */
	private int iFirstDayOfWeek = Calendar.MONDAY;
	/**
	 * ��־���ص��Ǵ�ʱ�ӽ��滹��С�ֱ���ʱ�ӽ���
	 */
	private boolean isBigClockActivity;
	/**
	 * �첽��������Ϣ
	 */
	private AsyncAdPicURLTask adPicUrl;
	/**
	 * ���������ʵ��
	 * */
	private update up;
	/**
	 * �첽��ȡ��濪��״̬
	 * */
	private  AsyncGetAdEnabledState getState;
	private AsyncTaskAutoGetWeather autoGetWeather;
	private PowerManager.WakeLock mWakeLock;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TAG="PicClockActivity";
		noStateBar=true;
		noTitleBar=true;
		noNavigationBar=true;
		super.onCreate(savedInstanceState);
		disableTitleBar();
//		/*���صײ�������*/
		window = getWindow();		WindowManager.LayoutParams params = window.getAttributes();
		params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		window.setAttributes(params);

		setContentView(R.layout.clock);
		activity=this;
		setTitle("����Ԥ��");

		if (FLAG_ONCE_SHOW_SYNCDATABASE <= 2) {
			//ÿ������ֻ��ʾһ��
			ActivationSyncManager manager=new ActivationSyncManager(this);
			manager.start();
			FLAG_ONCE_SHOW_SYNCDATABASE++;
		}
		PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP |
						PowerManager.SCREEN_DIM_WAKE_LOCK |
						PowerManager.ON_AFTER_RELEASE, "SimpleTimer");

		InitData();
		InitControls();
		InitWeather();
		/**
		 * ������Ϣ
		 */
		updateStartDateForMonth();
		generateContetView(mCalendarMainLayout);
		//�����·������л�����Ч
		slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

		_timeHandler.postDelayed(_timeRunnable, 2000);
//		_periodUpdataWeatherHandler.postDelayed(_periodUpdatWeatherRunnable, DATA.AutoUpdataWeatherIntevalHours * 60 * 60 * 1000/*Сʱ�� X ������ X ���� X ������*/);

		//��ȡ�����Ϣ��������ʾ���
		showAdvertisement();
		//	}




	}
	@Override
	protected void onResume() {
		checkUpdate(this);
		checkNetWork(this);
		if (mWakeLock != null && _bundle.getBoolean("pingbao",false))
		{
			mWakeLock.acquire();
		}
		super.onResume();
		ActivityManager.startWeatherService(this,TAG,null);


	}
	@Override
	protected void onPause() {
		if (mWakeLock != null && _bundle.getBoolean("pingbao",false)) {
			mWakeLock.release();
		}
		stopRequestAdvertisement();
		if (getState != null) {
			getState.stop();
		}
		_updataWeatherFirstStartHandler.removeCallbacks(_updateWeatherFirstStartRunnable);

		super.onPause();
	}
	private void stopRequestAdvertisement()
	{
		if (adPicUrl != null) {
			try {
				adPicUrl.release();
				adPicUrl=null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void showAdvertisement() {
		//ȡ����һ�ε������������û����ɵĻ���
		stopRequestAdvertisement();

		adPicUrl=new AsyncAdPicURLTask(PicClockActivity.this,app);
		adPicUrl.execute(DATA.HAdvertisementURL);

	}
	@Override
	protected void onRestart() {
		super.onRestart();

		showAdvertisement();

//		getState=new AsyncGetAdEnabledState(app);
//		getState.execute("");

		//checkUpdate(this);

		Time t=new Time();
		t.setToNow();
		int day=t.monthDay;
		if (nowDay != day) {
			nowDay=day;
			updateStartDateForMonth();
		}

	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(clockReciever);
		unregisterReceiver(_cityChangeReciever);
		app.removeActivity(this);
		super.onDestroy();
	}

	/**
	 * ��ʼ������
	 * */
	@SuppressLint("UseSparseArrays")
	private void InitData()
	{
//		handlerDelay=new Handler();
		_updataWeatherFirstStartHandler=new Handler();
		_updateWeatherFirstStartRunnable=new Runnable() {

			@Override
			public void run() {
				if (app.get_weatherInfoCache() == null) {
//					asyncGetWeather();
					String cityCache=app.get_cityNameCache();

					ActivityManager.startWeatherService(PicClockActivity.this,TAG,cityCache);

				}
				else {
					RefreshWeather();
				}
			}
		};
		clockReciever=new ClockReciever(PicClockActivity.this);
		IntentFilter intentFilter = new IntentFilter(DATA.BroadcastPicActionName);
		intentFilter.setPriority(800);
		registerReceiver(clockReciever,intentFilter);

		_cityChangeReciever=new PicClockActivity_CityChangeReciever(PicClockActivity.this, app);
		intentFilter = new IntentFilter(DATA.BroadcastPicClockActionName);
		intentFilter.setPriority(800);
		registerReceiver(_cityChangeReciever,intentFilter);
		_advertisementAdapter=new AdvertisementAdapter(PicClockActivity.this, _imageList);

		_weakDayMap=new HashMap<Integer,String>();
		_weakDayMap.put(2, "����һ");
		_weakDayMap.put(3, "���ڶ�");
		_weakDayMap.put(4, "������");
		_weakDayMap.put(5, "������");
		_weakDayMap.put(6, "������");
		_weakDayMap.put(7, "������");
		_weakDayMap.put(1, "������");

		_weatherIcon=new HashMap<String, Integer>(){
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Integer get(Object key) {
				if (super.get(key) == null) {
					return weatherFuzzyMaching((String)key);
				}
				else {
					return super.get(key);
				}

			}

		};
		_weatherIcon.put("С��", R.drawable.xiaoyu);
		_weatherIcon.put("����", R.drawable.duoyun);
		_weatherIcon.put("����", R.drawable.dayu);
		_weatherIcon.put("����", R.drawable.binbao);
		_weatherIcon.put("��ѩ", R.drawable.daxue);
		_weatherIcon.put("����", R.drawable.xiaoyu);
		_weatherIcon.put("������", R.drawable.lezhenyu);
		_weatherIcon.put("��", R.drawable.mai);
		_weatherIcon.put("��", R.drawable.qingtian);
		_weatherIcon.put("����", R.drawable.qingtian);
		_weatherIcon.put("��", R.drawable.wu);
		_weatherIcon.put("����", R.drawable.wu);
		_weatherIcon.put("Сѩ", R.drawable.xiaoxue);
		_weatherIcon.put("��ѩ", R.drawable.zhongxue);
		_weatherIcon.put("��", R.drawable.yin);
		_weatherIcon.put("����", R.drawable.zhongyu);
		_weatherIcon.put("С������", R.drawable.zhongyu);
		_weatherIcon.put("����תС��", R.drawable.xiaoyu);
		_weatherIcon.put("�е�����", R.drawable.dayu);
		_weatherIcon.put("����ת����", R.drawable.zhongyu);
		_weatherIcon.put("��תС��", R.drawable.xiaoyu);
		_weatherIcon.put("��ת����", R.drawable.duoyun);
		_weatherIcon.put("С��ת��", R.drawable.yin);
		_weatherIcon.put("С��ת����", R.drawable.duoyun);
		_weatherIcon.put("����תС��", R.drawable.xiaoyu);
		_weatherIcon.put("����ת��", R.drawable.duoyuzhuanqing);
		_weatherIcon.put("��ת����", R.drawable.duoyuzhuanqing);
		_weatherIcon.put("��תС��", R.drawable.qingzhuanxiaoyu);
		_weatherIcon.put("С��ת��", R.drawable.qingzhuanxiaoyu);

		_bundle=getIntent().getExtras();
		if (_bundle == null) {
			_bundle = new Bundle();
		}
		if(_bundle != null)
		{
			_isWeatherReady=_bundle.getBoolean("isWeatherReady",false);
			if (_bundle.getBoolean("acti",false)) {
				//����Ǽ�����ת������ʾ��ע���󶨵Ķ�ά��?
				final ImageView tempView=new ImageView(PicClockActivity.this);
				tempView.setImageResource(R.drawable.download);
				tempView.setPadding(0, 30, 0, 30);
				new AlertDialog.Builder(PicClockActivity.this)
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
					public void OnError(int code,String error) {

					}
				});
				qrUtil.asyncGetBitmap();
			}
//			else if(!_bundle.getBoolean("pingbao",false)){
//				change();
//			}
		}
		if (app.get_weatherInfoCache()!=null) {
			_isWeatherReady=true;
			_weatherEntity=app.get_weatherInfoCache();
		}
		else
		{
			_isWeatherReady=false;
		}


		mms = new MpfMachineService(PicClockActivity.this, DATA.DATAVERSION);
		if(mms.MpfMachineIsHave())
		{
			if (app.getBkPlayer() == null) {
				MediaPlayer player;
				if (app.getBkgMusicName()!=null) {
					File temp=new File(DATA.MUSICCACHEPATH, app.getBkgMusicName());
					if (!temp.exists()) {
						player=MediaPlayer.create(this, R.raw.bkmusic);
					}
					else {
						player=MediaPlayer.create(this, Uri.parse(temp.getAbsolutePath()));
					}
					player.setLooping(true);
					app.setBkPlayer(player);
					if (app.isBkgMusicEnabled() && !app.getBkPlayer().isPlaying()) {
						app.getBkPlayer().start();
					}
				}
				else {
					player=MediaPlayer.create(this, R.raw.bkmusic);
					player.setLooping(true);
					app.setBkPlayer(player);
					if (app.isBkgMusicEnabled() && !app.getBkPlayer().isPlaying()) {
						app.getBkPlayer().start();
					}
				}


			}
			else {
				if (app.isBkgMusicEnabled() && !app.getBkPlayer().isPlaying()) {
					app.getBkPlayer().start();
				}
			}
//			BkgMusic music=new BkgMusic(PicClockActivity.this);
//			music.play();
		}
	}

	/**
	 * ��ʼ���ؼ���Handler�Ͷ�ʱ��
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void InitControls()
	{
		_ivAdvertisement=(ImageView)findViewById(R.id.ivAdver);
		_ivAdvertisement.setScaleType(ScaleType.FIT_XY);
		_ivAdvertisement.setAdjustViewBounds(true);

		_weatherLayout=(LinearLayout)findViewById(R.id.lyWeather);

		_tvDate=(TextView)findViewById(R.id.tvData);
		_tvWeekDay=(TextView)findViewById(R.id.tvWeek);
		_tvLunarDate=(TextView)findViewById(R.id.tvLunarData);
		_tvTime=(TextView)findViewById(R.id.tvTime);
		_tvCity=(TextView)findViewById(R.id.tvCity);
		_tvTempToday=(TextView)findViewById(R.id.tvTemp);
		_tvTempTomorrow=(TextView)findViewById(R.id.tvTTemp);
		_tvTempDayAfterTomorrow=(TextView)findViewById(R.id.tvATTemp);
		_tvWeatherToday=(TextView)findViewById(R.id.tvWeather);
		_tvWeatherTomorrow=(TextView)findViewById(R.id.tvTWeather);
		_tvWeatherDayAfterTomorrom=(TextView)findViewById(R.id.tvATWeather);

		_ivWeatherToday=(ImageView)findViewById(R.id.ivWeatherIcon);
		_ivWeatherTomorrow=(ImageView)findViewById(R.id.ivTWeatherIcon);
		_ivWeatherDayAfterTomorrom=(ImageView)findViewById(R.id.ivATWeatherIcon);

//		 mTodayBtn = (Button) findViewById(R.id.today_btn);
		mDayMessage = (TextView) findViewById(R.id.day_message);
		mCalendarMainLayout = (RelativeLayout) findViewById(R.id.calendar_main);
		mPreMonthImg = (ImageView) findViewById(R.id.left_img);
		mNextMonthImg = (ImageView) findViewById(R.id.right_img);


		mPreMonthImg.setOnClickListener(onPreMonthClickListener);
		mNextMonthImg.setOnClickListener(onNextMonthClickListener);

		_timeHandler=new Handler();
		_timeRunnable=new Runnable() {

			@Override
			public void run() {
				SetDate();
			}
		};
		Configuration mConfiguration = this.getResources().getConfiguration(); //��ȡ���õ�������Ϣ
		int ori = mConfiguration.orientation ; //��ȡ��Ļ����
		if (ori == Configuration.ORIENTATION_PORTRAIT) {
//			_weatherLayout.setPadding(_weatherLayout.getPaddingLeft(), _weatherLayout.getPaddingTop()+100, _weatherLayout.getPaddingRight(), _weatherLayout.getPaddingBottom()-100);
			try {
				RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)_weatherLayout.getLayoutParams();
				params.setMargins(0,100,0,0);
				_weatherLayout.setLayoutParams(params);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	/**
	 * ����ͼ���ģ��ƥ��
	 * ���������о�ȷƥ�䲻����Ӧ��ͼ�꣬�����ģ��ƥ��
	 * @return ��Ӧ�����ؼ��ֵ�ͨ��ͼ��id
	 */
	private Integer weatherFuzzyMaching(String weather)
	{
		if (weather.contains("����")) {
			return R.drawable.baoyu;
		}
		else if(weather.contains("��"))
		{
			return R.drawable.yin;
		}
		else if(weather.contains("��"))
		{
			return R.drawable.zhongyu;
		}
		else if(weather.contains("ѩ"))
		{
			return R.drawable.zhongxue;
		}
		else if(weather.contains("��"))
		{
			return R.drawable.wu;
		}
		else if(weather.contains("��"))
		{
			return R.drawable.mai;
		}
		else if(weather.contains("��"))
		{
			return R.drawable.mai;
		}
		return R.drawable.qingtian;
	}
	/**
	 * ���ݸı�����ڸ�������
	 * ��������ؼ���
	 */
	private void updateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // ���óɵ��µ�һ��
		mMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// �õ���ǰ������ʾ����
		mMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);// �õ���ǰ������ʾ����

		String s = calStartDate.get(Calendar.YEAR)
				+ "-"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate
				.get(Calendar.MONTH) + 1);
		mDayMessage.setText(s);
		// ����һ��2 ��������1 ���ʣ������
		int iDay = 0;
		int iFirstDayOfWeek = Calendar.MONDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

	}
	/**
	 * ��Ҫ�������ɷ�ǰչʾ������View
	 *
	 * @param layout ��Ҫ����ȥ���صĲ���
	 */
	private void generateContetView(RelativeLayout layout) {
		try {
			// ����һ����ֱ�����Բ��֣��������ݣ�
			if (layout.getChildCount() >0) {
				layout.removeAllViews();
			}
			viewFlipper = new ViewFlipper(this);
			viewFlipper.setId(CAL_LAYOUT_ID);
			calStartDate = getCalendarStartDate();
			CreateGirdView();
			RelativeLayout.LayoutParams params_cal = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layout.addView(viewFlipper, params_cal);

			LinearLayout br = new LinearLayout(this);
			RelativeLayout.LayoutParams params_br = new RelativeLayout.LayoutParams(
					LayoutParams.FILL_PARENT, 1);
			params_br.addRule(RelativeLayout.BELOW, CAL_LAYOUT_ID);
			br.setBackgroundColor(getResources().getColor(R.color.calendar_background));
			layout.addView(br, params_br);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * ���ڼ��ص���ǰ�����ڵ��¼�
	 */
	private View.OnClickListener onTodayClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			calStartDate = Calendar.getInstance();
			updateStartDateForMonth();
			generateContetView(mCalendarMainLayout);
		}
	};
	@SuppressLint("SimpleDateFormat")
	private void SetDate()
	{
		Date date=new Date();
		Calendar calendar=Calendar.getInstance();
		SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy��MM��dd��");

		LunarUtil lunar=new LunarUtil(calendar);
		String timeString=timeFormat.format(date);
		String dateString=dateFormat.format(date);
		int dayNumber=calendar.get(Calendar.DAY_OF_WEEK);
		int tempNow=calendar.get(Calendar.DAY_OF_MONTH);
		if (tempNow != nowDay) {
			nowDay=tempNow;
			updateStartDateForMonth();
			generateContetView(mCalendarMainLayout);
		}

		_tvTime.setText(timeString);
		_tvDate.setText(dateString);
		_tvWeekDay.setText(_weakDayMap.get(dayNumber));
		_tvLunarDate.setText(lunar.toString());
		_timeHandler.postDelayed(_timeRunnable, 2000);
	}
	/**
	 *  ��ʼ��������Ϣ
	 */
	private void InitWeather()
	{
		SetDate();
		if (_isWeatherReady) {
			SetWeather();
		} else {
			Toast.makeText(PicClockActivity.this, "����������Ϣ�У����Ժ�...", Toast.LENGTH_SHORT).show();
			//RefreshWeather();
//			AsyncTaskAutoGetWeather autoGetWeather=new AsyncTaskAutoGetWeather(PicClockActivity.this);
//			autoGetWeather.execute(DATA.CityCodeURL);
			_updataWeatherFirstStartHandler.postDelayed(_updateWeatherFirstStartRunnable, DATA.PeriodWhenFirstStartUpdateWeatherFail);
		}
	}
	/**
	 * �첽������������
	 * */
	private void RefreshWeather()
	{
		Weather temp=app.get_weatherInfoCache();

		if(temp!=null) {
			_weatherEntity=temp;
			SetWeather();
		}

	}
	/**
	 * ���⿪�ŵ������������ݵĽӿ�
	 * * @param weather
	 */
	public void SetWeather(Weather weather)
	{
		Weather temp=weather;
		_tvCity.setText(temp.get_city());
		_tvLunarDate.setText(temp.get_lunarCalendar());
		_tvTempToday.setText(temp.get_temp()[0]);
		_tvTempTomorrow.setText(temp.get_temp()[1]);
		_tvTempDayAfterTomorrow.setText(temp.get_temp()[2]);
		Integer imageID1,imageID2,imageID3;
		imageID1=_weatherIcon.get(temp.get_weather()[0]);
		if (imageID1 != null) {
			_ivWeatherToday.setImageResource(imageID1);
		}
		imageID2=_weatherIcon.get(temp.get_weather()[1]);
		if (imageID2 != null) {
			_ivWeatherTomorrow.setImageResource(imageID2);
		}
		imageID3=_weatherIcon.get(temp.get_weather()[2]);
		if (imageID3 != null) {
			_ivWeatherDayAfterTomorrom.setImageResource(imageID3);
		}
		_tvWeatherToday.setText(temp.get_weather()[0]);
		_tvWeatherTomorrow.setText(temp.get_weather()[1]);
		_tvWeatherDayAfterTomorrom.setText(temp.get_weather()[2]);
		//	Toast.makeText(PicClockActivity.this, "���������ɹ�"?, Toast.LENGTH_SHORT).show();

	}
	/**
	 * ������������ */
	private void SetWeather()
	{
		//Weather weather=app.get_weatherInfoCache();
		_tvCity.setText(_weatherEntity.get_city());
		_tvLunarDate.setText(_weatherEntity.get_lunarCalendar());
		_tvTempToday.setText(_weatherEntity.get_temp()[0]);
		_tvTempTomorrow.setText(_weatherEntity.get_temp()[1]);
		_tvTempDayAfterTomorrow.setText(_weatherEntity.get_temp()[2]);
		Integer imageID1,imageID2,imageID3;
		imageID1=_weatherIcon.get(_weatherEntity.get_weather()[0]);
		if (imageID1 != null) {
			_ivWeatherToday.setImageResource(imageID1);
		}
		imageID2=_weatherIcon.get(_weatherEntity.get_weather()[1]);
		if (imageID2 != null) {
			_ivWeatherTomorrow.setImageResource(imageID2);
		}
		imageID3=_weatherIcon.get(_weatherEntity.get_weather()[2]);
		if (imageID3 != null) {
			_ivWeatherDayAfterTomorrom.setImageResource(imageID3);
		}
		_tvWeatherToday.setText(_weatherEntity.get_weather()[0]);
		_tvWeatherTomorrow.setText(_weatherEntity.get_weather()[1]);
		_tvWeatherDayAfterTomorrom.setText(_weatherEntity.get_weather()[2]);
//		Toast.makeText(PicClockActivity.this, R.string.weather_controls_refresh, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(_bundle.getBoolean("pingbao",false))
		{
			finish();
			app.returnHome();
		}
		switch (keyCode) {
			case KeyEvent.KEYCODE_C:
			case KeyEvent.KEYCODE_F9:
			case KeyEvent.KEYCODE_BACK:
				Log.i(TAG, "���˷��ؼ�");
//				change();
				ActivityManager.startPicPlayActivity(this,0,null,null);
				return true;
			case KeyEvent.KEYCODE_MENU:
				ActionBar action=getSupportActionBar();
				if (action!=null) {
					action.show();
				}
				return false;
			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * ����ͼƬ����
	 */
	/*private void playAllPic() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("OpenId", "");
		bundle.putString("type", "");
		bundle.putLong("DbId", 0);
		intent.putExtras(bundle);
		intent.setClassName(PicClockActivity.this, "com.qqonline.mpf.PicPlayActivity2");
		startActivity(intent);
	}*/
	/**
	 * �л���ͼƬ���Ž���
	 */
	public void change()
	{
//		playAllPic();
		ActivityManager.startPicPlayActivity(this,0,null,null);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ActionBar action=getSupportActionBar();
		if (action!=null) {
			action.hide();
		}
		switch (item.getItemId()) {
			case R.id.iClockSetting:
				Intent intent=new Intent();
				intent.setClass(PicClockActivity.this, CitySetting.class);
				startActivityForResult(intent, 200);
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.clockmenu, menu);
		//menu.add(Menu.NONE, Menu.FIRST + 3, 1, " ");
		//menu.add(Menu.NONE, Menu.FIRST + 1, 2, "ɾ��").setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction()==MotionEvent.ACTION_DOWN) {
			ActionBar action=getSupportActionBar();
			if (action!=null) {
				action.hide();
			}
		}
		return super.onTouchEvent(event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 200 && resultCode==RESULT_OK) {
//			String temp=data.getStringExtra("cityCode");
//			Log.i(TAG,"���д��룺"+temp);
//			//Toast.makeText(PicClockActivity.this, temp, Toast.LENGTH_SHORT).show();
//			AsyncTaskGetWeather weather=new AsyncTaskGetWeather(PicClockActivity.this.app);
//			weather.execute(DATA.WeatherURL+temp+".html");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ���ڻ�ȡ��ǰ��ʾ�·ݵ�ʱ��
	 *
	 * @return ��ǰ��ʾ�·ݵ�ʱ��
	 */
	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		return calStartDate;
	}
	/**
	 * ���ڴ�����ǰ��Ҫ����չʾ��View
	 */
	private void CreateGirdView() {

		GridView	firstGridView=null,currentGridView=null,lastGridView=null;
		Calendar firstCalendar = Calendar.getInstance(); // ��ʱ
		Calendar currentCalendar = Calendar.getInstance(); // ��ʱ
		Calendar lastCalendar = Calendar.getInstance(); // ��ʱ
		firstCalendar.setTime(calStartDate.getTime());
		currentCalendar.setTime(calStartDate.getTime());
		lastCalendar.setTime(calStartDate.getTime());

		try {

			firstGridView = new CalendarGridView(mContext);
			currentGridView = new CalendarGridView(mContext);
			lastGridView = new CalendarGridView(mContext);

			firstCalendar.add(Calendar.MONTH, -1);
			firstGridAdapter = new CalendarGridViewAdapter(this, firstCalendar,isBigClockActivity);
			firstGridView.setAdapter(firstGridAdapter);// ���ò˵�Adapter
			//      firstGridView.setId(CAL_LAYOUT_ID);    //�Ӹ�����������ظ�View��ID�Ĵ���




			currentGridAdapter = new CalendarGridViewAdapter(this, currentCalendar,isBigClockActivity);
			currentGridView.setAdapter(currentGridAdapter);// ���ò˵�Adapter
			//   currentGridView.setId(CAL_LAYOUT_ID); //�Ӹ�����������ظ�View��ID�Ĵ���




			lastCalendar.add(Calendar.MONTH, 1);
			lastGridAdapter = new CalendarGridViewAdapter(this, lastCalendar,isBigClockActivity);
			lastGridView.setAdapter(lastGridAdapter);// ���ò˵�Adapter
			//  lastGridView.setId(CAL_LAYOUT_ID); //�Ӹ�����������ظ�View��ID�Ĵ���

		} catch (Exception e) {
			e.printStackTrace();
		}



		//       currentGridView.setOnTouchListener(this);
		//       firstGridView.setOnTouchListener(this);
		//       lastGridView.setOnTouchListener(this);

		if (viewFlipper.getChildCount() != 0) {
			viewFlipper.removeAllViews();
		}
		try {
			viewFlipper.addView(currentGridView);
			viewFlipper.addView(lastGridView);
			viewFlipper.addView(firstGridView);
		} catch (Exception e) {
			// TODO: handle exception
		}


		String s = calStartDate.get(Calendar.YEAR)
				+ "-"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate
				.get(Calendar.MONTH) + 1);
		mDayMessage.setText(s);
	}
	/**
	 * ���ڼ�����һ�������ڵ��¼�
	 */
	private View.OnClickListener onPreMonthClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			viewFlipper.setInAnimation(slideRightIn);
			viewFlipper.setOutAnimation(slideRightOut);
			viewFlipper.showPrevious();
			setPrevViewItem();
		}
	};
	/**
	 * ��һ����
	 */
	private void setPrevViewItem() {
		mMonthViewCurrentMonth--;// ��ǰѡ����--
		// �����ǰ��Ϊ�����Ļ���ʾ��һ��
		if (mMonthViewCurrentMonth == -1) {
			mMonthViewCurrentMonth = 11;
			mMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1); // ������Ϊ����1��
		calStartDate.set(Calendar.MONTH, mMonthViewCurrentMonth); // ������
		calStartDate.set(Calendar.YEAR, mMonthViewCurrentYear); // ������

	}
	/**
	 * ���ڼ�����һ�������ڵ��¼�
	 */
	private View.OnClickListener onNextMonthClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			viewFlipper.setInAnimation(slideLeftIn);
			viewFlipper.setOutAnimation(slideLeftOut);
			viewFlipper.showNext();
			setNextViewItem();
		}
	};
	/**
	 * ��һ����
	 */
	private void setNextViewItem() {
		mMonthViewCurrentMonth++;
		if (mMonthViewCurrentMonth == 12) {
			mMonthViewCurrentMonth = 0;
			mMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, mMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, mMonthViewCurrentYear);

	}


}
