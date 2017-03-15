package com.qqonline.mpf;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.qqonline.Manager.ActivityManager;
import com.qqonline.Manager.CopyCacheToSDCard;
import com.qqonline.Manager.LoadSetting;
import com.qqonline.Manager.RebootManager;
import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.Manager.db.MpfPictureService;
import com.qqonline.Manager.db.MpfUserService;
import com.qqonline.broadcast.BindCodeUpdateReciever;
import com.qqonline.broadcast.MainReciever2;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.async.scanSDCard.UpdataImportPictureWhileStart;
import com.qqonline.conmon.getMd5;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.domain.MpfPicture;
import com.qqonline.error.MPFUnCatchHandler;
import com.qqonline.interfaces.GetWeather;
import com.qqonline.mpf.baseActivity.BaseActivity;
import com.qqonline.service.WeatherRefreshService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * �����
 *
 * @author fengcheng.ye, YE
 */
@SuppressLint("ShowToast")
public class MainActivity extends BaseActivity {
    //��̬����
    /**
     * �ӳټ���Ƿ񼤻�Handler����ҪĿ������ʾ��������
     */
    public static final int HANDLER_DELAY_ACTIVATION = 0;

    //��̬��������

    //����
    //��������

    //��ͨ����
    private MpfMachineService mms = null;
    private MpfUserService mus = null;

    //	private MainReceiver mainReceiver = null;// �㲥������
    private MainReciever2 mainReciever2 = null;
    /**
     * ���°���Ľ�����
     */
    private BindCodeUpdateReciever bindReciever = null;
    /**
     * ���ڸ�������
     */
    private Handler _periodUpdataWeatherHandler;
    /**
     * ��ʱ��������
     */
    private Runnable _periodUpdataWeatherRunable;

    /**
     * ��̨�����̵߳����ã�δʹ��
     */
    private ServiceConnection conn;

    /**
     * ��ȡ�������첽��
     */
    private GetWeather get;
    /**
     * ���ݼ����̻߳ص�Hander
     */
    public CallBackHandler delayHandler;
    //��ͨ��������

    //�Զ����ڲ���

    /**
     * �Զ���Handler�࣬
     * ��Ҫ���ã��������ݼ����̵߳Ļص�
     */
    private static class CallBackHandler extends Handler {
        private WeakReference<MainActivity> activity;

        public CallBackHandler(MainActivity activity) {
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            //TODO:����Handler���¼���Ӧ
            final int id = msg.what;
            switch (id) {
                case MainActivity.HANDLER_DELAY_ACTIVATION:
                    activity.get().checkActivated();
                    break;
                default:
                    break;
            }
        }
    }
    //�Զ����ڲ������


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = "MainActivity";
        noTitleBar = true;
        noNavigationBar = true;
        noStateBar = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        RebootManager.setRebootAlarm(this);

        //������
        checkUpdate(this);

        //��ʼ������ؼ��ȱ���
        initDate();

        //��ʼ��δ�����쳣Handler
        initUnCatchHandler();

        //ͬ��SD���ĵ���ͼƬ
        SyncSDCardCacheFile();

        //����ƫ������
        LoadSetting setting = new LoadSetting(MainActivity.this);
        setting.load(); // ����ƫ������

        //���µ����ͼƬ�ļ���
        UpdataImportPictureWhileStart update = new UpdataImportPictureWhileStart(MainActivity.this);
        update.execute("");   //���µ����ͼƬ�ļ���

        //����Ĭ�ϵĹ��ͼƬ
        LoadDefaultPicture();

//		//����Ƿ���Ҫ��SD��ͬ�������¼�������������ݿ���
//		ActivationSyncManager activationSyncManager=new ActivationSyncManager(this);
//		activationSyncManager.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //�ӳټ���Ƿ񼤻��ҪĿ������ʾ�������������
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                delayHandler.sendEmptyMessage(HANDLER_DELAY_ACTIVATION);
            }
        }).start();
