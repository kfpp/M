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
 * 入口类
 *
 * @author fengcheng.ye, YE
 */
@SuppressLint("ShowToast")
public class MainActivity extends BaseActivity {
    //静态变量
    /**
     * 延迟检测是否激活Handler，主要目的是显示启动界面
     */
    public static final int HANDLER_DELAY_ACTIVATION = 0;

    //静态变量结束

    //常量
    //常量结束

    //普通变量
    private MpfMachineService mms = null;
    private MpfUserService mus = null;

    //	private MainReceiver mainReceiver = null;// 广播接收者
    private MainReciever2 mainReciever2 = null;
    /**
     * 更新绑定码的接收者
     */
    private BindCodeUpdateReciever bindReciever = null;
    /**
     * 定期更新天气
     */
    private Handler _periodUpdataWeatherHandler;
    /**
     * 定时更新天气
     */
    private Runnable _periodUpdataWeatherRunable;

    /**
     * 后台服务线程的引用，未使用
     */
    private ServiceConnection conn;

    /**
     * 获取天气的异步类
     */
    private GetWeather get;
    /**
     * 数据加载线程回调Hander
     */
    public CallBackHandler delayHandler;
    //普通变量结束

    //自定义内部类

    /**
     * 自定义Handler类，
     * 主要作用：处理数据加载线程的回调
     */
    private static class CallBackHandler extends Handler {
        private WeakReference<MainActivity> activity;

        public CallBackHandler(MainActivity activity) {
            this.activity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            //TODO:完善Handler的事件响应
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
    //自定义内部类结束


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

        //检查更新
        checkUpdate(this);

        //初始化界面控件等变量
        initDate();

        //初始化未捕获异常Handler
        initUnCatchHandler();

        //同步SD卡的导入图片
        SyncSDCardCacheFile();

        //导入偏好设置
        LoadSetting setting = new LoadSetting(MainActivity.this);
        setting.load(); // 载入偏好设置

        //更新导入的图片文件夹
        UpdataImportPictureWhileStart update = new UpdataImportPictureWhileStart(MainActivity.this);
        update.execute("");   //更新导入的图片文件夹

        //载入默认的广告图片
        LoadDefaultPicture();

//		//检查是否需要从SD卡同步激活记录到本机储存数据库中
//		ActivationSyncManager activationSyncManager=new ActivationSyncManager(this);
//		activationSyncManager.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //延迟检测是否激活，主要目的是显示软件的启动界面
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                delayHandler.sendEmptyMessage(HANDLER_DELAY_ACTIVATION);
            }
        }).start();
