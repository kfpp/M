package com.qqonline.interfaces;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

public interface IUrlPostable {

	public byte[] postUrlWithNoParams(String url);
	public byte[] postUrlWithParams(String url,List<NameValuePair> params);
	public byte[] postUrlWithJsonParams(String url,String jsonParam);
	/**
	 * �������ӳ�ʱ
	 * @param timeout ����
	 */
	public void setConnectTimeout(int timeout);
	/**
	 * ���ö�ȡ��ʱ
	 * @param timeout ����
	 */
	public void setReadTimeout(int timeout);
	public void release();
}