//		checkActivated();


        //TODO:�������̼߳������ݵĺ���ת���¿��߳���ȥ�����Ż�����򿪵�������
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        delayHandler.sendEmptyMessage(HANDLER_DELAY_ACTIVATION);
        app.getActivityList().clear();
        app.addActivity(this);
    }

    @Override
    protected void onStop() {
        /*
		* �������ӳٵ�����£���ȡ��������ʱ���ܿ����ڽ����л�ʱ��
		* ��������δ��ɣ������ͻῨס���棨��������������Ҫ��������������ͷ�
		*
		* */
        if (get != null) {
            get.release();
        }
        super.onStop();
    }

    /**
     * ��ʼ����δ�������Ĵ���
     */
    private void initUnCatchHandler() {
        MPFUnCatchHandler handler = new MPFUnCatchHandler(app);
        Thread.setDefaultUncaughtExceptionHandler(handler);

    }

    /**
     * �����Ҫ��ͬ�����ش洢�е����ݵ�SD��
     */
    private void SyncSDCardCacheFile() {
        CopyCacheToSDCard copy = new CopyCacheToSDCard(MainActivity.this);
        boolean isCacheCopyNeeded = copy.isNeedCopy();
        if (isCacheCopyNeeded) {
            copy.copy();
        } /*else { // �������Ҫ�����������ݣ���ֱ���жϼ���״̬�������Ҫ�����������ݵ�����SD���������첽�������������ݺ����ж�
			checkActivated();
		}*/
    }

    /**
     * ��ʼ���������
     */
    private void initDate() {
        app.setmAcyivity(MainActivity.this);
        app.setVideoIcon(getResources());

        delayHandler = new CallBackHandler(this);

//		RefreshWeather();
        // ��ʱ������ʱ������������
        _periodUpdataWeatherHandler = new Handler();
        _periodUpdataWeatherRunable = new Runnable() {

            @Override
            public void run() {
                RefreshWeather();
                _periodUpdataWeatherHandler.postDelayed(this,
                        (DATA.AutoUpdataWeatherIntevalHours * 60 * 60 * 1000));
            }
        };
        _periodUpdataWeatherHandler.postDelayed(_periodUpdataWeatherRunable,
                (DATA.AutoUpdataWeatherIntevalHours * 60 * 60 * 1000 - 5000));
        // ��̬ע��㲥
//		mainReceiver = new MainReceiver(MainActivity.this);
        mainReciever2 = new MainReciever2(MainActivity.this);
        IntentFilter intentFilter = new IntentFilter(
                DATA.BroadcastMainActionName);
        intentFilter.setPriority(1000);
        registerReceiver(mainReciever2, intentFilter);
        bindReciever = new BindCodeUpdateReciever(MainActivity.this);
        intentFilter = new IntentFilter(
                DATA.BroadcastBindCodeName);
        intentFilter.setPriority(800);
        registerReceiver(bindReciever, intentFilter);
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //WsBinder binder = (WsBinder) service;
                //WsBinder binder = service.
                //socketIoClient = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
    }


    /**
     * �жϻ����Ƿ��Ѽ��ĩ��������ת������ҳ�棬�Ѽ��������ʱ�ӽ���
     */
    public void checkActivated() {
        if (mms == null) {
            mms = new MpfMachineService(MainActivity.this, DATA.DATAVERSION);
        }
        if (mus == null) {
            mus = new MpfUserService(MainActivity.this, DATA.DATAVERSION);
        }

        if (!mms.MpfMachineIsHave()) {
            ActivityManager.startActiActivityForResult(this, 200);// ����û�м��������ü���ҳ��
        } else {

            // ����Service��socket.io�ͻ��ˣ�
            Intent service = new Intent(MainActivity.this, SocketIoClient.class);
            startService(service);
            // ��ȡ�󶨵�service��SocketIoClient��
            //this.bindService(service, conn, Context.BIND_AUTO_CREATE);
            // �����û���Ƭ�б�
            //loadUser();
//            ActivityManager.startClockActivity(this, true);
            ActivityManager.startPicPlayActivityAndClockHistory(this, false);
            //TODO:�Ż�������תģ��
        }
    }

    /**
     * ������ݿ��ͼƬ�ļ� ���SD����û����Щ�����ļ����������洢������Щ�����ļ����򽫱����洢�е��ļ�ȫ�����Ƶ�SD���ϣ�
     * ��Ϊ���������ɨ��SD���������SD����û�ҵ���Щ�ļ������϶�Ϊĩ��������Ա����洢�е�����
     */
    private boolean checkCacheFile() {
        String path = GetExtSDCardPath.getSDCardPath();
        String internalSDCardPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + DATA.sdCardPicFile;
        if (path.equals(internalSDCardPath)) { // �����ȣ����ʾû��������SD��֮�֣��˳�
            return false;
        }
        File internalSDCardDirectery = new File(internalSDCardPath);
        File externalSDCardDirectery = new File(path);
        boolean isExternalCacheExist = false, isInternalCacheExist = false;
        // File test=null;
        if (!externalSDCardDirectery.exists()) {// ��������SD�������ڣ�Ҳ����ֻ��û����������ļ���
            externalSDCardDirectery.mkdir();
            if (externalSDCardDirectery.exists()) { // ��������ļ��гɹ�����˵������SD�����ڣ�ֻ��û�л�����SD����
                isExternalCacheExist = true;
            } else { // ˵������SD��������
                isExternalCacheExist = false;
            }
        } else { // ����SD�����ڣ��Ҵ��ڻ����ļ���
            if (externalSDCardDirectery.list().length <= 2) {
                isExternalCacheExist = true;
            } else {
                isExternalCacheExist = false;
            }

        }
        isInternalCacheExist = (internalSDCardDirectery.exists() && internalSDCardDirectery
                .list().length > 0); // �ж�����SD���Ƿ���ã������Ƿ����
        if (isExternalCacheExist && isInternalCacheExist) { // �������SD����û�л��������ұ����洢���У��򿽱��������ݵ�����SD��
            return true;
        } else {
            return false;
        }

    }

    // ˢ����������
    private void RefreshWeather() {
        Intent intent = new Intent(MainActivity.this, WeatherRefreshService.class);
        intent.putExtra(WeatherRefreshService.BUNDLE_KEY_FROM, TAG);
        String cityNameCache = app.get_cityNameCache();
        if (cityNameCache == null) {
        } else {
            intent.putExtra(WeatherRefreshService.BUNDLE_KEY_CITYNAME, cityNameCache);
        }
        startService(intent);
    }
    //����
    public void restartApplication() {
        final Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * ����򿪵���Activity�رպ󴫵ݵ����� (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == 201) {
            boolean successed = data.getBooleanExtra("successed", false);
            if (!successed) {
            } else {
                try {
                    // ����Service��socket.io�ͻ��ˣ�
                    Intent service = new Intent(MainActivity.this,
                            SocketIoClient.class);
                    startService(service);
                    // ��ȡ�󶨵�service��SocketIoClient��
                    this.bindService(service, conn, Context.BIND_AUTO_CREATE);
                    ActivityManager.startPicPlayActivityAndClockHistory(this,true);
                    //TODO:�Ż���תģ��
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //TODO:�Ż����磬���鿴�����Ƿ��б�Ҫ
        /**
         * ��֪��ʲôԭ������������ʱ����������ӳٺܴ�Ļ������л������ʱ�������Σ�
         * ֱ���������ӳ�ʱ���ز����л���ȥ�����԰��첽��������ŵ������
         * �ڽ����л���ʱ��Ҫ�ѷ����������������ֹ���������Ų�����Ϊ�������������
         * ��֤������
         */
        Intent stopWeatherServiceIntent = new Intent(MainActivity.this, WeatherRefreshService.class);
        stopWeatherServiceIntent.putExtra(WeatherRefreshService.BUNDLE_KEY_ISSTOP, true);
        startService(stopWeatherServiceIntent);
    }

    @Override
    protected void onDestroy() {
        //mps=null;
        //�˳������ҳ��ע���Reciever
//		unregisterReceiver(mainReceiver);
        unregisterReceiver(mainReciever2);
        unregisterReceiver(bindReciever);

        _periodUpdataWeatherHandler.removeCallbacks(_periodUpdataWeatherRunable);
        //unbindService(conn);

        //�˳�ʱ��������������Service
        Intent service = new Intent(MainActivity.this,
                SocketIoClient.class);
        stopService(service);
        service = new Intent(MainActivity.this, WeatherRefreshService.class);
        stopService(service);

        super.onDestroy();
    }
    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    /*
     * ��ʾ����Դ��Ĺ��ͼƬ
     * */
    public void LoadDefaultPicture() {
        MpfPictureService mps = new MpfPictureService(MainActivity.this, DATA.DATAVERSION);

        String picName = "ad.jpg";
        File cache = new File(new File(GetExtSDCardPath.getSDCardPath()), DATA.sdCardPicFile);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        String filename = getMd5.Md5(picName, 32);
        File file = new File(cache, filename);

        List<MpfPicture> list = mps.getPicture("0");
        final int pictureCount = list.size();
        Log.w(TAG, "pictureCount:" + pictureCount);
        if (pictureCount == 1) {   //"0"��ʾ�Դ����ͼƬ  ���ֻ��һ�ţ����������˳�
            return;
        } else if (pictureCount > 1) {    //����1  ���ݿ����ж��Ź��ͼƬ������֮ǰ����ͼƬ�����BUG���ھɰ汾���������Ļ����ϻ�ͬʱ�����¾�����ͼƬ

            mps.delMpfPicture(0);     //"0"��ʾ�Դ����ͼƬ
            if (file.exists()) {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return;                                 //���ݿ���ʣһ��ͼƬ�ļ�¼������
        }
        try {
            if (!file.exists()) {
                //  return Uri.fromFile(file);//Uri.fromFile(path)��������ܵõ��ļ���URI
                InputStream is = getResources().openRawResource(R.drawable.ad);

                Log.d(TAG, "copyImage2Data----->InputStream open");

                FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());

                byte[] buffer = new byte[8192];
                System.out.println("3");
                int count = 0;

                // ��ʼ����LogoͼƬ�ļ�
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                    System.out.println("4");
                }
                fos.close();
                is.close();
            }
            file = null;
            MpfPicture temp = new MpfPicture();
            temp.setDbId(0);
            temp.setOpenId("0");
            temp.setPicUrl("ad.jpg");
            temp.setAddTime(new java.util.Date());
            mps.AddMpfPicture(temp);
            temp = null;
            //picList.add(temp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mps = null;
        }

    }
}
