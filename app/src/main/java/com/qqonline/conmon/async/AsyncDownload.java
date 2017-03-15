package com.qqonline.conmon.async;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncDownload extends AsyncTask<String, Integer, String> {

	private final static String TAG="AsyncDownload";
	private DoPostBack postBack;
	private String path;
	private String fileName;
	/**
	 * �첽�����ļ�
	 * @param path   ������ɵ��ļ�Ҫ���浽�ĸ�·����
	 * @param fileName   ������ɵ��ļ�Ҫ����Ϊʲô����
	 * @param postBack	 ������ɺ�Ļص�����
	 */
	public AsyncDownload(String path, String fileName, DoPostBack postBack) {
		this.postBack = postBack;
		this.path = path;
		this.fileName = fileName;		
	}
	@Override
	protected String doInBackground(String... params) {
		Log.i(TAG,"doInBackground");
		File file = new File(path, fileName);
		// Log.i("file", file.toString());
		// ����ļ����ڱ��ػ���Ŀ¼����ȥ����������
		if (file.exists()) {
			return file.getAbsolutePath();// Uri.fromFile(path)��������ܵõ��ļ���URI
		} else {
			URL url;
			if (!params[0].startsWith("http")) {
				return null;
			}
			try {
				url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 200) {
					
					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
						// Log.i("dp", fos.toString());
					}
					is.close();
					fos.close();
					// Log.i("dp", "д��");
					// ����һ��URI����
					String type=conn.getContentType();
					if(type.equals("text/plain"))
					{
						String json=new String(buffer,"utf-8");
						return "error:"+json;
					}
					Log.i("file", file.toString());
					return file.getAbsolutePath();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	@Override
	protected void onPostExecute(String result) {
		Log.i(TAG,"onPostExecute");
		postBack.doPostBack(result);
	}
	public interface DoPostBack
	{
		public void doPostBack(String result);
	}
	
}
