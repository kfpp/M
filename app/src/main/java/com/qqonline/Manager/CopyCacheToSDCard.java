package com.qqonline.Manager;

import java.io.File;

import android.os.Environment;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.async.fileCopy.AsyncFileCopy;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.mpf.MainActivity;

/**
 * ͬ���������ݻ��浽�յ�SD����
 * 
 * @author YE
 *
 */
public class CopyCacheToSDCard {

	private String path;
	private String internalSDCardPath;
	private boolean isNeedCopy;
	private MainActivity activity;

	public CopyCacheToSDCard(MainActivity activity) {
		this.activity = activity;
		path = GetExtSDCardPath.getSDCardPath();
		internalSDCardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + DATA.sdCardPicFile;
		isNeedCopy = needCopy();
	}

	public void copy() {
		if (isNeedCopy) {
			copyFilesToExtSDCard();
		}
	}

	private void copyFilesToExtSDCard() {
		AsyncFileCopy copy = new AsyncFileCopy(activity);
		copy.execute(internalSDCardPath, path);
	}

	public boolean isNeedCopy() {
		return isNeedCopy;
	}

	private boolean needCopy() {
		if (path.equals(internalSDCardPath)) { // �����ȣ����ʾû��������SD��֮�֣��˳�
			return false;
		}
		if (existInteranlPicCache() && !existSDCardPicCache()) {
			return true;
		}
		return false;
	}

	/**
	 * �ж�SD���ϻ����Ƿ����
	 * 
	 * @return
	 */
	private boolean existSDCardPicCache() {
		String path = this.path + File.separator + DATA.sdCardPicFile;
		File file = new File(path);
		if (file.exists() && file.list().length > 1) {//1��Ĭ�ϻ���ڻ�������Դ����ͼƬ
			return true;
		}
		return false;
	}

	/**
	 * �жϻ����ڴ�����ݻ����Ƿ����
	 * 
	 * @return
	 */
	private boolean existInteranlPicCache() {
		String path = this.internalSDCardPath + File.separator
				+ DATA.sdCardPicFile;
		File file = new File(path);
		if (file.exists() && file.list().length > 1) {
			return true;
		}
		return false;
	}

}
