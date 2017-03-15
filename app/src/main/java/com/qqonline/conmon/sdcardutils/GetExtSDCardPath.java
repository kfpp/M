package com.qqonline.conmon.sdcardutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.qqonline.conmon.DATA;

import android.os.Environment;

/**
 * �߰汾��ƽ��Androidϵͳ������SD��������SD��������
 * ������Ҫ���ܣ�
 * 		���Ȼ�ȡ�ж�SD��·���Ƿ�����
 * 		�ܣ��򷵻ظ�·��
 * 		���û������SD�����򷵻�����SD��·��
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
			File file=new File(sdCard, "test.txt");    //д��һ���ļ�����·���Ƿ�����
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
		//���û���ҵ�����SD��·ô
		//������SD�����ҵ����򷵻�����SD��
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+DATA.sdCardPicFile;
		}
		return null;
	}
}
