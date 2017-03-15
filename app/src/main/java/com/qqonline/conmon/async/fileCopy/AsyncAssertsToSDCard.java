package com.qqonline.conmon.async.fileCopy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.qqonline.view.BaseAlertDialog;
import com.qqonline.view.WaitDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

public class AsyncAssertsToSDCard extends AsyncTask<String, String, Boolean> {

	private BaseAlertDialog showDialog;
	private Context context;
	private AsyncAssertsToSDCardDone done;
	private String filePath;
	private String assertsFileName;
	public AsyncAssertsToSDCard(Context context,AsyncAssertsToSDCardDone done) {
		this.context=context;
		this.done=done;
	}
	@Override
	protected void onPreExecute() {
		showDialog=new WaitDialog(context);
		showDialog.show();
		super.onPreExecute();
	}
	@Override
	protected Boolean doInBackground(String... params) { //p0:路径;p1:文件名;P2：Asserts目录下的哪个文件
		String diretory=params[0];
		assertsFileName=params[2];
		File dir=new File(diretory);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String name=params[1];
		File file=new File(dir, name);
		filePath=file.getAbsolutePath();
		if (file.exists()) {
			//return true;
		}		
		return copyFileFormAssertsToSDCard(filePath);
	}
	@Override
	protected void onPostExecute(Boolean result) {
		showDialog.dismiss();
		if (result.booleanValue()) {
			done.onFinished(filePath);
		}
		super.onPostExecute(result);
	}
	public interface AsyncAssertsToSDCardDone
	{
		public void onFinished(String filePath);
	}
	private boolean copyFileFormAssertsToSDCard(String toWhere)
	{
		InputStream is=null;
		FileOutputStream os=null;
		try {
			is=context.getAssets().open(assertsFileName);
			os=new FileOutputStream(toWhere);
			byte[] buff=new byte[1024];
			int count=0;
			while ((count = is.read(buff)) > 0) {
				os.write(buff, 0, count);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
