package com.qqonline.view;



import com.qqonline.mpf.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class WaitDialog extends BaseAlertDialog {

	public WaitDialog(Context context) {
		TAG="WaitDialog";
		String text=context.getResources().getString(R.string.waiting);
		this.context=context;
		this.title=text;
		this.message=text;
		this.icon=android.R.drawable.ic_dialog_info;
	}
	@Override
	protected Dialog newDialog() {
		ProgressDialog dialog=new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setIcon(icon);
		dialog.setMessage(message);
		this.dialog=dialog;
		return dialog;
	}

}
