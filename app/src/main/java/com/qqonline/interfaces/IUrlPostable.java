package com.qqonline.interfaces;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

public interface IUrlPostable {

	public byte[] postUrlWithNoParams(String url);
	public byte[] postUrlWithParams(String url,List<NameValuePair> params);
	public byte[] postUrlWithJsonParams(String url,String jsonParam);
	/**
	 * 设置连接超时
	 * @param timeout 毫秒
	 */
	public void setConnectTimeout(int timeout);
	/**
	 * 设置读取超时
	 * @param timeout 毫秒
	 */
	public void setReadTimeout(int timeout);
	public void release();
}
