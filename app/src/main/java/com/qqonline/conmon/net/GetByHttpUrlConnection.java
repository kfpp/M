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
		 * ����HttpUrlConnection��disconnect()����û��HttpClient��shutdown()������ô��Ч
		 * HttpUrlConnection��disconnect()�������ú����û�а취ǿ���˳��������ݣ������ǻ�һֱ�ȴ�����ʱ��
		 * ������滹��һֱ������
		 * ��������䷢�֣���һ��ʱ�����ڣ����ʱ��������̫�̣����粻��ֱ����д����disconnect()��
		 * ��������disconnect���������Ļ����Ż�ͨ����������
		 * ���˲²⣬����������������ε�ԭ������ǣ�
		 * ���ӵ������ζϿ����������������ڲ�����Ȼ������HttpUrlConnection��disconnect����Ҳ�����ء�
		 * ���������һ��Сʱ�������ܵ��õڶ��Σ�ԭ������Ƿ������ú���һЩ������ִ�У�����Ҫһ��ʱ�䡣
		 *
		 * ���ԣ�������ڶ��ε��ò��ü򵥵��̵߳ķ�ʽ �������м��һ��Сʱ��������������߳�˯�ߣ�
		 * ˯��ʱ������500 ���룬���ó��˵Ļ������л���ʱ����ܻ�о����Եĺ������л���������
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
					 * ˯��һ����ٴε��ã������ӳ������ܳ��׷���
					 */
					conn.disconnect();
				}
			}).start();
			Log.i(TAG, "release url:" + conn.getURL().toString());
		}
	}

}
