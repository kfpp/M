package com.qqonline.conmon.async;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.GetUrlPicUri;
import com.qqonline.conmon.ImageCompress;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.ThumbnailUtil;
import com.qqonline.conmon.getMd5;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.domain.MpfPicture;
import com.qqonline.domain.MpfUser;
import com.qqonline.mpf.MainActivity;
import com.qqonline.mpf.PicPlayActivity;
import com.qqonline.mpf.R;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * �첽����ͼƬ������sd�����̳�AsyncTask
 * ��Ҫ����ͼƬ�������������ͼ����
 * @author zhihao.ye
 *
 */
public class AsyncImageTask extends AsyncTask<String, Integer, Bitmap> {
	private final static int VIDEOTYPE=0;
	private final static int IMAGETYPE=1;
	private File cache;
	/**
	 * ���ݿ��е�ͼƬURL��δ����
	 */
	private String pictureName;
	//Ҫ���ص�ͼƬ�����߿�
	private int width,height;
	private Bitmap videoBitmap;
	private OnFinishListener onFinishListener;
	public interface OnFinishListener
	{
		public void OnFinish(Bitmap bitmap);
	}
	public void setOnFinishListener(OnFinishListener listener)
	{
		this.onFinishListener=listener;
	}

	public AsyncImageTask(int width,int height) {
		cache = new File(new File(GetExtSDCardPath.getSDCardPath()),
				DATA.sdCardPicFile);
		if (!cache.exists()) {
			cache.mkdirs();
		}
		this.width = width;
		this.height = height;
	}
//	public AsyncImageTask(ImageView img, Activity pActivity, int width,int height) {
//		this.img = img;
//		this.pActivity = pActivity;
//		cache = new File(new File(GetExtSDCardPath.getSDCardPath()),
//				DATA.sdCardPicFile);
//		if (!cache.exists()) {
//			cache.mkdirs();
//		}
//		this.width=width;
//		this.height=height;
//	}
	@Override
	protected Bitmap doInBackground(String... params) {		
		int type=IMAGETYPE;
		String result =null;
		if (params[0] == null || params[0].equals("")) {
			return null;
		}
		if (params[0].startsWith("video:")) {
			videoBitmap = ThumbnailUtil.GetVideoThumbnailByName(params[0].replace("video:", ""), width,
					height, Images.Thumbnails.MINI_KIND);
			type = VIDEOTYPE;
		}
		else{
			if (params[0].startsWith("http") || params[0].contains("ad.jpg")) {
				pictureName=getMd5.Md5(params[0], 32);                             //���ͼƬ
			}
			else {		//��ͨͼƬ	
				int lastIndex=params[0].lastIndexOf("/");
				String path=params[0].substring(0, lastIndex);
				pictureName=params[0].substring(lastIndex+1, params[0].length());
				cache=new File(path);
				if (!cache.exists() || !cache.isDirectory()) {
					cache = new File(new File(GetExtSDCardPath.getSDCardPath()),DATA.sdCardPicFile);
				}
			}
			
			try {
				//
				boolean localFileExist=GetUrlPicUri.isLocalFileExist(pictureName, cache);
				if (!localFileExist) { //�����ļ������ڣ�����
					return null;
				}			
				result=GetUrlPicUri.getImageFile(pictureName, cache,false);					
				Log.i("fileasync",
						result + "," + String.valueOf(width) + ","
								+ String.valueOf(height));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		Bitmap bitmap=null;
		//ͼƬ����ѡ��
		Options opt=new Options();
		opt.inJustDecodeBounds=false;
		opt.inPreferredConfig= Bitmap.Config.RGB_565;
		opt.inSampleSize=ImageCompress.getScale(result, width, height);
		if(type==VIDEOTYPE)
		{
			if (videoBitmap != null) {
				bitmap=videoBitmap;  //������Ƶ������ʾ��Ƶ����ͼ
			}
		}
		else {
			bitmap = ImageCompress.Compress(result, width,
					height);			                    //����ͼƬ����ʾͼƬ����ͼ
		}
		if (bitmap == null) { // ͼƬ����ʱ��ImageCompress.Compress�����᷵��null
			bitmap = BitmapFactory.decodeResource(
					((ContextWrapper) MPFApp.getInstance()).getResources(), R.drawable.nopicture,opt); // ����Ĭ��ͼƬ
			
		}
		//���ͼƬС��ָ����С����Ŵ��Ա�֤����ͼ��Сһ�£����ֽ�������
		if (bitmap.getWidth() < width || bitmap.getHeight() < height) {
			//�Ŵ�
			int minSize=0;
			int targetSize=0;
			if (bitmap.getWidth() >= bitmap.getHeight()) {
				minSize=bitmap.getHeight();
				targetSize=height;
			}
			else {
				minSize=bitmap.getWidth();
				targetSize=width;
			}
			float scale=(float) targetSize / minSize;
			Matrix matrix=new Matrix();
			matrix.postScale(scale, scale);
			bitmap=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			
		}
		return bitmap;
	}
	protected void onPostExecute(Bitmap result) { 
		if (result != null) {
			Log.i("fileasync", "ͼƬ�ߣ�" + (result.getHeight()));
			Log.i("fileasync", "ͼƬ��" + (result.getWidth()));
		}	
		onFinishListener.OnFinish(result);
	}
}
