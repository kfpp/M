package com.qqonline.interfaces;

public interface IUrlGetable {

	public byte[] getUrl(String url);
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
