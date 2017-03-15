package com.qqonline.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.qqonline.Manager.db.WeatherService;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.SharedPreferenceUtil;
import com.qqonline.domain.Weather;
import com.qqonline.interfaces.IWeatherGetable;
import com.qqonline.interfaces.WeatherResultHandler;
import com.qqonline.modules.weathermodule.WeatherAPI;
import com.qqonline.mpf.R;

import java.util.Map;

/**
 * Intent�����б�<br/>
 * cityName:�����Ϊ�գ����ʾ���ó����������������£�����Ϊ����IP�Զ���λ����������<br/>
 * from�����Ǵ��ĸ�TAG�Ľ��洫�����ģ��ò�����Ҫ����Log������ʾ������Ϣ��
 * isStop����ʾ��ε������service��������ȡ��������ֹ֮ͣǰ�Ļ�ȡ������Ϊ(�ò���ֹͣʹ��)
 * Created by Administrator on 2015/7/24 0024.
 */
public class WeatherRefreshService extends Service {
    //��̬����
    public static final String BUNDLE_KEY_CITYNAME = "cityName";
    public static final String BUNDLE_KEY_ISSTOP = "isStop";
    public static final String BUNDLE_KEY_FROM = "from";
    private static final String TAG = "WeatherRefreshService";
    private String tag_from;
    //��̬��������

    //��ͨ�ڲ�����
    private IWeatherGetable weatherGetable;
    //��ͨ�ڲ���������

    //�Զ����ڲ���

    //�Զ����ڲ������
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

         Log.i(TAG, "onStartCommand");
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Bundle bundle = intent.getExtras();
        String cityName = null;
        weatherGetable = new WeatherAPI();
        if (bundle != null) {
            cityName = bundle.getString(BUNDLE_KEY_CITYNAME, null);
            tag_from = bundle.getString(BUNDLE_KEY_FROM, "");
        }

        try {
            Log.i(TAG, "Bundle start by" + tag_from);

            Log.i(TAG, "Bundle end,app start");
            if (MPFApp.getInstance().getActivityList().size() <= 0) {
                return super.onStartCommand(intent, flags, startId);
            }
            Log.i(TAG, "app end,activity start");
            Log.i(TAG, "activity end");
            if (cityName == null) {
                //����������ĳ�����Ϊ�գ�����ƫ�����ã��Ƿ��������õĳ�����
                Context context=MPFApp.getInstance().getActivityList().get(0);
                cityName= SharedPreferenceUtil.getPreference(SharedPreferenceUtil.
                        getDefaultSharedPreferences(context),
                        context.getResources().getStringArray(R.array.PreferenceArray)[0]);
                if (cityName == null) {
                    //���ƫ��������Ҳû�����ù����У���ô����IP��λ��ѯ
                    weatherGetable.initWeather(weatherHandler);
                } else {
                    //�������ƫ�������еĳ����������Դ˼���������Ϣ
                    weatherGetable.initWeather(cityName,weatherHandler);
                }
                Log.i(TAG, "The new weather is request from IP ");
            } else {
                //�����������Ϊ�գ��򰴸ó�����������������
                weatherGetable.initWeather(cityName, weatherHandler);
                Log.i(TAG, "The new weather is request from CityName: " + cityName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    private static WeatherResultHandler weatherHandler = new WeatherResultHandler() {
        private MPFApp app = MPFApp.getInstance();

        @Override
        public void onResult(String json) {

        }

        @Override
        public void onResult(Map<String, String> weather) {
            Weather weather1 = getEntity(weather);
            saveAndSendBroadcast(weather1);
            Toast.makeText(app.getActivityList().get(0), R.string.weather_updata,
                    Toast.LENGTH_SHORT).show();
        }

        /**
         * ��Map���͵���������ת����Weatherʵ������
         * @param weather map���͵���������
         * @return
         */
        private Weather getEntity(Map<String, String> weather) {
            Weather weather1 = new Weather(3);
            String[] temp = new String[3];
            String[] weath = new String[3];
            temp[0] = weather.get("temp0");
            temp[1] = weather.get("temp1");
            temp[2] = weather.get("temp2");
            weath[0] = weather.get("weather0");
            weath[1] = weather.get("weather1");
            weath[2] = weather.get("weather2");
            weather1.set_city(weather.get("city"));
            weather1.set_lunarCalendar(weather.get("lunar"));
            weather1.set_temp(temp);
            weather1.set_weather(weath);
            weather1.set_updateData(weather.get("data"));
            return weather1;
        }

        private void saveAndSendBroadcast(Weather weather) {
            WeatherService service = new WeatherService(app.getActivityList().get(0), DATA.DATAVERSION);
            service.setWeatherToDatebase(weather);
            app.set_weatherInfoCache(weather);
            Intent intent = new Intent();
            intent.setAction(DATA.BroadcastPicClockActionName);
            app.sendBroadcast(intent);
        }

    };
}
