package com.qqonline.interfaces;

/**
 * 天气模块对外接口，只要实现了该接口，都可以用来更新天气。
 * Created by YE on 2015/10/12 0012.
 */
public interface IWeatherGetable {
    /**
     * 根据IP自动定位城市，并更新天气
     * @param handler 异步回调
     */
    void initWeather(WeatherResultHandler handler);

    /**
     *根据城市名获取天气数据
     * @param cityName 城市名
     * @param handler 异步回调
     */
    void initWeather(String cityName, WeatherResultHandler handler);
}
