package com.qqonline.conmon.async.fileCopy;

import com.qqonline.conmon.FileCopy;
import com.qqonline.mpf.MainActivity;


import com.qqonline.mpf.R;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncFileCopy extends AsyncTask<String, Integer, Boolean> {
	private static final String TAG="AsyncFileCopy";
	private MainActivity activity;
	private Dialog waitingDialog;
	public AsyncFileCopy(MainActivity activity) {
		this.activity = activity;
	}
	@Override
	protected Boolean doInBackground(String... params) {
		Log.i(TAG,"doInBackground");
		String oldPath=params[0];
		String newPath=params[1];
		if (oldPath == null || newPath == null || oldPath.equals("") || newPath.equals("")) {
			return false;
		}
		try {
			FileCopy.copyFolder(oldPath, newPath);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	protected void onPreExecute() {
		Log.i(TAG,"onPreExecute");
		waitingDialog=new AlertDialog.Builder(activity)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(R.string.data_sync)
		.setMessage(R.string.waiting)
		.show();
	}
	@Override
	protected void onPostExecute(Boolean result) {
		Log.i(TAG,"onPostExecute");
		if (!result) {
			showFileCopyErrorDialog();
		}
		else {
			if (waitingDialog != null) {
				waitingDialog.dismiss();
			}	
			//因为改变了数据库和图片的存储路径，所以要重启以重新加载新路径下的数据库和图片
			activity.restartApplication();
					
		}
		
	}
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	private void showFileCopyErrorDialog()
	{
		new AlertDialog.Builder(activity)
		.setTitle(R.string.data_sync_error)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(R.string.data_sync_by_hand)
		.setNegativeButton(R.string.OK, null)
		.show();
	}

}
