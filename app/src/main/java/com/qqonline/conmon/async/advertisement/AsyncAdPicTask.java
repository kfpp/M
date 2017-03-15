package com.qqonline.conmon.async.advertisement;

import java.io.File;
import java.lang.ref.WeakReference;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.GetUrlPicUri;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.mpf.PicClockActivity;
import com.qqonline.mpf.PicPlayActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * 异步下载广告图片
 * @author YE
 *
 */
public class AsyncAdPicTask extends AsyncTask<String, Integer, String> {

	private static final String TAG="AsyncAdPicTask";
	private File cache;
//	private PicClockActivity _active;
	private WeakReference<PicClockActivity> clockActivityWeakReference;
//	private PicPlayActivity _activityPlay;
	private WeakReference<PicPlayActivity> picPlayActivityWeakReference;
	public AsyncAdPicTask(PicClockActivity active) {
		clockActivityWeakReference=new WeakReference<PicClockActivity>(active);
//		cache = new File(Environment.getExternalStorageDirectory(),
//				DATA.sdCardAdPicFile);
		cache = new File(new File(GetExtSDCardPath.getSDCardPath()),
				DATA.sdCardAdPicFile);
		if (!cache.exists()) {
			cache.mkdirs();
		}
	}
	public AsyncAdPicTask(PicPlayActivity _activity) {
		// TODO Auto-generated constructor stub
		picPlayActivityWeakReference=new WeakReference<PicPlayActivity>(_activity);
//		cache = new File(Environment.getExternalStorageDirectory(),
//				DATA.sdCardAdPicFile);
		cache = new File(new File(GetExtSDCardPath.getSDCardPath()),
				DATA.sdCardAdPicFile);
		if (!cache.exists()) {
			cache.mkdirs();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG,"doInBackground");
		try {
			return GetUrlPicUri.getImageFile(params[0], cache);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		Log.i(TAG,"onPostExecute");
        if ( result != null) {
        	Uri uriFile=Uri.parse(result);
        	Intent intent=new Intent();
        	intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        	intent.setData(uriFile);      	        	
        	Bitmap bm=BitmapFactory.decodeFile(uriFile.getPath());
//       	_active._ivAdvertisement.setImageURI(uriFile);
        	if (clockActivityWeakReference.get() != null) {
				clockActivityWeakReference.get().sendBroadcast(intent);
				clockActivityWeakReference.get()._ivAdvertisement.setImageBitmap(bm);
			} else {
				picPlayActivityWeakReference.get().sendBroadcast(intent);
				picPlayActivityWeakReference.get()._ivAdvertisement.setImageBitmap(bm);
			}
       // 	bm.recycle();
        	bm=null;
        	intent=null;
        	uriFile=null;
        	release();
 //       	_active._ivAdvertisement.setImageDrawable(new BitmapDrawable(uriFile.getPath()));
        }
	}
	private void release()
	{
		  cache=null;
	}
}
