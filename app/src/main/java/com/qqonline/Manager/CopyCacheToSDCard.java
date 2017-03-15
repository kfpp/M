package com.qqonline.Manager;

import java.io.File;

import android.os.Environment;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.async.fileCopy.AsyncFileCopy;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.mpf.MainActivity;

/**
 * 同步机身数据缓存到空的SD卡上
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
		if (path.equals(internalSDCardPath)) { // 如果相等，则表示没有内外置SD卡之分，退出
			return false;
		}
		if (existInteranlPicCache() && !existSDCardPicCache()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断SD卡上缓存是否存在
	 * 
	 * @return
	 */
	private boolean existSDCardPicCache() {
		String path = this.path + File.separator + DATA.sdCardPicFile;
		File file = new File(path);
		if (file.exists() && file.list().length > 1) {//1是默认会放在缓存里的自带广告图片
			return true;
		}
		return false;
	}

	/**
	 * 判断机身内存的数据缓存是否存在
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
