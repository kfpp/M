package com.qqonline.interfaces;

/**
 * ����ģ�����ӿڣ�ֻҪʵ���˸ýӿڣ���������������������
 * Created by YE on 2015/10/12 0012.
 */
public interface IWeatherGetable {
    /**
     * ����IP�Զ���λ���У�����������
     * @param handler �첽�ص�
     */
    void initWeather(WeatherResultHandler handler);

    /**
     *���ݳ�������ȡ��������
     * @param cityName ������
     * @param handler �첽�ص�
     */
    void initWeather(String cityName, WeatherResultHandler handler);
}
