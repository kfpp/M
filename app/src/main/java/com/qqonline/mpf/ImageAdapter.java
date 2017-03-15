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
	 * 要显示的图片列表
	 */
	private List<MpfPicture> picList;
	private PicPlayActivity activity;
	public LayoutInflater mInflater;
	float start=0,end=0;
	private File cache;
	/**
	 * 用于读取屏幕参数：如分辨率等
	 */
	private Display display;
	private MPFApp mApp;
	//一定时间内双击;
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
		// 获取视频的缩略图
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
		.setTitle("删除图片")
		.setMessage("图片删除后不可恢复，您确定要删除吗？")
		.setIcon(android.R.drawable.ic_dialog_info)
		.setPositiveButton("删除", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deletePictureRecord(position);			
			}
		})
		.setNegativeButton("取消", null);
		if (isVideo) {
			builder.setNeutralButton("播放", new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.showVideo(picList.get(position).getPicUrl());
				}
			});
		}
		builder.show();
		
		
	}
	/**
	 * 删除图片记录，主要作用为：删除数据库中该图片的记录，并在当前图片列表中去掉该图片的记录
	 * @param position 图片在图片列表中的位置
	 */
	protected void deletePictureRecord(int position) {
		List<MpfPicture> tempList=activity.picList;
		MpfPicture deletePictureEntity=null;
		if (position <= (tempList.size()-1)) {
			
			deletePictureEntity=tempList.get(position);
			tempList.remove(position);	//清除图片列表中该图片的记录
			
			try {
				MpfPictureService service=new MpfPictureService(activity, DATA.DATAVERSION);
				service.delMpfPicture(deletePictureEntity.getDbId()); //清除数据库中该图片的记录
			} catch (Exception e) {
				e.printStackTrace();
			}						
			activity.imageAdapter.notifyDataSetChanged(); //通知Adapter更新图片列表这个数据源
		}
	}
	private final class AsyncShowImage extends AsyncTask<String, Integer, Bitmap>
	{
		private final static String TAG="AsyncShowImage";
		private ImageView view;
		private String pictureName;
		/**
		 * 保存加载图片时的nanoTime，用于计算加载图片代码所执行的时间
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
			 * 如果图片存在本地，则直接加载
			 */
			if (isImageExistLocation(pictureName)) {
				//获取压缩后的图片，如果不压缩，由于机器内存比较小，经常会出现OOM的情况
				bitmap=getCompressImageByName(pictureName,display);
				
				//如果此处bitmap为null,则说明该图片已损坏，可能的原因是：网络不稳定导致图片只下了一部分，需要从微信服务器重新下载,并删除坏图片
				if (bitmap == null) {
					redownloadPicture(view, reloadURL);
					deleteBrokenPicture(pictureName);
				}
				//能正常加载
				else {
					Log.i(TAG,"picture load");
					/**
					 * 如果图片本地存在,而且能正常加载的话，就屏蔽删除功能，正常的图片目前客户端不给删除，只能通过微信端删除
					 */
					//view.setOnTouchListener(null);
				}
			}
			/**
			 * 图片本地没有，则从网上下载
			 */
			else {
				redownloadPicture(view,reloadURL);
			}
			//本地没有图片或已损坏 ，上面的代码已重新下载图片中，但在下载的过程中该位置的图片为黑屏，所以在这里要放一张 图片下载中 的默认图片
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
				Log.i(TAG,String.valueOf(timeRun/(1000 * 1000))+"毫秒");
			}
		}
		
	}
	/**
	 * 判断图片是否存在
	 * @param url 图片名
	 * @return
	 */
	public boolean isImageExistLocation(String url) {  //本地图片的名字都是由图片的下载链接通过MD5加密后得到的
		String imageName=url;
		File image=new File(cache, imageName);
		if (image.exists()) {
			return true;
		}
		return false;
	}
	/**
	 * 删除本地图片，主要用于当加载到坏图片时删除已损坏的本地图片,并由redownloadPicture()方法重新下载图片
	 * @param url
	 */
	private void deleteBrokenPicture(String url) {
		File brokenPicture=new File(cache, getMd5.Md5(url, 32));
		if (brokenPicture.exists()) {
			brokenPicture.delete();
		}
		
	}
	/**
	 * 从网上重新下载图片
	 * @param string 要重新下载的图片地址
	 */
	private void redownloadPicture(final ImageView view, String url) {
		AsyncDownload download=new AsyncDownload(cache.getAbsolutePath(), getMd5.Md5(url, 32), new DoPostBack() {			
			@Override
			public void doPostBack(String result) {
				Log.i("redownloadPicture","redownloadPicture");
				if (result == null) {
					Bitmap bitmap = BitmapFactory.decodeResource(
							activity.getResources(), R.drawable.nopicture); // 加载默认图片
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
	 * 压缩图片并返回
	 * @param url 要压缩的图片
	 * @param display 屏幕参数
	 * @return 压缩后的图片
	 */
	private Bitmap getCompressImageByName(String url, Display display) {
	//	String name=getMd5.Md5(url, 32);
		String name=url;
		String path=new File(cache, name).getAbsolutePath();		
		return ImageCompress.Compress(path, display.getWidth(), display.getHeight());
	}
	/**
	 * 压缩图片并返回
	 * @param url 要压缩的图片
	 * @param display 屏幕参数
	 * @return 压缩后的图片
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
				//模拟双击事件
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
