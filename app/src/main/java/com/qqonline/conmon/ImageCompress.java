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
	 * ѹ��ͼƬ
	 * @param file  Ҫѹ����ͼƬͼ��
	 * @param maxWidth  �����������ߴ��ѹ��Ϊ��������ߴ��ͼƬ
	 * @param maxHeight	 �����������ߴ��ѹ��Ϊ��������ߴ��ͼƬ
	 * @return ѹ�����ͼƬ
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
	 * ��ȡѹ����
	 * @param file
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static int getScale( String file,int maxWidth,int maxHeight)
	{
		BitmapFactory.Options opts = new Options();
        // ����ȡ�������鵽�ڴ��У�����ȡͼƬ����Ϣ
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, opts);
        
        // ��Options�л�ȡͼƬ�ķֱ���
        int imageHeight = opts.outHeight;
        int imageWidth = opts.outWidth;
        // ���������
        int scaleX = imageWidth / maxWidth;
        int scaleY = imageHeight / maxHeight;
        int scale = 1;
        // �������������ķ���Ϊ׼
        // �˴��Ƚϱ�����ϵȺţ���Ȼ������ͼƬ�ͻ�����Ļ����һ��ʱ��
        //������Ļ����1000:1000��Ȼ��ͼƬ����Ϊ5000��5000
        //��ʱscaleX==scaleY�����û�еȿڣ���ֱ����������ĸ�ֵ
        //ֱ��scale=1,��ʾԭͼ��֮ǰûע�������������OOM;
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
        // false��ʾ��ȡͼƬ�������鵽�ڴ��У������趨�Ĳ�����
        opts.inJustDecodeBounds = false;
        // ������
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
//        if (bitmap == null) {  //����ͼƬ���𻵻򲻴���
//        	try {
//        		bitmap=BitmapFactory.decodeResource(activity.getResources(), R.drawable.nopicture);  //����Ĭ��ͼƬ
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//        	
//		}
      //�ж�ͼƬ�ò�����ת
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
	        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
	        int options = 100;
	        while ( baos.toByteArray().length / 1024> TARGETBITMAPKB) {    //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��        
	            baos.reset();//����baos�����baos
	            options -= 20;//ÿ�ζ�����20
	            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��

	        }
	        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
	        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
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
