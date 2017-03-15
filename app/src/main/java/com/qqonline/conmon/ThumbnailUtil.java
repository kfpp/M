package com.qqonline.conmon;

import java.io.File;

import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

public class ThumbnailUtil {

	public static Bitmap GetVideoThumbnailByName(String videoName, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		String path=GetExtSDCardPath.getSDCardPath()
				+ File.separator + DATA.SDCARDVIDEOPATH + File.separator
				+ videoName + DATA.VIDEOTYPE;
		// ��ȡ��Ƶ������ͼ
		bitmap = ThumbnailUtils.createVideoThumbnail(path, kind);
		if (bitmap == null) {
			return null;
		}
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
	public static Bitmap GetVideoThumbnailByPath(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// ��ȡ��Ƶ������ͼ
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		if (bitmap == null) {
			return null;
		}
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
}
