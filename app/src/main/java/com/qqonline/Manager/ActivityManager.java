package com.qqonline.Manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qqonline.conmon.MPFApp;
import com.qqonline.mpf.PicClockActivity;
import com.qqonline.mpf.PicPlayActivity2;
import com.qqonline.mpf.R;
import com.qqonline.service.WeatherRefreshService;

/**
 * 界面跳转管理类
 * Created by YE on 2015/10/10 0010.
 */
public class ActivityManager {

    /**
     * 跳转到激活界面并返回激活结果
     *
     * @param context     context
     * @param requestCode 请求码
     */
    public static void startActiActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent();
        intent.setClassName(context, "com.qqonline.mpf.ActiActivity");
        context.startActivityForResult(intent, 200);
        showAnim(context);
    }

    public static void startPicPlayActivityAndClockHistory(Activity context, boolean isActi) {
        //TODO:Clock,Play界面跳转传参优化完成后，修改这里的跳转传参
        Intent[] intents = new Intent[2];
        Intent intent = new Intent(context, PicPlayActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putString("OpenId", "");
        bundle.putString("type", "");
        bundle.putBoolean("acti", isActi);
        bundle.putBoolean("pingbao",false);
        intent.putExtras(bundle);
        intents[1] = intent;
        intents[0] = getPicClockActivityIntent(context,false);
//        intents[0] = new Intent(context,PicClockActivity.class);
        //启动多界面时，会直接启动数组里的最后一个Intent,然后将前面的Intent放到历史记录中，提供后退按钮使用
        context.startActivities(intents);
        showAnim(context);
    }

    public static void startPicPlayActivity(Activity activity,int dbId,String type,String openId) {
        Intent intent = new Intent(activity, PicPlayActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putString("OpenId", openId);
        bundle.putString("type", type);
        bundle.putLong("DbId", dbId);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
    /**
     * 启动时钟界面
     * @param context context
     * @param isActi  Intent里的参数，表示是否激活跳转，还是普通的启动跳转
     */
    public static void startClockActivity(Activity context, boolean isActi) {
        Intent intent = getPicClockActivityIntent(context, isActi);
        context.startActivity(intent);
        showAnim(context);
    }

    /**
     * @param TAG 界面标志，主要用于调试信息输出
     * @param cityname 要获取天气的城市名，为NULL则根据IP自动位置并获取
     */
    public static void startWeatherService(Context context,String TAG, String cityname) {
        Intent intent=new Intent(context, WeatherRefreshService.class);
        intent.putExtra(WeatherRefreshService.BUNDLE_KEY_FROM,TAG);
        if (cityname != null) {
            intent.putExtra(WeatherRefreshService.BUNDLE_KEY_CITYNAME,cityname);
        }
        context.startService(intent);
    }
    /**
     * 显示界面切换特效
     *
     * @param context 要做特效的界面
     */
    private static void showAnim(Activity context) {
        context.overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
    }

    /**
     * 获取切换到时钟界面的Intent，这里独立出来是因为有两个方法要用到这个Intent
     * @param context context
     * @param isActi Intent里的参数，表示是否激活跳转，还是普通的启动跳转
     * @return intent
     */
    private static Intent getPicClockActivityIntent(Activity context, boolean isActi) {
        MPFApp app = MPFApp.getInstance();
        Intent intent = new Intent(context, PicClockActivity.class);
        Bundle bundle = new Bundle();
        //TODO:这部分可以优化，天气是否准备好可以跳转到界面后直接读取，不用在这里传
        if (app.get_weatherInfoCache() != null) {
            bundle.putBoolean("isWeatherReady", true);   //表示跳转时天气是否已经加载完
        } else {
            bundle.putBoolean("isWeatherReady", false);
        }
        //TODO:检查这个参数在这个界面是否有用过
        bundle.putString("checkOpenId", "");  //其一用户的ID ，如果为空表示显示全部用户的图片
        //TODO:检查这个参数在这个界面是否有用过
        bundle.putBoolean("acti", isActi);  //表示是激活成功后跳转还是已激活直接跳转
        intent.putExtras(bundle);
        return intent;
    }
}
