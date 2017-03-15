package com.qqonline.conmon.net;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.util.Log;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.DATA;
import com.qqonline.interfaces.IUrlPostable;

public class PostByHttpClient implements IUrlPostable {

	private static final String TAG="GetByHttpClient";
	private HttpClient httpClient;
	private String url;
	public PostByHttpClient() {
		httpClient=new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DATA.NETWORDCONNECTTIMEOUT);
	}
	@Override
	public byte[] postUrlWithNoParams(String url) {		
		return postUrlWithParams(url, null);
	}

	@Override
	public byte[] postUrlWithParams(String url, List<NameValuePair> params) {
		HttpEntity entity=null;
		if (params != null) {
			try {
				entity=new UrlEncodedFormEntity(params);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}			
		}
		return postUrlWithEntity(url, entity);
	}

	@Override
	public byte[] postUrlWithJsonParams(String url, String jsonParam) {
		HttpEntity entity=null;
		if (jsonParam != null) {
			try {
				entity=new StringEntity(jsonParam);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		return postUrlWithEntity(url, entity);
	}
	private byte[] postUrlWithEntity(String url,HttpEntity entity)
	{
		Log.i(TAG,"Post Url: "+url);
		this.url=url;
		HttpPost post=new HttpPost(url);
		byte[] bytes=null;
		if (entity != null) {
			post.setEntity(entity);			
		}
		try {
			HttpResponse response=httpClient.execute(post);
			int resultCode=response.getStatusLine().getStatusCode();
			if (resultCode == 200) {
				InputStream is=response.getEntity().getContent();
				bytes=ByteUtil.inputStreamToByteArray(is);
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return bytes;
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
		Log.i(TAG,"release url:"+url);
		httpClient.getConnectionManager().shutdown();
	}

}
