package com.qqonline.conmon.async;

import com.qqonline.mpf.PicClockActivity;
import com.qqonline.mpf.R;

import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class AsyncQRCode extends AsyncTask<String, Integer, Bitmap> implements DialogInterface.OnClickListener{

	private Context context;
	private ProgressDialog dialog;
	private Resources resources;
	private boolean isCancled,isShowDialog;
	private ImageView imageView;
	private Bitmap bitmap;
	public AsyncQRCode(Context context) {
		this.context=context;
		resources=context.getResources();
		isCancled=false;
		isShowDialog=true;
		imageView=null;
		bitmap=null;
	}
	public void setImageView(ImageView iv)
	{
		this.imageView=iv;
	}
	public void setIsShowDialog(boolean flag)
	{
		this.isShowDialog=flag;
	}
	public Bitmap getBitmap()
	{
		return this.bitmap;
	}
	@Override
	protected void onPreExecute() {
		if(!isShowDialog)
		{
			return ;
		}
		dialog=new ProgressDialog(context);
		dialog.setTitle(R.string.wait_for_QRCode);
		dialog.setMessage(resources.getString(R.string.waiting));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, resources.getString(R.string.Cancle), this);
		dialog.show();
		super.onPreExecute();
	}
	@Override
	protected Bitmap doInBackground(String... params) {

		
		return null;
	}
	@Override
	protected void onPostExecute(Bitmap result) {
		this.bitmap=result;
		if(isShowDialog && !isCancled)
		{
			dialog.dismiss(); //取消等待对话框的显示
		}
		if(isCancled)
		{
			return;         
		}
		if(imageView != null)
		{
			imageView.setImageBitmap(result);
		}
		if(isShowDialog)
		{
			ImageView tempView=new ImageView(context);
			tempView.setImageBitmap(result);
			tempView.setPadding(0, 30, 0, 30);
			new AlertDialog.Builder(context)
			.setTitle(R.string.clock_activity_alert_dialog_title)
			.setView(tempView)
			.setNegativeButton(R.string.OK, null)
			.show();
		}
	}
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEGATIVE:
			isCancled=true;
			dialog.dismiss();
			break;
		default:
			break;
		}
	}
	public void release()
	{
		isCancled=true;
		context=null;
		imageView=null;
		bitmap=null;
		resources=null;
	}

}
