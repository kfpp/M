package com.qqonline.update;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.net.GetByHttpUrlConnection;
import com.qqonline.interfaces.IUrlGetable;
import com.qqonline.mpf.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class update {
	public static final String TAG = "update";
	private static final String savePath = "/sdcard/updateformpf/";//要下载的apk存放sd卡存放目录
	private static final String saveFileName = savePath + "UpdateMpfRelease.apk";//对应的文件名称
	private ProgressBar mProgress;
	private TextView protext;
	private Dialog downloadDialog;
	private String server;
	private String url;
	private String _updateContent;
	private Boolean interceptFlag = false;
	private boolean isStop=false;
	private GetUrlAnsykTask task;
//	private activityWeakReference.get() activityWeakReference.get();//此处改成你自己的activity类名
	private WeakReference<Activity> activityWeakReference;
	public update(String server,String url,Activity activity){
		this.server = server;
		this.url = url;
		activityWeakReference=new WeakReference(activity);
	}
	public void  releaseActivity()
	{
//		activityWeakReference.get()=null;
	}
	/*
	 * 启动更新
	 */
	public void start() throws Exception{
		 
		checkUpdateInfo();
	}
	public void stop(boolean isStop)
	{
		Log.i(TAG,"stop");
		this.isStop=isStop;
		if (isStop && task != null) {
//			task.cancel(true);
			task.stop();
			task=null;
			
		}
	}
	/**
	 *获取当前程序的 versionName
	*/
	public String getVersionName() throws Exception{  
	    PackageManager packageManager = activityWeakReference.get().getPackageManager();  
		PackageInfo packInfo = packageManager.getPackageInfo(activityWeakReference.get().getPackageName(), 0);  
		return packInfo.versionName;   
	}
	/**
	 * 获取当前程序的 versionCode
	 */
	public Integer GetVersionCode() throws Exception{
		PackageManager packageManager = activityWeakReference.get().getPackageManager();  
		PackageInfo packInfo = packageManager.getPackageInfo(activityWeakReference.get().getPackageName(), 0);  
		return packInfo.versionCode;   
	}
	public void checkUpdateInfo() throws Exception{
		Log.i(TAG,"start");
		task=new GetUrlAnsykTask();
		task.execute(url);
		
	}
	public class GetUrlAnsykTask extends AsyncTask<String, Integer, JSONObject> {
		private IUrlGetable get;
		public void stop()
		{
			isStop=true;
			Log.i(TAG,"updata->GetUrlAnsykTask.stop");
			if (get !=null) {
				get.release();
			}			
		}
		@Override
		protected JSONObject doInBackground(String... arg0) {
			Log.i(TAG,"updata->GetUrlAnsykTask.doInBackground");
			JSONObject content = null;
			try {
				get=new GetByHttpUrlConnection();		
				if (isStop) {
					return null;
				}
				String urlResponse = ByteUtil.bytesToString(get.getUrl(arg0[0]),"utf-8");
				if (urlResponse == null || urlResponse.equals("")) {
					return null;
				}
				content = new JSONObject(urlResponse);
				_updateContent=content.getString("remark");
				Log.i(TAG, urlResponse);
				Log.i(TAG, content.getString("version"));
				Log.i(TAG, getVersionName());
				Log.i(TAG, content.getString("path"));
				} catch (Exception e) {
					e.printStackTrace();
			}
			return content;
		}
		@Override
		protected void onCancelled() {
			Log.i(TAG,"updata->GetUrlAnsykTask.onCancelled");
			get.release();
			super.onCancelled();
		}
		protected void onPostExecute(JSONObject result) {
			Log.i(TAG,"updata->GetUrlAnsykTask.onPostExecute");
			if (result == null) {
				return;
			}
			try {
				checkDownload(getVersionName(), result.getString("version") ,result.getString("path"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
			
		}
	}
	/**
	 * 检查是否需要下载对应的apk
	 * version手机的版本，serverVersion服务器上的最新版本，path 下载地址
	 */
	private void checkDownload(String version, String serverVersion, String path) {	
		if(version.equals(serverVersion)){
			Log.i(TAG, "无需更新");
		}
		else{
			final String downFilePath = path;
			Log.i(TAG, downFilePath);
			Log.i(TAG, "需要更新：选择是否更新");
			new AlertDialog.Builder(activityWeakReference.get())
			.setTitle("软件更新提示")  
			.setIcon(android.R.drawable.ic_dialog_info)  
			.setMessage("检测到有最新版本，是否更新 \r\n"+"更新内容：\r\n"+_updateContent)  
			.setPositiveButton("更新", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					Log.i(TAG,"下载apk,更新");
					dialog.dismiss();
					downLoad(downFilePath);
				}
			})  
			.setNegativeButton("下次再说", new OnClickListener() { 
		        public void onClick(DialogInterface dialog, int which) { 
		        	dialog.dismiss();
		        } 
			})  
			.show();			
		}
	}
	private void downLoad(String downFilePath) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activityWeakReference.get());
		builder.setTitle("更新进度");
		final LayoutInflater inflater = LayoutInflater.from(activityWeakReference.get());
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progressBar02);
		protext = (TextView) v.findViewById(R.id.protext);
		protext.setText("0%");
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            	interceptFlag = true;
        	}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		new DownLoadAnsykTask().execute(downFilePath);
	}	
	public class DownLoadAnsykTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.i(TAG,"updata->DownLoadAnsykTask.doInBackground");
			try {
				Log.i(TAG, params[0]);
				URL url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				Log.i(TAG, "1");
                int length = conn.getContentLength();
                Log.i(TAG,String.valueOf(length));
                InputStream is = conn.getInputStream();
                File file = new File(savePath);
                if(!file.exists()){
                	Log.i(TAG,"文件夹不存在");
                    file.mkdir();
                }
                Log.i(TAG, "2");
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                Log.i(TAG, "3");
                do{
                	int numread = is.read(buf);
                	count += numread;
                	publishProgress((int) ((count / (float) length) * 100));
                	if(numread <= 0){ 
                		publishProgress(100);
                		break;
                	}
                	fos.write(buf,0,numread);
                }while(!interceptFlag);
                fos.close();
                is.close();
			}
			catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }

			return null;
		}
		protected void onPostExecute(String result) {
			Log.i(TAG,"updata->DownLoadAnsykTask.onPostExecute");
			Log.i(TAG, "完成");
			installApk();
			super.onPostExecute(result);
		}
		protected void onProgressUpdate(Integer... values) {
	         // 更新进度
	        Log.i(TAG, ""+values[0]);
	        mProgress.setProgress(values[0]);
	        protext.setText(values[0]+"%");
	     }
	}
	//安装apk
	protected void installApk() { 
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
	    Intent intent = new Intent(); 
	    intent.setAction(Intent.ACTION_VIEW); 
	    intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");   
	    activityWeakReference.get().startActivity(intent);
	}
}
