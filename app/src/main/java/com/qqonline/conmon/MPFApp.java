package com.qqonline.conmon;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qqonline.broadcast.MainReceiver;
import com.qqonline.broadcast.PicReceiver;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.domain.Weather;
import com.qqonline.mpf.MainActivity;
import com.qqonline.mpf.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MPFApp extends Application {
    private static MPFApp app;
    /**
     * ?????????
     */
    private Bitmap videoIcon;

    public MediaPlayer getListenPlayer() {
        return listenPlayer;
    }

    public void setListenPlayer(MediaPlayer listenPlayer) {
        this.listenPlayer = listenPlayer;
    }

    public MainReceiver AppMainReceiver = null;
    public PicReceiver AppActiReceiver = null;
    public boolean ActiActivityIsLived = false;
    public MainActivity mAcyivity = null;
    private String[] Level1CityCache = null;
    private Map<String, String> Level1CityMapCache = null;
    /**
     * ???????
     */
    private List<String> shieldOpenIdList;

    public List<String> getShieldOpenIdList() {
        return shieldOpenIdList;
    }

    public void setShieldOpenIdList(List<String> shieldOpenIdList) {
        this.shieldOpenIdList = shieldOpenIdList;
    }

    /**
     * ?????????????
     */
    private String _cityNameCache = null;
    /**
     * ??????????????
     */
    private Weather _weatherInfoCache = null;
    /**
     * ????????
     */
    private MediaPlayer bkPlayer = null;
    /**
     * ?????????????
     */
    private MediaPlayer listenPlayer = null;
    /**
     * ??????????????????
     */
    private MediaPlayer picRecievePlayer = null;
    /**
     * ??????????????????????????????
     */
    private String BkgMusicName = null;
    /**
     * ????????????????
     */
    private boolean galleryPause = false;
    /**
     * ?????????
     */
    private DATA.VERSIONTYPE versionType;

    /**
     * ?????????????
     */
    private ArrayList<Activity> activityList = new ArrayList<Activity>();
    private DisplayImageOptions mImageOptions;
    private DisplayImageOptions mIconOptions;



    public MPFApp() {

        app = this;
        versionType = DATA.VERSIONTYPE.Topiserv;

        if (versionType == DATA.VERSIONTYPE.SmartHome) {
            DATA.updateUrl = "http://mpf.qq-online.net/ClientAPI/SoftUpdate/4";
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();

    }



    private void initImageLoader() {
        BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
        File cacheDir = new File(new File(GetExtSDCardPath.getSDCardPath()), DATA.sdCardPicFile);
        DiskCache diskCache;
        /*try {
            diskCache = new MyLruDiskCache(cacheDir,null,new MyFileNameGenerator(),0L,0);

        } catch (IOException e) {
            e.printStackTrace();
            diskCache = new UnlimitedDiskCache(cacheDir,null, new MyFileNameGenerator());
        }*/
        diskCache = new UnlimitedDiskCache(cacheDir,null, new MyFileNameGenerator());
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(6)
                .diskCache(diskCache)

                .diskCacheFileNameGenerator(new MyFileNameGenerator())
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSizePercentage(50)
                .denyCacheImageMultipleSizesInMemory()
                .imageDownloader(new MyImageDownloader(this))
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(configuration);
        mImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2 )
                .showImageOnLoading(R.drawable.jiazai)
                .showImageForEmptyUri(R.drawable.nopicture)
                .showImageOnFail(R.drawable.nopicture)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .decodingOptions(decodingOptions)
                .cacheInMemory(true)
                .considerExifParams(true)
                .displayer(new MyBitmapDisplay())
                .build();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        mIconOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .showImageOnLoading(R.drawable.ic_launcher)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .cacheInMemory(true)
                .decodingOptions(options)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }
    public DisplayImageOptions getIconOptions() {
        return mIconOptions;
    }

    public DisplayImageOptions getImageOptions() {
        return mImageOptions;
    }
    public DATA.VERSIONTYPE getVersionType() {
        return versionType;
    }

    public static MPFApp getInstance() {
        return app;
    }

    public ArrayList<Activity> getActivityList() {
        return activityList;
    }

    /**
     * ????????
     */
    public void returnHome()
    {
        Intent home = new Intent(Intent.ACTION_MAIN);

        home.addCategory(Intent.CATEGORY_HOME);

        getActivityList().get(0).startActivity(home);
    }
    /**
     * ?????/???????????????????
     *
     * @param context
     * @param isActi  ???????????????????????
     * @param openId  ????????????????????????
     */
    public void showNextActivity(Context context, boolean isActi, String openId) {
        Intent intent = getShowPicPlayActivityIntent(isActi, openId);
        context.startActivity(intent);
    }

    /**
     * ??????????
     *
     * @param
     * @param isActi
     */
    public Intent getShowPicPlayActivityIntent(boolean isActi, String openId) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("OpenId", openId);
        bundle.putString("type", "");
        intent.putExtras(bundle);
        intent.setClassName(this, "com.qqonline.mpf.PicPlayActivity");
        return intent;
    }

    /**
     * ?????Activity???????
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        removeActivity(activity);
        activityList.add(0,activity);
    }

    /**
     * ?????Activity???????
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        int index = -1;
        while ((index = activityList.indexOf(activity)) != -1) {
            activityList.remove(index);
        }
    }

    /**
     * ???????Activity????????
     */
    public void finishAllActivity() {

        try {
            for (Activity activity : activityList) {
                if (null != activity) {
                    activity.finish();
                }
            }
            //???????????
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????
     *
     * @return
     */
    public boolean isGalleryPause() {
        return galleryPause;
    }

    /**
     * ?????????????????
     *
     * @param galleryPause
     */
    public void setGalleryPause(boolean galleryPause) {
        this.galleryPause = galleryPause;
    }

    /**
     * ???????????????
     *
     * @return
     */
    public String getBkgMusicName() {
        return BkgMusicName;
    }

    /**
     * ????????????????
     *
     * @param bkgMusicName
     */
    public void setBkgMusicName(String bkgMusicName) {
        BkgMusicName = bkgMusicName;
    }

    /**
     * ??????????????????????
     *
     * @return
     */
    public MediaPlayer getPicRecievePlayer() {
        return picRecievePlayer;
    }

    /**
     * ???????????????????????
     *
     * @return
     */
    public void setPicRecievePlayer(MediaPlayer picRecievePlayer) {
        this.picRecievePlayer = picRecievePlayer;
    }

    private boolean isBkgMusic = true;

    /**
     * ????????????????
     *
     * @return
     */
    public boolean isBkgMusicEnabled() {
        return isBkgMusic;
    }

    /**
     * ?????????????????
     *
     * @param isBkgMusic
     */
    public void setBkgMusicEnabled(boolean isBkgMusic) {
        this.isBkgMusic = isBkgMusic;
    }

    /**
     * ????????????????
     *
     * @return
     */
    public MediaPlayer getBkPlayer() {
        return bkPlayer;
    }

    /**
     * ?????????????????
     *
     * @return
     */
    public void setBkPlayer(MediaPlayer bkPlayer) {
        this.bkPlayer = bkPlayer;
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public String get_cityNameCache() {
        return _cityNameCache;
    }

    /**
     * ?????????????????????????????
     *
     * @return
     */
    public void set_cityNameCache(String _cityNameCache) {
        this._cityNameCache = _cityNameCache;
    }

    /**
     * ???????????????????????????????
     *
     * @return
     */
    public Weather get_weatherInfoCache() {
        return _weatherInfoCache;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public void set_weatherInfoCache(Weather _weatherInfoCache) {
        this._weatherInfoCache = _weatherInfoCache;
    }

    /**
     * ???????????????????
     *
     * @return
     */
    public String[] getLevel1CityCache() {
        return Level1CityCache;
    }

    /**
     * ???????????????????
     */
    public void setLevel1CityCache(String[] level1CityCache) {
        Level1CityCache = level1CityCache;
    }

    /**
     * ?????????????????
     *
     * @return
     */
    public Map<String, String> getLevel1CityMapCache() {
        return Level1CityMapCache;
    }

    /**
     * ?????????????????
     */
    public void setLevel1CityMapCache(Map<String, String> level1CityMapCache) {
        Level1CityMapCache = level1CityMapCache;
    }

    /**
     * ???????????????
     */
    public boolean isHengping = true;
    /**
     * ????????????????
     */
    public boolean AdsIsPlay = true;
    /**
     * ????????????
     */
    public boolean AdsIsPlayPicActive = false;
    /**
     * ????????????OpenId????????????
     */
    public String OpenId = "";

    public MainReceiver getAppMainReceiver() {
        return AppMainReceiver;
    }

    public void setAppMainReceiver(MainReceiver appMainReceiver) {
        AppMainReceiver = appMainReceiver;
    }

    public PicReceiver getAppActiReceiver() {
        return AppActiReceiver;
    }

    public void setAppActiReceiver(PicReceiver appActiReceiver) {
        AppActiReceiver = appActiReceiver;
        ActiActivityIsLived = true;
    }

    public String getOpenId() {
        return OpenId;
    }

    public void setOpenId(String openId) {
        OpenId = openId;
    }

    public MainActivity getmAcyivity() {
        return mAcyivity;
    }

    public void setmAcyivity(MainActivity mAcyivity) {
        this.mAcyivity = mAcyivity;
    }

    /**
     * @return ???????????????
     */
    public boolean isAdsIsPlay() {
        return AdsIsPlay;
    }

    public void setAdsIsPlay(boolean adsIsPlay) {
        AdsIsPlay = adsIsPlay;
    }

    /**
     * @return ????????????
     */
    public boolean isAdsIsPlayPicActive() {
        return AdsIsPlayPicActive;
    }

    public void setAdsIsPlayPicActive(boolean adsIsPlay) {
        AdsIsPlayPicActive = adsIsPlay;
    }

    public boolean isHengping() {
        return isHengping;
    }

    public void setHengping(boolean isHengping) {
        this.isHengping = isHengping;
    }

    public Bitmap getVideoIcon() {
        return videoIcon;
    }

    public void setVideoIcon(Resources resource) {
        videoIcon = BitmapFactory.decodeResource(resource, R.drawable.video_icon);
    }

}
