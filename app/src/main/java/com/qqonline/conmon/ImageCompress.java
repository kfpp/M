package com.qqonline.conmon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.qqonline.mpf.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;

public  class ImageCompress {

	private static final int TARGETBITMAPKB =800;
	/**
	 * 压缩图片
	 * @param file  要压缩的图片图径
	 * @param maxWidth  如果超过这个尺寸就压缩为接收这个尺寸的图片
	 * @param maxHeight	 如果超过这个尺寸就压缩为接收这个尺寸的图片
	 * @return 压缩后的图片
	 */
	public static Bitmap Compress( String file,int maxWidth,int maxHeight)
	{
		Bitmap bitmap=borderCompress( file, maxWidth, maxHeight);
		//bitmap=qualityCompress(bitmap);
		return bitmap;
	}
	public static Bitmap CompositePictures(Bitmap background,Bitmap foreGround)
	{
		if(background == null || foreGround == null) return null;
		Bitmap newBitmap=Bitmap.createBitmap(background.getWidth(), background.getHeight(), Config.ARGB_8888);
		Canvas cv=new Canvas(newBitmap);
		int iconX=(background.getWidth())/2 - foreGround.getWidth()/2;
		int iconY=background.getHeight()/2 - foreGround.getHeight()/2;
		cv.drawBitmap(background, 0, 0, null);
		cv.drawBitmap(foreGround, iconX, iconY, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		background.recycle();
		background=null;
		foreGround=null;
		return newBitmap;
	}
	/**
	 * 获取压缩率
	 * @param file
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static int getScale( String file,int maxWidth,int maxHeight)
	{
		BitmapFactory.Options opts = new Options();
        // 不读取像素数组到内存中，仅读取图片的信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, opts);
        
        // 从Options中获取图片的分辨率
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;
        // 计算采样率
        int scaleX = imageWidth / maxWidth;
        int scaleY = imageHeight / maxHeight;
        int scale = 1;
        // 采样率依照最大的方向为准
        // 此处比较必须加上等号，不然当发的图片和机器屏幕比例一致时，
        //比如屏幕比例1000:1000，然后图片比例为5000：5000
        //这时scaleX==scaleY，如果没有等口，则直接跳过这里的赋值
        //直接scale=1,显示原图，之前没注意这里，曾引发了OOM;
        if (scaleX >= scaleY && scaleX >= 1) {
            scale = scaleX;
        }
        if (scaleX <= scaleY && scaleY >= 1) {
            scale = scaleY;
        }   
        return scale;
	}
	private static Bitmap borderCompress(String file,int maxWidth,int maxHeight)
	{
		BitmapFactory.Options opts = new Options();
		//
        int scale=getScale(file, maxWidth, maxHeight);    
        // false表示读取图片像素数组到内存中，依照设定的采样率
        opts.inJustDecodeBounds = false;
        // 采样率
        opts.inSampleSize = scale;
        opts.inPreferredConfig= Bitmap.Config.RGB_565;
        Bitmap bitmap=null;
        try {
        	FileInputStream fis = new FileInputStream(file);
        	bitmap = BitmapFactory.decodeStream(fis, null, opts);
        	fis.close();
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
//        if (bitmap == null) {  //加载图片已损坏或不存在
//        	try {
//        		bitmap=BitmapFactory.decodeResource(activity.getResources(), R.drawable.nopicture);  //加载默认图片
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//        	
//		}
      //判断图片用不用旋转
        if (bitmap !=null) {   
        	int r=0;
        	try {
				ExifInterface exifInterface=new ExifInterface(file);
				r=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	if (r != ExifInterface.ORIENTATION_UNDEFINED) {
        		bitmap=rotate(bitmap, r);
			}
		}
        
        file=null;      
        return bitmap;
	}
	private static Bitmap qualityCompress(Bitmap image)
	{
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	        int options = 100;
	        while ( baos.toByteArray().length / 1024> TARGETBITMAPKB) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
	            baos.reset();//重置baos即清空baos
	            options -= 20;//每次都减少20
	            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

	        }
	        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
	        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
	        baos=null;
	        try {
				isBm.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        return bitmap;

		
	}
	private static Bitmap rotate(Bitmap file,int rotate)
	{
		Matrix m=new Matrix();
		int digree=0;
		switch (rotate) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			digree=90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			digree=180;
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			digree=270;
			break;
		default:
			break;
		}
		m.postRotate(digree);		
		return Bitmap.createBitmap(file, 0, 0, file.getWidth(), file.getHeight(), m, true);
	}
}
