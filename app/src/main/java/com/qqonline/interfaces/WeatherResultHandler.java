package com.qqonline.interfaces;

import java.util.Map;

/**
 * ����ģ�����API����ȡ�õ��������ݺ�Ļص�
 * Created by YE on 2015/10/10 0010.
 */
public interface WeatherResultHandler {
    void onResult(String json);

    /**
     * ��ȡ���������ݵĻص�
     * @param weather ������<br/>
     *                temp0,temp1,temp2:�����ڵ��¶�<br/>
     *                weather0,weather1,weather2:�����ڵ�����<br/>
     *                city:������<br/>
     *                data:��������ʱ��<br/>
     *                lunar:ũ������
     *
     */
    void onResult(Map<String, String> weather);
}
