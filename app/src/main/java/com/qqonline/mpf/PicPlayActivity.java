package com.qqonline.mpf;

import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.Manager.db.MpfPictureService;
import com.qqonline.broadcast.PlayReceiver;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.ImageCompress;
import com.qqonline.modules.QRCodeModule.QRCodeUtil;
import com.qqonline.conmon.ThumbnailUtil;
import com.qqonline.conmon.UploadFileUtil;
import com.qqonline.conmon.async.AsyncDownload;
import com.qqonline.conmon.async.AsyncGet;
import com.qqonline.conmon.async.advertisement.AsyncAdPicURLTask;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.domain.MpfPicture;
import com.qqonline.mpf.baseActivity.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PicPlayActivity extends BaseActivity implements MediaPlayer.OnCompletionListener{
	/**
	 * 0:OpenID��String����Ҫ��ʾ�ĸ��û���OpenId���µ�����ͼƬ��Ϊ������ʾ�����û�ͼƬ
	 * 1:DbId(Long)�����ô�����ͼƬ��ʼ���ţ���Ҫ���ڹ�������ͼƬ���������е����ӦͼƬ��
	 * 				�ٸ���ͼƬDbId����ͼƬ���Ž��棬��������ͼƬΪ��һ�ſ�ʼ����
	 * 2:acti(boolean)���ж��Ƿ񼤻���ת������ǣ����ӳ�һС��ʱ�����ʾ��ά����棬
	 * 					������ǣ�����ʾ��ά�����
	 */
	public static final String[] INTENT_PARAMS={
			"OpenId",
			"DbId",
			"acti"
	};
	private static final int QRCODE=1;
	private static final int MENU_SETTING=1;
	private static final int MENU_PALY=0;

	/**
	 * ����ң������ͣ���󣬱�ʾ�Ƿ���ͣ�ı�־λ
	 */
	public static Boolean isStop=false;
//	/**
//	 * ���浱ǰ���������
//	 */
//	public static PicPlayActivity app;
	/**
	 * ��ʾ��ǰͼƬλ��ͼƬ�б�ĵڼ���
	 */	
	public  int currentPosition=0;
	/**
	 * ���ڱ�ʾ��ǰ�ӵ���ͼƬ������
	 */
	public static int newPictureRecieved=0;
	
	public TextView _tvMain;
	public WebView _wvMain;
	/**
	 * �û�ID������������ʾ��ͬ�û���ͼƬ
	 */
	public String checkOpenId = "";
	/**
	 * �ײ������ͼƬ�ؼ�
	 */
	public ImageView _ivAdvertisement;
	public WebView clockWv;
	/**
	 * ͼƬ�б�
	 */
	public List<MpfPicture> picList = null;

	/**
	 * ���ڿؼ�Gallery�ؼ���ͼƬ��ʾ����ʾ��Ч��Handlerʵ�� 
	 */
	public Handler autoGalleryHandler;
	
	
	/**
	 * ���ڲ���ͼƬ�Ŀؼ�
	 */
	private Gallery galleryPlay;
	/**
	 * ��ͼƬ�����Reciever������
	 */
	private PlayReceiver picReceiver=null;//�㲥������
	private WebView picWv;
	private VideoView videoMain;
	/**
	 * ��ʱ���б���Ҫ���ڶ�ʱ�л�����һ��ͼƬ��ʾ
	 */
	private List<Timer> autoGalleryList =  new ArrayList<Timer>();	
	/**
	 * ��ǰͼƬ�ǵڼ���
	 */
	int gallerypisition = 0;
	/**
	 * ��־λ����־ͼƬ������������������Ƿ���ʾ�ı�־λ�������ĸ���Ӧ�ĸ����棬���ˣ�
	 */
	private boolean isPlay = false,isPicAdPlay=false;		
	/**
	 * ���ڻ�ȡ��Ļ����
	 */
	private Display display;
	public ImageAdapter imageAdapter;
	private MediaController mediaCo;
	/**
	 * ���л���ͼƬ����ĳ�ʼ���ŵ�ͼƬID����Ҫ������ͼƬ������������һ��ͼƬ����������ͼƬ����ʼ��ʾ
	 */
	public long startPictureDbId=0;
	private final Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case QRCODE:
				showQRCodeDialog();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onDestroy() {
		unregisterReceiver(picReceiver);		
		app.setGalleryPause(false);
		picReceiver=null;
//		galleryPlay=null;
		picWv=null;
		_ivAdvertisement=null;
		clockWv=null;
		StopAutoChangePicture();
		autoGalleryList.clear();
		autoGalleryList=null;
//		display=null;
		_tvMain=null;
		_wvMain=null;
		
		super.onDestroy();
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TAG = "PicPlayActivity";
		noStateBar=true;
		super.onCreate(savedInstanceState);
//		app=this;
		setContentView(R.layout.play_main);	
		activity=this;
		isStop=app.isAdsIsPlayPicActive();
	//	new Thread(new getIsPlay(app)).start();//������ò�����Ƭ���
		isPlay = app.isAdsIsPlay();
		isPicAdPlay=app.isAdsIsPlayPicActive();
		display=getWindowManager().getDefaultDisplay();
		//showToast(String.valueOf(isPlay));
//		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//ȥ�������״̬��
		
		try {
			Bundle bundle=this.getIntent().getExtras();
			checkOpenId = bundle.getString(INTENT_PARAMS[0]);
			startPictureDbId=bundle.getLong(INTENT_PARAMS[1]);
			boolean isActi=bundle.getBoolean(INTENT_PARAMS[2], false);
			if (isActi) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
							handler.sendEmptyMessage(QRCODE);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//��̬ע��㲥
		picReceiver = new PlayReceiver(checkOpenId);
        IntentFilter intentFilter = new IntentFilter(DATA.BroadcastPlayActionName);
        intentFilter.setPriority(800);
        registerReceiver(picReceiver,intentFilter);
        intentFilter=null;
//        mps = new MpfPictureService(PicPlayActivity.this,DATA.DATAVERSION);
		galleryPlay = (Gallery) findViewById(R.id.galleryforplay);
		galleryPlay.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				gallerypisition=position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		mediaCo=new MediaController(this);
		mediaCo.setPrevNextListeners(new VideoNextListener(), new VideoPreListener());
		videoMain=(VideoView)findViewById(R.id.vdo_main);
		videoMain.setOnCompletionListener(this);
		videoMain.setMediaController(mediaCo);
	//	videoMain.setOnKeyListener(new );
		mediaCo.setMediaPlayer(videoMain);
		clockWv = (WebView) findViewById(R.id.clockwv);
		clockWv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		clockWv.getSettings().setJavaScriptEnabled(true); //����֧��js
		
		picWv = (WebView) findViewById(R.id.picwebview);
		picWv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		picWv.getSettings().setJavaScriptEnabled(true); //����֧��js
		
		_ivAdvertisement=(ImageView)findViewById(R.id.ivAdvertisement);
		_ivAdvertisement.setScaleType(ScaleType.FIT_XY);
		_ivAdvertisement.setAdjustViewBounds(true);
				
		if(this.getIntent().getExtras().getString("type").equals("clock"))
        {
			clockWv.setVisibility(View.VISIBLE);
			galleryPlay.setVisibility(View.GONE);
			picWv.setVisibility(View.GONE);
        }
        else
        {
        	clockWv.setVisibility(View.GONE);
    		galleryPlay.setVisibility(View.VISIBLE);
    		picWv.setVisibility(View.GONE);
 //   		if (isPicAdPlay) {
				AsyncAdPicURLTask pictask=new AsyncAdPicURLTask(PicPlayActivity.this,app);
				pictask.execute(DATA.HAdvertisementURL);
//			}
        	loadPic(true);
        }
		 autoGalleryHandler = new Handler() {
			public void handleMessage( Message message) {
				super.handleMessage(message);
				switch (message.what) {
				case 1:		
					if (galleryPlay == null) {
						return;
					}
					final int currentIndex=message.getData().getInt("pos");
					String url=picList.get(currentIndex).getPicUrl();
					if (url.startsWith("video:")) {     //�������Ƶ����ʾ��Ƶ
						StopAutoChangePicture();
						showVideo(url);
					}
					else {     //�����ͼƬ���л�ͼƬ
						Effect(currentIndex);//ͼƬ�л�Ч��
						new Handler().postDelayed(new Runnable() {  //��ʱ�л�ͼƬ���ڳ�ʱ��÷���Ч�����л�ͼƬ						
							@Override
							public void run() {
								
								// TODO Auto-generated method stub
								galleryPlay.setSelection(currentIndex);
								currentPosition=currentIndex;
//								int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
//								Log.i(TAG,String.valueOf(maxMemory));
								getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
							}
						}, 450);
					}										
				break;
				}
			}
		};
	}
	/**
	 * ����Ǽ�����ת������ʾ��ά�����
	 */
	public void showQRCodeDialog()
	{
		//����Ǽ�����ת������ʾ��ע���󶨵Ķ�ά��
		final ImageView tempView=new ImageView(PicPlayActivity.this);

		tempView.setPadding(0, 30, 0, 30);
		new AlertDialog.Builder(PicPlayActivity.this)
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
	public void showVideo(String url)
	{
		final String mid=url.replace("video:", "");
		String MPFRootPath= GetExtSDCardPath.getSDCardPath();
		String diretoryName=DATA.SDCARDVIDEOPATH;
		final String path=MPFRootPath+File.separator+diretoryName;
		File dir=new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		final File video=new File(path, mid+DATA.VIDEOTYPE);
		if (video.exists()) {
			videoMain.setOnErrorListener(new OnErrorListener() {				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					video.delete();
					downloadVideo(path,mid,DATA.VIDEOTYPE);
					return true;
				}
			});
			showLocalVideo(video.getAbsolutePath());
		}
		else {
			downloadVideo(path,mid,DATA.VIDEOTYPE);
		}
	}
	private void showLocalVideo(String path)
	{
		videoMain.setVisibility(View.VISIBLE);
		galleryPlay.setVisibility(View.GONE);
	//	galleryPlay.setSelection(gallerypisition+1);     
		videoMain.setVideoPath(path);
		
		videoMain.start();
	}
	private  void downloadVideo(String path, final String mid, String type) { // mid+".mp4";
		final String videoMid=mid;
		final AsyncDownload download = new AsyncDownload(path, mid + type,
				new AsyncDownload.DoPostBack() {

					@Override
					public void doPostBack(String result) {
						if(result.startsWith("error:"))
						{
							//���ض�ý���ļ�ʧ��,�ļ������ڻ�����ԭ��
							MpfPictureService service=new MpfPictureService(PicPlayActivity.this, DATA.DATAVERSION);
							service.delMpfPictureByPicUrl("video:"+mid);
							showNextImageOrVideo();
						}
						else
						{
							//��������Ƶ����ʾ
							showLocalVideo(result);
							//��������Ƶ���ϴ�һ������ͼ�������������ṩ΢�Ŷ˹�����������ʾ
							UploadVideoThumbnail(result,mid);
						}						
					}
				});
		AsyncGet get = new AsyncGet(PicPlayActivity.this,
				new AsyncGet.MyDoPostBack() {
					@Override
					public void DoPostBack(String json) {
						String accessToken = json;
						String downUrl=DATA.VIDIODOWNLOADURL.replace("%s1", accessToken).replace("%s2", videoMid);
						download.execute(downUrl);
					}
				});
		get.execute(DATA.ACCESSTOKENURL);

	}
	/**
	 * �ϴ���Ƶ������ͼ��������
	 * @param videoPath
	 */
	private void UploadVideoThumbnail(final String videoPath,final String videoName) {
		
		MpfPictureService service=new MpfPictureService(PicPlayActivity.this, DATA.DATAVERSION);
		final String openId=service.getPictureOpenIDByName("video:"+videoName);
		MpfMachineService machineService=new MpfMachineService(PicPlayActivity.this, DATA.DATAVERSION);
		final String machineId=String.valueOf(machineService.getMpfMachine().getDbId());
		service.release();
		machineService.release();
		service=null;
		machineService=null; 
		new Thread(new Runnable() {			
			@Override
			public void run() {
				Bitmap thumbs = ThumbnailUtil.GetVideoThumbnailByPath(videoPath,
						DATA.VIDEO_THUMBNAILS_WIDTH_TO_SERVICE,
						DATA.VIDEO_THUMBNAILS_HEIGHT_TO_SERVICE,
						Images.Thumbnails.MINI_KIND);
				thumbs=ImageCompress.CompositePictures(thumbs, app.getVideoIcon());
				UploadFileUtil.uploadImg(DATA.THUMBNAILS_UPLOAD_ADDRESS, thumbs, openId, machineId, videoName+DATA.PICTURETYPE);
			}
		}).start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(app.getVersionType() == DATA.VERSIONTYPE.SmartHome)
		{
			menu.add(0,MENU_PALY,MENU_PALY,R.string.menu_play_pause_continue);
			menu.add(0,MENU_SETTING,MENU_SETTING,R.string.menu_setting);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id=item.getItemId();
		switch (id)
		{
			case MENU_PALY:
				onKeyPauseClick();
				break;
			case MENU_SETTING:
				Intent intent=new Intent();
				intent.setClass(PicPlayActivity.this, CitySetting.class);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
    	switch(keyCode)
    	{
    	case KeyEvent.KEYCODE_C:
		case KeyEvent.KEYCODE_F9:
			//�л�������һ���õ���ĸC���ڶ����õ�F9
			if(app.getVersionType() == DATA.VERSIONTYPE.SmartHome)
			{
				change();
				return true;
			}
    	case KeyEvent.KEYCODE_BACK:
    		Log.i(TAG, "���˷��ؼ�");
			if(app.getVersionType() == DATA.VERSIONTYPE.SmartHome)
			{
				app.returnHome();
			}
			else {
				change();
			}
            return true;
    	case KeyEvent.KEYCODE_S:
		case KeyEvent.KEYCODE_F10:
			//��ͣ������һ���õ���ĸS���ڶ����õ�F10
			onKeyPauseClick();
    		    	return true;
    	}
        return super.onKeyDown(keyCode,event);
    }

	/**
	 * ��ͣ��ť��Ӧ�¼�
	 */
	private void onKeyPauseClick()
	{
		if (!isStop) {
			Toast.makeText(PicPlayActivity.this, String.valueOf("��ͣ����"), Toast.LENGTH_SHORT).show();
			app.setGalleryPause(true);
			for(Timer item :autoGalleryList)
			{
				item.cancel();//ȡ����֮ǰ�Ķ�ʱ��
				autoGalleryList.remove(item);
			}
			isStop=!isStop;

		} else {
			Toast.makeText(PicPlayActivity.this, String.valueOf("��������"), Toast.LENGTH_SHORT).show();
			app.setGalleryPause(false);
			for(Timer item :autoGalleryList)
			{
				item.cancel();//ȡ����֮ǰ�Ķ�ʱ��
				autoGalleryList.remove(item);
			}
			loopTimer();
			isStop=!isStop;
		}
	}
	private void onVideoKeyDown(int keyCode, KeyEvent event)
	{
		
	}
	/**
	 * �л�ʱ�Ӻ�ͼƬ�����б�
	 */
	public void change() {
		PicPlayActivity.this.finish();
	//	playAllPic();
		
	}
	public void showToast(String content){
		Toast.makeText(this, content, Toast.LENGTH_LONG).show();
	}
	/**
	 * TrueΪ��һ�μ��أ�����Timer��false���������Timer,TypeΪtrue������ʾ������Ƭ��false��������Ԥ��
	 * @param isFirst
	 * @param Type
	 */
	public void loadPic(boolean isFirst) {
		if (videoMain.getVisibility() == View.VISIBLE) {
			videoMain.stopPlayback();
		}
		videoMain.setVisibility(View.GONE);
		MpfPictureService mps = new MpfPictureService(PicPlayActivity.this,DATA.DATAVERSION);
		picList = mps.getPicture(checkOpenId);
		
		imageAdapter = new ImageAdapter(picList,PicPlayActivity.this,display);
		
		galleryPlay.setAdapter(imageAdapter);	
			clockWv.setVisibility(View.GONE);
    		galleryPlay.setVisibility(View.VISIBLE);
    		//if(isPlay)
    		for(Timer item :autoGalleryList)
    		{
    			item.cancel();//ȡ����֮ǰ�Ķ�ʱ��
    			autoGalleryList.remove(item);
    		}
    		String url=picList.get(0).getPicUrl();
    		if (url.startsWith("video:")) {     //�������Ƶ����ʾ��Ƶ
    			showVideo(url);
    		}
    		else {
    			gallerypisition++;
    			setGalleryStartPosition();
        		loopTimer();//����������ʱ��
			}   		
	}
	private void setGalleryStartPosition() {
		if (startPictureDbId != 0) {
			int position=0;
			if ((position=getStartPicturePosition(startPictureDbId)) != 0) {
				galleryPlay.setSelection(position);
				gallerypisition = position;
			}
		}
		else {
			galleryPlay.setSelection(0);
			gallerypisition = 0;
		}
	}
	private int getStartPicturePosition(long DbId)
	{
		for (int i = 0; i < picList.size(); i++) {
			MpfPicture temp=picList.get(i);
			if (temp.getDbId() == DbId) {
				return i;
			}
		}
		return 0;
	}
	/*
	 * ͼƬ�л���Ч
	 * */
	private void Effect(int index)
	{
		Animation animation=null;
		int rand=new Random().nextInt(7);
		switch (rand) {   //ͼƬ�л�Ч����������Ч����������
		case 0:
			animation = new AlphaAnimation(1f, 0f);  
			animation.setDuration(500);    
			break;
		case 1:
			animation = new TranslateAnimation(0,display.getWidth(), 0, 0);  
			animation.setDuration(500);   				
			break;
		case 2:
			animation = new ScaleAnimation(1f, 0.05f, 1f, 0.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);  
			animation.setDuration(550);  
			
			break;
		case 3:
			animation = new RotateAnimation(0f, +360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);  
			animation.setDuration(500);    			 
			break;
		case 4:
			animation = new TranslateAnimation(0,0, 0, display.getHeight());
			animation.setDuration(500);    			 
			break;
		case 5:
			animation = new TranslateAnimation(0,0, 0, -display.getHeight());
			animation.setDuration(500);    			 
			break;
		case 6:
			animation = new TranslateAnimation(0,-display.getWidth(), 0, 0); 
			animation.setDuration(500);    			 
			break;
		default:
			break;
		}
		galleryPlay.startAnimation(animation); 
	}
	public void StopAutoChangePicture()
	{
		if (autoGalleryList != null && autoGalleryList.size() > 0) {
			Log.i(TAG,"ֹͣ��ʱ�л�");
			try {
				for(Timer item :autoGalleryList)
				{
					item.cancel();//ȡ����֮ǰ�Ķ�ʱ��
					item=null;
					//autoGalleryList.remove(item);
				}
				autoGalleryList.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	/**
	 * ��Ӳ��ŵļƻ�����
	 */
	public void loopTimer() {
		try {
			Timer autoGallery = new Timer();
			autoGalleryList.add(autoGallery);
			autoGallery.schedule(new TimerTask() {
				@Override
				public void run() {
					if (gallerypisition < picList.size() - 1 ) {
						gallerypisition = gallerypisition + 1;
					} else {
						gallerypisition = 0;
					}
					handleMessage(gallerypisition);
				}
			}, 10000, 10000);//10���ÿ��10��ִ����Ϣ����
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void handleMessage(int pos)
	{
		Message msg = new Message();
		Bundle date = new Bundle();// �������
		date.putInt("pos", pos);
		msg.setData(date);
		msg.what = 1;//��Ϣ��ʶ
		autoGalleryHandler.sendMessage(msg);
		
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
			showNextImageOrVideo();
	}
	/**
	 * ������һ�ţ� �����һ����ͼƬ������ʾͼƬ�������һ������Ƶ���򲥷���Ƶ
	 */
	private void showNextImageOrVideo()
	{
		gallerypisition++;
		if (gallerypisition >= picList.size()) {
			gallerypisition=0;
		}
		String url=picList.get(gallerypisition).getPicUrl();
		if (url.startsWith("video:")) {
			showVideo(url);
		}
		else {
			videoMain.setVisibility(View.GONE);
			galleryPlay.setVisibility(View.VISIBLE);
			galleryPlay.setSelection(gallerypisition);
			loopTimer();
		}
	}
	/**
	 * ������һ�ţ� �����һ����ͼƬ������ʾͼƬ�������һ������Ƶ���򲥷���Ƶ
	 */
	private void showPreImageOrVideo()
	{
		gallerypisition--;
		if (gallerypisition < 0) {
			gallerypisition= picList.size()-1;
		}
		String url=picList.get(gallerypisition).getPicUrl();
		if (url.startsWith("video:")) {
			showVideo(url);
		}
		else {
			videoMain.setVisibility(View.GONE);
			galleryPlay.setVisibility(View.VISIBLE);
			galleryPlay.setSelection(gallerypisition);
			loopTimer();
		}
	}


	private final class VideoPreListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			
			showPreImageOrVideo();	
		}		
	}
	private final class VideoNextListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) {
			showNextImageOrVideo();	
		}		
	}
	
}
