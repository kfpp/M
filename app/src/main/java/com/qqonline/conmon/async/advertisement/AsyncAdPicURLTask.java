package com.qqonline.conmon.async.advertisement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.getUrlResponse;
import com.qqonline.conmon.net.GetByHttpUrlConnection;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.interfaces.IUrlGetable;
import com.qqonline.mpf.PicClockActivity;
import com.qqonline.mpf.PicPlayActivity;
import com.qqonline.mpf.getIsPlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 异步获取广告信息
 * @author YE
 *
 */
public class AsyncAdPicURLTask extends AsyncTask<String, Integer, String> {
	private final static String TAG="AsyncAdPicURLTask";
	public enum ActivityType
	{
		PicActivity,ClockActivity
	}
	private WeakReference<PicClockActivity> activityWeak;
	private PicPlayActivity _activityPlay;
	private AsyncAdPicTask advertisementPicture;
	private MPFApp mApp;
	private ActivityType _type;
	private IUrlGetable get;
	public AsyncAdPicURLTask(PicClockActivity active,MPFApp app) {
		// TODO Auto-generated constructor stub
		activityWeak=new WeakReference(active);
		advertisementPicture=new AsyncAdPicTask(activityWeak.get());
		mApp=app;
		_type=ActivityType.ClockActivity;
	}
	public AsyncAdPicURLTask(PicPlayActivity activity,MPFApp app) {
		// TODO Auto-generated constructor stub
		_activityPlay=activity;
		advertisementPicture=new AsyncAdPicTask(_activityPlay);
		mApp=app;
		_type=ActivityType.PicActivity;
	}
	public void stop()
	{
		Log.i(TAG,"stop");
		get.release();
	}
	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG,"doInBackground");
		String result="";
		boolean isEnableAdvertisement=true;
		try {		
			switch (_type) {
			case PicActivity:
				if (mApp!= null && mApp.isAdsIsPlayPicActive()) {
					isEnableAdvertisement=true;
				}
				else {
					isEnableAdvertisement=false;
				}
				break;
			case ClockActivity:
				if (mApp!= null && mApp.isAdsIsPlay()) {
					isEnableAdvertisement=true;
				}
				else {
					isEnableAdvertisement=false;
				}
				break;
			default:break;
			}
			if (!isEnableAdvertisement) {
				return "";
			}
			/**
			 * 获取广告信息数据：
			 * {"success":"7","data":[{"ID":9,"Title":"大屏幕横向广告(勿删)","ImgUrl":"/FileUploading/635549513033311250.png",
			 * "TypeId":7,"Scope":"0","ScopeList":"","AdsContent":null,"StartTime":"\/Date(1413216000000)\/",
			 * "EndTime":"\/Date(1572537599000)\/","AddTime":"\/Date(1419325713207)\/"}]}
			 */
			if (isCancelled()) {
				return null;
			}
			get=new GetByHttpUrlConnection();
			result=ByteUtil.bytesToString(get.getUrl(DATA.HAdvertisementURL), "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	@Override
	protected void onCancelled() {
		Log.i(TAG,"onCancelled");
		if (get == null) {
			return ;
		}
		get.release();
		super.onCancelled();
	}

	@SuppressWarnings("unused")
	@Override
	protected void onPostExecute(String result) {
		Log.i(TAG,"onPostExecute");
		//请求不到数据，可能网络不通，显示本机广告图片
		if (result == null) {
			File cache = new File(new File(GetExtSDCardPath.getSDCardPath()),
					DATA.sdCardAdPicFile);
			if (cache.exists()) {
				//获取广告文件夹中的最新一张广告，并显示
				File[] files= cache.listFiles(new MyFilenameFilter());
				List<File> list=Arrays.asList(files);
				Collections.sort(list, new Comparator<File>() {
					@Override
					public int compare(File lhs, File rhs) {
						//lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
						return lhs.lastModified() > rhs.lastModified() ? -1 : (lhs.lastModified() == rhs.lastModified() ? 0 : 1);
					}
				});
				Bitmap bitmap=BitmapFactory.decodeFile(list.get(0).getAbsolutePath());
				setBitmap(bitmap);
			}
			return;
		}
		// 后台关闭广告显示
		if (result.equals("")) {
			return;
		}	
		
		//当请求到数据时，解析数据
		try {            //解析数据
			JSONObject jsonObj=new JSONObject(result);
			JSONObject temp=(JSONObject)jsonObj.getJSONArray("data").get(0);
			String picUrl=temp.getString("ImgUrl");           //获取数据中的广告图片地址;
//			AsyncAdPicTask advertisementPicture=new AsyncAdPicTask(activityWeak.get());
			advertisementPicture.execute("http://mpf.qq-online.net"+picUrl);   //根据图片地址下载广告图片
			picUrl=null;
			temp=null;
			jsonObj=null;
		} catch (JSONException e) {
			e.printStackTrace();
		}finally
		{
			release();
		}
		
		
	}
	public void release()
	{
		if(get != null)
		{
			get.release();
			get=null;
		}
		  _activityPlay=null;
		  mApp=null;
	}
	private void setBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		if (activityWeak.get() != null) {
			activityWeak.get()._ivAdvertisement.setImageBitmap(bitmap);
		} else if (_activityPlay != null){
			_activityPlay._ivAdvertisement.setImageBitmap(bitmap);
		}
	}
	private final class MyFilenameFilter implements FilenameFilter
	{

		@Override
		public boolean accept(File dir, String filename) {
			 //对获取的文件全名进行拆分  
	        String[] arrName = filename.split("\\.");  
	        if(arrName[1].equalsIgnoreCase("jpg"))   //如果是图片
	        {  
	            return true;  
	        }  
	        return false;			
		}
		
	}
	
}
