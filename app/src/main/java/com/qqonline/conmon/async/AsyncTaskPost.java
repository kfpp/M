package com.qqonline.conmon.async;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.getUrlResponse;
import com.qqonline.conmon.net.PostByHttpClient;
import com.qqonline.interfaces.IUrlPostable;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskPost extends AsyncTask<String, Integer, String> {

	private final static String TAG="AsyncTaskPost";
	private DoPostBack _postback;
	private IUrlPostable post;
	private boolean isFinished;
	private boolean isStoped;
	private List<NameValuePair> postParams;
	private String jsonParams;
	public AsyncTaskPost(DoPostBack postback) {
		_postback=postback;
		post=new PostByHttpClient();
		isFinished=false;
		isStoped=false;
		postParams = null;
		postParams = null;
	}
	public boolean isTaskFinished()
	{
		return isFinished;
	}
	public void setJsonParams(String json)
	{
		this.jsonParams=json;
	}
	public void stop()
	{
		Log.i(TAG,"stop");
		post.release();
		isStoped=true;
	}
	public void setParams(List<NameValuePair> params)
	{
		this.postParams=params;
	}
	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG,"doInBackground");
		String jsonString=null;
		byte[] bytes=null;
		try {
			if (isStoped) {
				return null;
			}
			if (postParams == null && jsonParams == null) {
				bytes= post.postUrlWithNoParams(params[0]);
			}
			else if(postParams != null){
				bytes= post.postUrlWithParams(params[0],postParams);
			}
			else if(jsonParams != null)
			{
				bytes=post.postUrlWithJsonParams(params[0], jsonParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (bytes != null) {
			jsonString=ByteUtil.bytesToString(bytes, "utf-8");
		}
		return jsonString;
	}
	@Override
	protected void onPostExecute(String result) {
		Log.i(TAG,"onPostExecute");
		_postback.PostBack(result);
		isFinished=true;
	}
	public interface DoPostBack
	{
		public void PostBack(String result);
	}

}
