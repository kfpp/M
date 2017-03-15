package com.qqonline.modules.weathermodule;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qqonline.interfaces.WeatherResultHandler;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by YE on 2015/10/12 0012.
 */
public class WeatherManager {
    /**
     * 定位城市
     *
     * @return 城市名
     */
    public static String location(final WeatherResultHandler handler) {
        HttpUtil.get(Global.URLIPLocation, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String text=null;
                try {
                    text=new String(bytes,"GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (text != null) {
                    handler.onResult(text);
                } else {
                    handler.onResult("error");
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
        return null;
    }

    public static String getWeather(String cityname, final WeatherResultHandler handler) {
        String url = Global.URLWeather + URLEncoder.encode(cityname);
        HttpUtil.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String xml=null;
                try {
                    xml=new String(bytes,"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                InputStream is = new ByteArrayInputStream(xml.getBytes());
                Map<String, String> map = null;
                try {
                    map = PullWeatherParse.parse(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (map != null) {
                    handler.onResult(map);
                } else {
                    handler.onResult("error");
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                handler.onResult("error");
            }
        });
        return null;
    }
}
