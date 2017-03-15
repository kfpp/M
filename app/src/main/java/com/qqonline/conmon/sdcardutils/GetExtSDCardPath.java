package com.qqonline.conmon.sdcardutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.qqonline.conmon.DATA;

import android.os.Environment;

/**
 * 高版本的平板Android系统有内置SD卡和外置SD卡的区别，
 * 本类主要功能：
 * 		首先获取判断SD卡路径是否能用
 * 		能，则返回该路径
 * 		如果没有外置SD卡，则返回内置SD卡路径
 * @author YE
 *
 */
public  class GetExtSDCardPath {

	public final static String[] pathName={"mnt/external_sd","mnt/extsd"};
	public static String getSDCardPath() 
	{
		for (int i = 0; i < pathName.length; i++) {
			File sdCard=new File(pathName[i]);
		//	boolean isfds=sdCard.exists();
			if (!sdCard.exists()) {
				continue;
			}
			File file=new File(sdCard, "test.txt");    //写入一个文件测试路径是否能用
			try {
				OutputStream out=new FileOutputStream(file);
				out.write("test".getBytes());
				out.close();
			} catch (Exception e) {
				continue;			}		
			if (file.exists()) {
				try {
					file.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return pathName[i]+File.separator+DATA.sdCardPicFile;
			}
		}
		//如果没有找到外置SD卡路么
		//但内置SD卡有找到，则返回内置SD卡
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+DATA.sdCardPicFile;
		}
		return null;
	}
}
