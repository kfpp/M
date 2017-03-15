package com.qqonline.conmon.async;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.net.GetByHttpUrlConnection;
import com.qqonline.interfaces.IUrlGetable;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

public class AsyncGet extends AsyncTask<String, Integer, String> {
	private final static String TAG="AsyncGet";
	private MyDoPostBack _doPostBack;
	private Dialog _loadingDialog;
//	private Activity _context;
	private WeakReference<Activity> activityWeakReference;
	private boolean isShowPreDialog;
	private IUrlGetable get;
	private boolean isStop;
	public AsyncGet(Activity context,MyDoPostBack doPostBack) {
		this(context, doPostBack, true);
	}
	public AsyncGet(Activity context,MyDoPostBack doPostBack,boolean showPreDialog) {

		activityWeakReference=new WeakReference<Activity>(context);
		_doPostBack=doPostBack;
		isShowPreDialog=showPreDialog;
		get=new GetByHttpUrlConnection();
		isStop=false;
	}
	@Override
	protected void onPreExecute() {
		if (isShowPreDialog) {
			_loadingDialog=new AlertDialog.Builder(activityWeakReference.get()).setTitle("加载").setMessage("正在加载中，请稍候。。。").create();
			_loadingDialog.show();
		}
		
	}
	@Override
	protected String doInBackground(String... params) {
		String url=params[0];
		String result=null;
		byte[] bytes=null;
		if (isStop) {
			return null;
		}
		try {
			bytes=get.getUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bytes != null) {
			result=ByteUtil.bytesToString(bytes, "utf-8");
		}
		return result;
	}
	@Override
	protected void onPostExecute(String result) {
		if (isShowPreDialog) {
			_loadingDialog.dismiss();
		}		
		if (_doPostBack != null && result != null && !isStop) {
			_doPostBack.DoPostBack(result);
		}
		
		super.onPostExecute(result);
	}
	public void release()
	{
		isStop=true;
		get.release();
		_doPostBack=null;
		Log.w(TAG,"release AsyncGet");
	}
	public interface MyDoPostBack {
		public void DoPostBack(String json);
	}

	public void setReadTimeout(int timeout) {
		get.setReadTimeout(timeout);
	}

	public void setConnectTimeout(int timeout) {
		get.setConnectTimeout(timeout);
	}
}
