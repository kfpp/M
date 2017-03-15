package com.qqonline.domain;

public class ImportRecord {

	public final static String TABLENAME="ImportRecord";
	public final static String PATH="Path";
	public final static String COUNT="Count";
	
	/**
	 * 导入的SD卡根目录下的文件夹路径
	 */
	private String path;
	/**
	 * 该文件夹下的图片数，主要用于判断是否有新的图片放入
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
