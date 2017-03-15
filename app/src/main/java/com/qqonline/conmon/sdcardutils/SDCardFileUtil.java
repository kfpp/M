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
	 * 获取指定路径下的指定文件类型的总和，包括它的子目录
	 * @param path 指定路径
	 * @param fileType 指定文件类型
	 * @return 总和
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
		Log.i(TAG,"正在统计路径："+path+" 下的 "+sbType.toString()+" 文件数目");
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
	 * 获取指定路径下的某文件类型的数量
	 * @param path 指定路径
	 * @param fileType 文件类型
	 * @return 文件数量
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
				//这是SD卡根目录
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
		 * 如果是SD卡根目录，则只添加根目录下的图片，不搜索其子目录
		 */
		for (String rootPath : GetExtSDCardPath.pathName) {
			if (rootPath.equals(path) || ("/"+rootPath).equals(path)) {
				return getPictureListFromDiretoryWithoutChild(path,fileType,loaded);
			}
		}
		Log.i(TAG,"正在统计路径："+path+" 下的 "+sbType.toString()+" 文件数目");
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
		Log.i(TAG,"正在统计路径："+path+" 下的 "+sbType.toString()+" 文件数目");
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
