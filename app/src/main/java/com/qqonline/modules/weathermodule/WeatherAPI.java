package com.qqonline.modules.weathermodule;


import com.qqonline.interfaces.IWeatherGetable;
import com.qqonline.interfaces.WeatherResultHandler;

import java.util.Map;

/**
 * 天气模块的对外API
 * Created by YE on 2015/10/10 0010.
 */
public class WeatherAPI implements IWeatherGetable {


    @Override
    public void initWeather(WeatherResultHandler handler) {
        WeatherManager.location(new LocationHandler(handler));
    }

    @Override
    public void initWeather(String cityName, WeatherResultHandler handler) {
        WeatherManager.getWeather(getCityName(cityName), handler);
    }
    private String getCityName(String city) {
        String temp=city.substring(city.length()-1);
        if (temp.equals("省") || temp.equals("市") || temp.equals("镇") || temp.equals("区")) {
            return city.substring(0, city.length() - 1);
        } else {
            return city;
        }
    }
    private class LocationHandler implements WeatherResultHandler {
        private WeatherResultHandler handler;
        public LocationHandler(WeatherResultHandler handler) {
            this.handler=handler;
        }

        @Override
        public void onResult(String json) {
            String cityName=getCityName(json);
            if (cityName == null) {
                handler.onResult("error");
            }
            WeatherManager.getWeather(cityName, handler);
        }

        @Override
        public void onResult(Map<String, String> weather) {

        }
        private String getCityName(String json)
        {
            String[] items=json.split("	");
            if (items != null && items.length > 1) {
                return items[items.length - 1];
            }
            return null;
        }
    }

}
