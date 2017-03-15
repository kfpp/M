package com.qqonline.conmon.async.weather;

import org.json.JSONObject;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.SharedPreferenceUtil;
import com.qqonline.conmon.async.AsyncTaskPost;
import com.qqonline.conmon.net.PostByHttpClient;
import com.qqonline.interfaces.IUrlPostable;
import com.qqonline.mpf.CitySetting;
import com.qqonline.mpf.R;
import com.qqonline.Manager.db.WeatherService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
/**
 * 定位本IP所在的城市代码
 * @author YE
 *
 */
public class AsyncTaskAutoGetWeather extends AsyncTask<String, Integer, String> {

	private final static String TAG="AsyncTaskAutoGetWeather"; 
	private Activity activity;
	private IUrlPostable post;
	private boolean isFinished;
	private boolean isStoped;
	private AsyncTaskPost taskPost;
	public AsyncTaskAutoGetWeather(Activity activity) {
		this.activity=activity;
		this.isFinished=false;
		this.isStoped=false;
	}
	public void stop()
	{
		Log.i(TAG,"stop");
		if (post != null) {
			post.release();
		}
		if (taskPost != null && taskPost.getStatus() != AsyncTask.Status.FINISHED) {
			taskPost.stop();
		}
		isStoped=true;
	}
	public boolean isTaskFinished()
	{
		return isFinished;
	}
	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG,"doInBackground");
		post=new PostByHttpClient();
		String json=null;
		byte[] bytes=null;
		try {
			bytes=post.postUrlWithNoParams(params[0]);
			if (bytes != null) {
				json=ByteUtil.bytesToString(bytes, "uft-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return json;
	}
	@Override
	protected void onCancelled() {
		Log.i(TAG,"onCancelled");
		post.release();
		super.onCancelled();
	}
	@Override
	protected void onPostExecute(String result) {
		Log.i(TAG,"onPostExecute");
		if (result == null || isStoped) {
			return;
		}
		int indexBegin=result.indexOf("ip=")+4;
		int indexEnd=result.indexOf(";", indexBegin)-1;
		String IP=result.substring(indexBegin, indexEnd).replaceAll("_", "");
//		Long cityCode=Long.parseLong(codeString);
		taskPost=new AsyncTaskPost(new AsyncTaskPost.DoPostBack() {
			
			@Override
			public void PostBack(String result) {
				try {
					JSONObject obj=new JSONObject(result);
					if (obj.getInt("status") == 301) {
						if (activity !=null) {
							Toast.makeText(activity, "更新天气次数上限，每小时只能更新20次，请稍后重试", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(activity, "更新天气次数上限，每小时只能更新20次，请稍后重试", Toast.LENGTH_SHORT).show();
						}
						
					}
					else if(obj.getInt("status") == 200){												
						WeatherService ws=new WeatherService(activity,result, 3,DATA.DATAVERSION);	
						String city=activity.getResources().getStringArray(R.array.PreferenceArray)[0];
						SharedPreferences shared=activity.getSharedPreferences(activity.getResources().getStringArray(R.array.PreferenceFileNameArray)[0], 
								CitySetting.MODE_PRIVATE);
						SharedPreferenceUtil.setPreference(shared, city, ws.GetEntity().get_city());
						((MPFApp) activity.getApplication()).set_weatherInfoCache(ws.GetEntity());	
						((MPFApp) activity.getApplication()).set_cityNameCache(ws.GetEntity().get_city());
						Intent intent=new Intent();
						intent.setAction(DATA.BroadcastPicClockActionName);
						activity.sendBroadcast(intent);						
						Toast.makeText(activity, R.string.weather_updata, Toast.LENGTH_SHORT).show();
						
					}
				} catch (Exception e) {
					e.printStackTrace();
					//return null;
				}
				isFinished=true;
			}
		});
		taskPost.execute(DATA.AutoWeatherByIP.replace("%s1", IP));              
	}
	
}
