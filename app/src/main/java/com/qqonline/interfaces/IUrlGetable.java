package com.qqonline.interfaces;

public interface IUrlGetable {

	public byte[] getUrl(String url);
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
