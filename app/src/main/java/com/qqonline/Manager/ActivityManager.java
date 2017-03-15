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
 * ������ת������
 * Created by YE on 2015/10/10 0010.
 */
public class ActivityManager {

    /**
     * ��ת��������沢���ؼ�����
     *
     * @param context     context
     * @param requestCode ������
     */
    public static void startActiActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent();
        intent.setClassName(context, "com.qqonline.mpf.ActiActivity");
        context.startActivityForResult(intent, 200);
        showAnim(context);
    }

    public static void startPicPlayActivityAndClockHistory(Activity context, boolean isActi) {
        //TODO:Clock,Play������ת�����Ż���ɺ��޸��������ת����
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
        //���������ʱ����ֱ����������������һ��Intent,Ȼ��ǰ���Intent�ŵ���ʷ��¼�У��ṩ���˰�ťʹ��
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
     * ����ʱ�ӽ���
     * @param context context
     * @param isActi  Intent��Ĳ�������ʾ�Ƿ񼤻���ת��������ͨ��������ת
     */
    public static void startClockActivity(Activity context, boolean isActi) {
        Intent intent = getPicClockActivityIntent(context, isActi);
        context.startActivity(intent);
        showAnim(context);
    }

    /**
     * @param TAG �����־����Ҫ���ڵ�����Ϣ���
     * @param cityname Ҫ��ȡ�����ĳ�������ΪNULL�����IP�Զ�λ�ò���ȡ
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
     * ��ʾ�����л���Ч
     *
     * @param context Ҫ����Ч�Ľ���
     */
    private static void showAnim(Activity context) {
        context.overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);
    }

    /**
     * ��ȡ�л���ʱ�ӽ����Intent�����������������Ϊ����������Ҫ�õ����Intent
     * @param context context
     * @param isActi Intent��Ĳ�������ʾ�Ƿ񼤻���ת��������ͨ��������ת
     * @return intent
     */
    private static Intent getPicClockActivityIntent(Activity context, boolean isActi) {
        MPFApp app = MPFApp.getInstance();
        Intent intent = new Intent(context, PicClockActivity.class);
        Bundle bundle = new Bundle();
        //TODO:�ⲿ�ֿ����Ż��������Ƿ�׼���ÿ�����ת�������ֱ�Ӷ�ȡ�����������ﴫ
        if (app.get_weatherInfoCache() != null) {
            bundle.putBoolean("isWeatherReady", true);   //��ʾ��תʱ�����Ƿ��Ѿ�������
        } else {
            bundle.putBoolean("isWeatherReady", false);
        }
        //TODO:��������������������Ƿ����ù�
        bundle.putString("checkOpenId", "");  //��һ�û���ID �����Ϊ�ձ�ʾ��ʾȫ���û���ͼƬ
        //TODO:��������������������Ƿ����ù�
        bundle.putBoolean("acti", isActi);  //��ʾ�Ǽ���ɹ�����ת�����Ѽ���ֱ����ת
        intent.putExtras(bundle);
        return intent;
    }
}
