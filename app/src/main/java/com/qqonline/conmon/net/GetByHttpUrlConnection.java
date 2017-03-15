package com.qqonline.conmon.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.util.Log;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.DATA;
import com.qqonline.interfaces.IUrlGetable;

public class GetByHttpUrlConnection implements IUrlGetable {

	private static final String TAG = "GetByHttpUrlConnection";
	private HttpURLConnection conn = null;
	private int connectionTimeOut = DATA.NETWORDCONNECTTIMEOUT, readTimeOut = 0;
	private URL urlGet;

	@Override
	public byte[] getUrl(String url) {
		Log.i(TAG, "Url: " + url);
		byte[] bytes = null;
		try {
			urlGet = new URL(url);

			conn = (HttpURLConnection) urlGet.openConnection();
			Log.i(TAG, "URL:" + url);
			conn.setConnectTimeout(connectionTimeOut);

			if (readTimeOut > 0) {
				conn.setReadTimeout(readTimeOut);
			} else {
				conn.setReadTimeout(DATA.NETWORK_READ_TIME_OUT);
			}
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "text/html");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			Log.i(TAG,"HttpURLConnection begin!");
			if (conn.getResponseCode() == 200) {
				Log.i(TAG,"HttpURLConnection Done!");
				InputStream is = conn.getInputStream();
				bytes = ByteUtil.inputStreamToByteArray(is);
				is.close();
			} else {
				Log.i(TAG,"HttpURLConnection Done !"+conn.getResponseCode());
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "URL Error:" + url);
		}
		return bytes;
	}

	@Override
	public void setConnectTimeout(int timeout) {
		this.connectionTimeOut = timeout;

	}

	@Override
	public void setReadTimeout(int timeout) {
		this.readTimeOut = timeout;

	}

	@Override
	public void release() {
		/**
		 * 由于HttpUrlConnection的disconnect()方法没有HttpClient的shutdown()方法那么有效
		 * HttpUrlConnection的disconnect()方法调用后根本没有办法强制退出接收数据，它还是会一直等待到超时，
		 * 结果界面还是一直黑屏。
		 * 后来无意间发现，在一段时间间隔内（这个时间间隔不能太短，比如不能直接连写两行disconnect()）
		 * 调用两次disconnect（）方法的话，才会通出网络请求。
		 * 本人猜测，这个得连续调用两次的原因可能是：
		 * 连接调用两次断开会引发网络连接内部出错，然后由于HttpUrlConnection的disconnect跟着也出错返回。
		 * 而这个得有一个小时间间隔才能调用第二次，原因可能是方法调用后有一些程序在执行，这需要一点时间。
		 *
		 * 所以，在这里第二次调用采用简单的线程的方式 ，而这中间的一个小时间间隔，则采用让线程睡眠，
		 * 睡眠时间设这500 毫秒，设置长了的话界面切换的时候可能会感觉明显的黑屏（切换不流畅）
		 */
		if (conn != null) {
			conn.disconnect();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					/**
					 * 睡眠一秒后再次调用，让连接出错，才能彻底返回
					 */
					conn.disconnect();
				}
			}).start();
			Log.i(TAG, "release url:" + conn.getURL().toString());
		}
	}

}