//		checkActivated();


        //TODO:将在主线程加载数据的函数转到新开线程中去，以优化软件打开的流畅度
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
		* 在网络延迟的情况下，获取网络数据时，很可能在界面切换时，
		* 网络请求还未完成，这样就会卡住界面（黑屏），这里需要对网络请求进行释放
		*
		* */
        if (get != null) {
            get.release();
        }
        super.onStop();
    }

    /**
     * 初始化对未捕获错误的处理
     */
    private void initUnCatchHandler() {
        MPFUnCatchHandler handler = new MPFUnCatchHandler(app);
        Thread.setDefaultUncaughtExceptionHandler(handler);

    }

    /**
     * 如果需要，同步本地存储中的数据到SD卡
     */
    private void SyncSDCardCacheFile() {
        CopyCacheToSDCard copy = new CopyCacheToSDCard(MainActivity.this);
        boolean isCacheCopyNeeded = copy.isNeedCopy();
        if (isCacheCopyNeeded) {
            copy.copy();
        } /*else { // 如果不需要拷贝缓存数据，则直接判断激活状态，如果需要拷贝缓存数据到外置SD卡，则将在异步任务里拷贝完数据后再判断
			checkActivated();
		}*/
    }

    /**
     * 初始化相关数据
     */
    private void initDate() {
        app.setmAcyivity(MainActivity.this);
        app.setVideoIcon(getResources());

        delayHandler = new CallBackHandler(this);

//		RefreshWeather();
        // 定时器，定时更新天气数据
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
        // 动态注册广播
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
     * 判断机器是否已激活，末激活则跳转到激活页面，已激活则进入时钟界面
     */
    public void checkActivated() {
        if (mms == null) {
            mms = new MpfMachineService(MainActivity.this, DATA.DATAVERSION);
        }
        if (mus == null) {
            mus = new MpfUserService(MainActivity.this, DATA.DATAVERSION);
        }

        if (!mms.MpfMachineIsHave()) {
            ActivityManager.startActiActivityForResult(this, 200);// 机器没有激活先启用激活页面
        } else {

            // 启动Service（socket.io客户端）
            Intent service = new Intent(MainActivity.this, SocketIoClient.class);
            startService(service);
            // 获取绑定的service的SocketIoClient类
            //this.bindService(service, conn, Context.BIND_AUTO_CREATE);
            // 加载用户照片列表
            //loadUser();
//            ActivityManager.startClockActivity(this, true);
            ActivityManager.startPicPlayActivityAndClockHistory(this, false);
            //TODO:优化界面跳转模块
        }
    }

    /**
     * 检查数据库和图片文件 如果SD卡上没有这些缓存文件，而本机存储中有这些缓存文件，则将本机存储中的文件全部复制到SD卡上，
     * 因为软件会优先扫描SD卡，如果在SD卡上没找到这些文件，就认定为末激活，而忽略本机存储中的数据
     */
    private boolean checkCacheFile() {
        String path = GetExtSDCardPath.getSDCardPath();
        String internalSDCardPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + DATA.sdCardPicFile;
        if (path.equals(internalSDCardPath)) { // 如果相等，则表示没有内外置SD卡之分，退出
            return false;
        }
        File internalSDCardDirectery = new File(internalSDCardPath);
        File externalSDCardDirectery = new File(path);
        boolean isExternalCacheExist = false, isInternalCacheExist = false;
        // File test=null;
        if (!externalSDCardDirectery.exists()) {// 可能外置SD卡不存在，也可能只是没有这个缓存文件夹
            externalSDCardDirectery.mkdir();
            if (externalSDCardDirectery.exists()) { // 如果创建文件夹成功，则说明外置SD卡存在，只是没有缓存在SD卡上
                isExternalCacheExist = true;
            } else { // 说明外置SD卡不存在
                isExternalCacheExist = false;
            }
        } else { // 外置SD卡存在，且存在缓存文件夹
            if (externalSDCardDirectery.list().length <= 2) {
                isExternalCacheExist = true;
            } else {
                isExternalCacheExist = false;
            }

        }
        isInternalCacheExist = (internalSDCardDirectery.exists() && internalSDCardDirectery
                .list().length > 0); // 判断内置SD卡是否可用，缓存是否存在
        if (isExternalCacheExist && isInternalCacheExist) { // 如果外置SD卡上没有缓存数据且本机存储中有，则拷贝缓存数据到外置SD卡
            return true;
        } else {
            return false;
        }

    }

    // 刷新天气数据
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
    //重启
    public void restartApplication() {
        final Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 处理打开的子Activity关闭后传递的数据 (non-Javadoc)
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
                    // 启动Service（socket.io客户端）
                    Intent service = new Intent(MainActivity.this,
                            SocketIoClient.class);
                    startService(service);
                    // 获取绑定的service的SocketIoClient类
                    this.bindService(service, conn, Context.BIND_AUTO_CREATE);
                    ActivityManager.startPicPlayActivityAndClockHistory(this,true);
                    //TODO:优化跳转模块
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        //TODO:优化网络，并查看这里是否还有必要
        /**
         * 不知道什么原因，请求天气的时候如果网络延迟很大的话，在切换界面的时候会黑屏蔽，
         * 直到网络连接超时返回才能切换过去，所以把异步天气请求放到服务里，
         * 在界面切换的时候要把服务里的天气请求终止掉，这样才不会因为卡到界面而黑屏
         * 保证流畅性
         */
        Intent stopWeatherServiceIntent = new Intent(MainActivity.this, WeatherRefreshService.class);
        stopWeatherServiceIntent.putExtra(WeatherRefreshService.BUNDLE_KEY_ISSTOP, true);
        startService(stopWeatherServiceIntent);
    }

    @Override
    protected void onDestroy() {
        //mps=null;
        //退出清除本页面注册的Reciever
//		unregisterReceiver(mainReceiver);
        unregisterReceiver(mainReciever2);
        unregisterReceiver(bindReciever);

        _periodUpdataWeatherHandler.removeCallbacks(_periodUpdataWeatherRunable);
        //unbindService(conn);

        //退出时，清除整个程序的Service
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
     * 显示软件自带的广告图片
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
        if (pictureCount == 1) {   //"0"表示自带广告图片  如果只有一张，正常，并退出
            return;
        } else if (pictureCount > 1) {    //大于1  数据库里有多张广告图片，这是之前更换图片引起的BUG，在旧版本升级上来的机器上会同时存在新旧两张图片

            mps.delMpfPicture(0);     //"0"表示自带广告图片
            if (file.exists()) {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return;                                 //数据库里剩一张图片的记录，返回
        }
        try {
            if (!file.exists()) {
                //  return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
                InputStream is = getResources().openRawResource(R.drawable.ad);

                Log.d(TAG, "copyImage2Data----->InputStream open");

                FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());

                byte[] buffer = new byte[8192];
                System.out.println("3");
                int count = 0;

                // 开始复制Logo图片文件
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
