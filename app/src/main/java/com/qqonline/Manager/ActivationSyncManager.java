package com.qqonline.Manager;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.conmon.DATA;
import com.qqonline.mpf.MainActivity;
import com.qqonline.mpf.R;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2015/7/30 0030.
 */
public class ActivationSyncManager {
    private static final String TAG="ActivationSyncManager";
    private WeakReference<Activity> activityWeakReference;
    private MpfMachineService service;

    public ActivationSyncManager(Activity activity) {
        activityWeakReference = new WeakReference<Activity>(activity);
        service = new MpfMachineService(activityWeakReference.get(), DATA.DATAVERSION);
    }

    public void start() {
            if (!service.isInternalMachineHave() && service.isExternalMachineHave()) {
                showDialog();
            }
    }

    private void showDialog() {
        final Activity activity = activityWeakReference.get();
        if (activity == null) {
            return;
        }
        new AlertDialog.Builder(activity)
                .setTitle(R.string.warning)
                .setMessage(R.string.dialog_message_syncDataBase)
                .setPositiveButton(R.string.dialog_button_syncDataBase_OK, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        service.copyMachineDataToInternalDatabase();
                        Toast.makeText(activity,R.string.Toast_sync_compelete,Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.Cancle, null)
                .show();
    }
}
