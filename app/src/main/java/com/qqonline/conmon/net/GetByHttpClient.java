package com.qqonline.conmon.net;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.util.Log;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.DATA;
import com.qqonline.interfaces.IUrlGetable;

public class GetByHttpClient implements IUrlGetable{

	private static final String TAG="GetByHttpClient";
	private HttpClient httpClient;

	private String url;
	private HttpGet get;
	public GetByHttpClient() {
		httpClient=new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DATA.NETWORDCONNECTTIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,DATA.NETWORK_READ_TIME_OUT);
//		httpClient.getParams().setParameter(HttpParams.HTTP_CONTENT_CHARSET,"utf-8");
	}
	@Override
	public byte[] getUrl(String url) {
		this.url=url;
		Log.i(TAG,"Url: "+url);
		get=new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(get);
			int resultCode = response.getStatusLine().getStatusCode();
			if (resultCode == 200) {
				InputStream is = response.getEntity().getContent();
				byte[] bytes = ByteUtil.inputStreamToByteArray(is);
				is.close();
//				HttpEntity entity=response.getEntity();
//				String content = EntityUtils.toString(entity);
//				byte[] bytes=content.getBytes("ISO-8859-1");
				return bytes;
			} else {
				get.abort();
				return null;
			}
		}catch (Exception e) {
			Log.e(TAG,url);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setConnectTimeout(int timeout) {
		if (timeout > 0) {
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
		}		
	}

	@Override
	public void setReadTimeout(int timeout) {
		if (timeout > 0) {
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
		}		
	}

	@Override
	public void release() {
		if (get != null) {
			get.abort();
		}
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
		Log.i(TAG, "shutdown url:" + url);
	}

}
