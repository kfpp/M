package com.qqonline.interfaces;

import java.util.Map;

/**
 * 天气模块对外API，获取得到天气数据后的回调
 * Created by YE on 2015/10/10 0010.
 */
public interface WeatherResultHandler {
    void onResult(String json);

    /**
     * 获取到天气数据的回调
     * @param weather 参数：<br/>
     *                temp0,temp1,temp2:三天内的温度<br/>
     *                weather0,weather1,weather2:三天内的天气<br/>
     *                city:城市名<br/>
     *                data:天气更新时间<br/>
     *                lunar:农历日期
     *
     */
    void onResult(Map<String, String> weather);
}
