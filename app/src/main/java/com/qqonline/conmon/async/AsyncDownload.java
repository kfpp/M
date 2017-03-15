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
	 * 异步下载文件
	 * @param path   下载完成的文件要保存到哪个路径下
	 * @param fileName   下载完成的文件要保存为什么名字
	 * @param postBack	 下载完成后的回调函数
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
		// 如果文件存在本地缓存目录，则不去服务器下载
		if (file.exists()) {
			return file.getAbsolutePath();// Uri.fromFile(path)这个方法能得到文件的URI
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
					// Log.i("dp", "写入");
					// 返回一个URI对象
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
