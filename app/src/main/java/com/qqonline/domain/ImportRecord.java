package com.qqonline.domain;

public class ImportRecord {

	public final static String TABLENAME="ImportRecord";
	public final static String PATH="Path";
	public final static String COUNT="Count";
	
	/**
	 * �����SD����Ŀ¼�µ��ļ���·��
	 */
	private String path;
	/**
	 * ���ļ����µ�ͼƬ������Ҫ�����ж��Ƿ����µ�ͼƬ����
	 */
	private int count;
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
