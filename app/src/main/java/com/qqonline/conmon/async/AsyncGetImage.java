package com.qqonline.conmon.async;

import com.qqonline.conmon.getUrlResponse;
import com.qqonline.conmon.net.GetByHttpUrlConnection;
import com.qqonline.interfaces.IUrlGetable;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;

public class AsyncGetImage extends AsyncTask<String, Integer, byte[]> {

	private MyDoPostBack iPostBack;
	public AsyncGetImage(MyDoPostBack iPostBack) {
		this.iPostBack=iPostBack;
	}
	@Override
	protected byte[] doInBackground(String... params) {
		byte[] result=null;
		IUrlGetable get=new GetByHttpUrlConnection();
		try {
			result=get.getUrl(params[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	protected void onPostExecute(byte[] result) {
		if(iPostBack != null)
		{
			iPostBack.DoPostBack(result);
		}
	}
	public interface MyDoPostBack {
		public void DoPostBack(byte[] bytes);
	}
}
