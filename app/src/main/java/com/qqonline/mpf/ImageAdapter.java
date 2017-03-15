package com.qqonline.mpf;

import java.io.File;
import java.util.List;

import com.qqonline.conmon.async.AsyncDownload;
import com.qqonline.conmon.async.AsyncDownload.DoPostBack;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.ImageCompress;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.getMd5;
import com.qqonline.domain.MpfPicture;
import com.qqonline.Manager.db.MpfPictureService;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {
	/**
	 * Ҫ��ʾ��ͼƬ�б�
	 */
	private List<MpfPicture> picList;
	private PicPlayActivity activity;
	public LayoutInflater mInflater;
	float start=0,end=0;
	private File cache;
	/**
	 * ���ڶ�ȡ��Ļ��������ֱ��ʵ�
	 */
	private Display display;
	private MPFApp mApp;
	//һ��ʱ����˫��;
	private int keyDownCount=0;
	public ImageAdapter(List<MpfPicture> picList,PicPlayActivity pactivity,Display display) {
		// TODO Auto-generated constructor stub
		this.picList = picList;
		this.display=display;
		this.activity = pactivity;
		this.mInflater  = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mApp=(MPFApp) activity.getApplication();
		cache = new File(new File(GetExtSDCardPath.getSDCardPath()),DATA.sdCardPicFile);
//		cache = new File(new File("/storage/extsd/MPFCache"),DATA.sdCardPicFile);
		if (!cache.exists()) {
			cache.mkdir();
		}
	}
	@Override
	public int getCount() {
		return picList.size();
	}
	@Override
	public Object getItem(int position) {
		return picList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int index=position;
        if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.playphoneitem, null);
        	ImageView img= (ImageView) convertView.findViewById(R.id.playImage);
        	
        	convertView.setTag(img);
        }  
        String phoneImage = picList.get(position).getPicUrl();
        ImageView img= (ImageView) convertView.getTag();        
		img.setScaleType(ScaleType.FIT_CENTER);//centerInside
		boolean isVideo;
		if (phoneImage.startsWith("video:")) {
			isVideo=true;
		}
		else {
			isVideo=false;
		}		
		img.setOnTouchListener(new ImageTouchListener(index,isVideo));
		if (isVideo) {
			String path=GetExtSDCardPath.getSDCardPath()+File.separator+DATA.SDCARDVIDEOPATH;
			String name=path+File.separator+phoneImage.replace("video:", "")+DATA.VIDEOTYPE;
			Bitmap bitmap=getVideoThumbnail(name, 800, 600, Images.Thumbnails.MINI_KIND);
			if (bitmap == null) {
				img.setImageResource(R.drawable.ship);
				return convertView;
			}
			Bitmap videoIcon=mApp.getVideoIcon();
			Bitmap newBitmap=ImageCompress.CompositePictures(bitmap, videoIcon);
			img.setImageBitmap(newBitmap);
			
		}
		else {
			AsyncShowImage show=new AsyncShowImage(img);
	        show.execute(phoneImage);
		}        
		return convertView;
	}

	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
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
	private void showDeleteDialog(final int position, final boolean isVideo) {
		
		AlertDialog.Builder builder=new AlertDialog.Builder(activity)
		.setTitle("ɾ��ͼƬ")
		.setMessage("ͼƬɾ���󲻿ɻָ�����ȷ��Ҫɾ����")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("ɾ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deletePictureRecord(position);			
			}
		})
		.setNegativeButton("ȡ��", null);
		if (isVideo) {
			builder.setNeutralButton("����", new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.showVideo(picList.get(position).getPicUrl());
				}
			});
		}
		builder.show();
		
		
	}
	/**
	 * ɾ��ͼƬ��¼����Ҫ����Ϊ��ɾ�����ݿ��и�ͼƬ�ļ�¼�����ڵ�ǰͼƬ�б���ȥ����ͼƬ�ļ�¼
	 * @param position ͼƬ��ͼƬ�б��е�λ��
	 */
	protected void deletePictureRecord(int position) {
		List<MpfPicture> tempList=activity.picList;
		MpfPicture deletePictureEntity=null;
		if (position <= (tempList.size()-1)) {
			
			deletePictureEntity=tempList.get(position);
			tempList.remove(position);	//���ͼƬ�б��и�ͼƬ�ļ�¼
			
			try {
				MpfPictureService service=new MpfPictureService(activity, DATA.DATAVERSION);
				service.delMpfPicture(deletePictureEntity.getDbId()); //������ݿ��и�ͼƬ�ļ�¼
			} catch (Exception e) {
				e.printStackTrace();
			}						
			activity.imageAdapter.notifyDataSetChanged(); //֪ͨAdapter����ͼƬ�б��������Դ
		}
	}
	private final class AsyncShowImage extends AsyncTask<String, Integer, Bitmap>
	{
		private final static String TAG="AsyncShowImage";
		private ImageView view;
		private String pictureName;
		/**
		 * �������ͼƬʱ��nanoTime�����ڼ������ͼƬ������ִ�е�ʱ��
		 */
		private long timeStart;
		public AsyncShowImage(ImageView view) {
			this.view=view;
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			timeStart=System.nanoTime();
			Bitmap bitmap=null;
			String reloadURL=params[0];
			Log.i(TAG,reloadURL);
			if (params[0].startsWith("http") || params[0].contains("ad.jpg")) {
				pictureName=getMd5.Md5(params[0], 32);
			}
			else {				
				int lastIndex=params[0].lastIndexOf("/");
				String path=null;
				if (lastIndex != -1) {
					path=params[0].substring(0, lastIndex);
					pictureName=params[0].substring(lastIndex+1, params[0].length());
				}
				else {
					path=params[0];
					pictureName=params[0];
				}
				cache=new File(path);
				if (!cache.exists() || !cache.isDirectory()) {
					cache = new File(new File(GetExtSDCardPath.getSDCardPath()),DATA.sdCardPicFile);
//					cache = new File(new File("/storage/extsd/MPFCache"),DATA.sdCardPicFile);
				}
			}
			/**
			 * ���ͼƬ���ڱ��أ���ֱ�Ӽ���
			 */
			if (isImageExistLocation(pictureName)) {
				//��ȡѹ�����ͼƬ�������ѹ�������ڻ����ڴ�Ƚ�С�����������OOM�����
				bitmap=getCompressImageByName(pictureName,display);
				
				//����˴�bitmapΪnull,��˵����ͼƬ���𻵣����ܵ�ԭ���ǣ����粻�ȶ�����ͼƬֻ����һ���֣���Ҫ��΢�ŷ�������������,��ɾ����ͼƬ
				if (bitmap == null) {
					redownloadPicture(view, reloadURL);
					deleteBrokenPicture(pictureName);
				}
				//����������
				else {
					Log.i(TAG,"picture load");
					/**
					 * ���ͼƬ���ش���,�������������صĻ���������ɾ�����ܣ�������ͼƬĿǰ�ͻ��˲���ɾ����ֻ��ͨ��΢�Ŷ�ɾ��
					 */
					//view.setOnTouchListener(null);
				}
			}
			/**
			 * ͼƬ����û�У������������
			 */
			else {
				redownloadPicture(view,reloadURL);
			}
			//����û��ͼƬ������ ������Ĵ�������������ͼƬ�У��������صĹ����и�λ�õ�ͼƬΪ����������������Ҫ��һ�� ͼƬ������ ��Ĭ��ͼƬ
			if (bitmap == null) {				
				bitmap=BitmapFactory.decodeResource(activity.getResources(), R.drawable.download);								
			}
			return bitmap;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				view.setImageBitmap(result);
				long timeRun=System.nanoTime()-timeStart;
				Log.i(TAG,String.valueOf(timeRun/(1000 * 1000))+"����");
			}
		}
		
	}
	/**
	 * �ж�ͼƬ�Ƿ����
	 * @param url ͼƬ��
	 * @return
	 */
	public boolean isImageExistLocation(String url) {  //����ͼƬ�����ֶ�����ͼƬ����������ͨ��MD5���ܺ�õ���
		String imageName=url;
		File image=new File(cache, imageName);
		if (image.exists()) {
			return true;
		}
		return false;
	}
	/**
	 * ɾ������ͼƬ����Ҫ���ڵ����ص���ͼƬʱɾ�����𻵵ı���ͼƬ,����redownloadPicture()������������ͼƬ
	 * @param url
	 */
	private void deleteBrokenPicture(String url) {
		File brokenPicture=new File(cache, getMd5.Md5(url, 32));
		if (brokenPicture.exists()) {
			brokenPicture.delete();
		}
		
	}
	/**
	 * ��������������ͼƬ
	 * @param string Ҫ�������ص�ͼƬ��ַ
	 */
	private void redownloadPicture(final ImageView view, String url) {
		AsyncDownload download=new AsyncDownload(cache.getAbsolutePath(), getMd5.Md5(url, 32), new DoPostBack() {			
			@Override
			public void doPostBack(String result) {
				Log.i("redownloadPicture","redownloadPicture");
				if (result == null) {
					Bitmap bitmap = BitmapFactory.decodeResource(
							activity.getResources(), R.drawable.nopicture); // ����Ĭ��ͼƬ
					view.setImageBitmap(bitmap);
					Log.i("redownloadPicture","noPicture");
				}	
				else {
					Bitmap bitmap=getCompressImageByPath(result, display);
					view.setImageBitmap(bitmap);
					Log.i("redownloadPicture","downloadComplete");
				}
			}
		});
		download.execute(url);
	}
	/**
	 * ѹ��ͼƬ������
	 * @param url Ҫѹ����ͼƬ
	 * @param display ��Ļ����
	 * @return ѹ�����ͼƬ
	 */
	private Bitmap getCompressImageByName(String url, Display display) {
	//	String name=getMd5.Md5(url, 32);
		String name=url;
		String path=new File(cache, name).getAbsolutePath();		
		return ImageCompress.Compress(path, display.getWidth(), display.getHeight());
	}
	/**
	 * ѹ��ͼƬ������
	 * @param url Ҫѹ����ͼƬ
	 * @param display ��Ļ����
	 * @return ѹ�����ͼƬ
	 */
	private Bitmap getCompressImageByPath(String path, Display display) {
		Bitmap bitmap=ImageCompress.Compress(path, display.getWidth(), display.getHeight());
		return bitmap;
	}
	private final class ImageTouchListener implements View.OnTouchListener{

		private int index;
		private boolean isVideo;
		public ImageTouchListener(int index,boolean isVideo) {
			this.index=index;
			this.isVideo=isVideo;
		}
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (MotionEvent.ACTION_DOWN ==event.getAction()) {
				//showDeleteDialog(index);	
				//ģ��˫���¼�
				keyDownCount++;
				if (keyDownCount == 2) {
					showDeleteDialog(index,isVideo);							
				}
				new Thread(){
					public void run() {
						try {
							Thread.sleep(350);
						} catch (Exception e) {
							e.printStackTrace();
						}
						keyDownCount =0;
					};
				}.start();
			}									
			return false;
		}
		
	}

}
