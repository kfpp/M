package com.qqonline.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

public abstract class BaseAlertDialog {

	protected String TAG;
	protected Context context;
	protected String title;
	protected String message;
	protected int icon;
	protected View view;
	protected String btnPositiveString;
	protected String btnNagetiveString;
	protected String btnMiddleString;
	protected Dialog dialog;
	protected abstract Dialog newDialog();
	public Dialog getDialog()
	{
		if (dialog == null) {
			dialog=newDialog();
		}
		return dialog;
	}
	public void show()
	{
		if (dialog == null) {
			dialog=newDialog();			
		}	
		dialog.show();
	}
	public void dismiss()
	{
		if (dialog !=null) {
			dialog.dismiss();
		}
	}
}
