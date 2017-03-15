package com.qqonline.conmon.sdcardutils;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.qqonline.mpf.R;

public class SDCardFileUtil {

	private static final String TAG="SDCardFileUtil";
	private boolean isStoped=false;
	public void stop()
	{
		isStoped=true;
	}
	/**
	 * ��ȡָ��·���µ�ָ���ļ����͵��ܺͣ�����������Ŀ¼
	 * @param path ָ��·��
	 * @param fileType ָ���ļ�����
	 * @return �ܺ�
	 */
	public  int getDiretoryFileCounts(String path,String[] fileType)
	{
		StringBuilder sbType=new StringBuilder();
		for (String string : fileType) {
			sbType.append(string+" ");
		}
		int count=0;
		File file=new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return 0;
		}
		Log.i(TAG,"����ͳ��·����"+path+" �µ� "+sbType.toString()+" �ļ���Ŀ");
		for (File fileTemp : file.listFiles()) {
			if (isStoped) {
				return 0;
			}
			
			String name=fileTemp.getName();
			if (name.equalsIgnoreCase("mpfcache")) {
				continue;
			}
			if (fileTemp.isDirectory()) {
				count=count+ getDiretoryFileCounts(fileTemp.getAbsolutePath(), fileType);
			}
			else {
				String[] arrName = fileTemp.getName().split("\\.");  
				if (arrName.length <= 1) {
					continue;
				}
				for (int i = 0; i < fileType.length; i++) {
					if(fileType[i].equalsIgnoreCase(arrName[1]))
			        {  
			        	count++;
			        } 
				}
		         
			}
		}
		return count;
	}

	/**
	 * ��ȡָ��·���µ�ĳ�ļ����͵�����
	 * @param path ָ��·��
	 * @param fileType �ļ�����
	 * @return �ļ�����
	 * */
	public int getDiretoryFileCountsWithoutChild(String path, String[] fileType) {
		int count = 0;
		File file=new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return 0;
		}
		for (File fileTemp : file.listFiles()) {
			if (isStoped) {
				return count;
			}
			if (fileTemp.isDirectory()) {
				continue;
			}
			else {
				String[] arrName = fileTemp.getName().split("\\.");
				if (arrName.length <= 1) {
					continue;
				}
				for (int i = 0; i < fileType.length; i++) {
					if(fileType[i].equalsIgnoreCase(arrName[1]))
					{
						count++;
					}
				}

			}
		}
		return count;
	}

	public boolean isSDCardRoot(String path) {
		for (String temp : GetExtSDCardPath.pathName) {
			if (temp.equals(path) || ("/" + temp).equals(path)) {
				//����SD����Ŀ¼
				return true;
			}
		}
		return false;
	}
	public ArrayList<String> getPictureListFromDiretorys(String path,String[] fileType,PictureLoaded loaded)
	{
		
		ArrayList<String> list=new ArrayList<String>();
		StringBuilder sbType=new StringBuilder();
		for (String string : fileType) {
			sbType.append(string+" ");
		}
		File file=new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return null;
		}
		/**
		 * �����SD����Ŀ¼����ֻ��Ӹ�Ŀ¼�µ�ͼƬ������������Ŀ¼
		 */
		for (String rootPath : GetExtSDCardPath.pathName) {
			if (rootPath.equals(path) || ("/"+rootPath).equals(path)) {
				return getPictureListFromDiretoryWithoutChild(path,fileType,loaded);
			}
		}
		Log.i(TAG,"����ͳ��·����"+path+" �µ� "+sbType.toString()+" �ļ���Ŀ");
		for (File fileTemp : file.listFiles()) {
			if (isStoped) {
				return null;
			}
			
			String name=fileTemp.getName();
			if (name.equalsIgnoreCase("mpfcache")) {
				continue;
			}
			if (fileTemp.isDirectory()) {
				
				list.addAll(getPictureListFromDiretorys(fileTemp.getAbsolutePath(), fileType,loaded)) ;
			}
			else {
				String[] arrName = fileTemp.getName().split("\\.");  
				if (arrName.length <= 1) {
					continue;
				}
				for (int i = 0; i < fileType.length; i++) {
					if(fileType[i].equalsIgnoreCase(arrName[1]))
			        {  
			        	list.add(fileTemp.getAbsolutePath());
			        	loaded.onOnePictureLoaded(path);
			        } 
				}
		         
			}
		}
		return list;
	}
	public ArrayList<String> getPictureListFromDiretoryWithoutChild(String path,String[] fileType,PictureLoaded loaded)
	{

		ArrayList<String> list=new ArrayList<String>();
		StringBuilder sbType=new StringBuilder();
		for (String string : fileType) {
			sbType.append(string+" ");
		}
		File file=new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return null;
		}
		Log.i(TAG,"����ͳ��·����"+path+" �µ� "+sbType.toString()+" �ļ���Ŀ");
		for (File fileTemp : file.listFiles()) {
			if (isStoped) {
				return null;
			}
			if (fileTemp.isDirectory()) {
				continue;
			}
			else {
				String[] arrName = fileTemp.getName().split("\\.");
				if (arrName.length <= 1) {
					continue;
				}
				for (int i = 0; i < fileType.length; i++) {
					if(fileType[i].equalsIgnoreCase(arrName[1]))
					{
						list.add(fileTemp.getAbsolutePath());
						if (loaded != null) {
							loaded.onOnePictureLoaded(path);
						}
					}
				}

			}
		}
		return list;
	}
	public interface PictureLoaded
	{
		public void onOnePictureLoaded(String path);
	}
}
