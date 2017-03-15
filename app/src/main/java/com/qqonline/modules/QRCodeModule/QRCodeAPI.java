package com.qqonline.modules.QRCodeModule;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.conmon.DATA;
import com.qqonline.mpf.R;

/**
 * Created by Administrator on 2015/10/27 0027.
 */
public class QRCodeAPI {

    public static void show(final Activity activity)
    {
        final ImageView tempView=new ImageView(activity);

        final AlertDialog.Builder builder= new AlertDialog.Builder(activity)
                .setTitle(R.string.clock_activity_alert_dialog_title)
                .setNegativeButton(R.string.OK, null);
        MpfMachineService service=new MpfMachineService(activity, DATA.DATAVERSION);
        int dbID=service.getMPFDBID();
        QRCodeUtil.IDoPostBack callback = new QRCodeUtil.IDoPostBack()
        {
            @Override
            public void BitmapLoaded(Bitmap bitmap) {
                tempView.setImageBitmap(bitmap);
                builder.setView(tempView);
                builder.show();
            }

            @Override
            public void OnError(int errCode, String error) {
//                if (errCode == 40001) {
//                    show(activity);
//                }
                builder.setMessage(error);
                builder.show();
            }
        };
        QRCodeUtil qrUtil=new QRCodeUtil(activity, dbID,callback);
        qrUtil.asyncGetBitmap();
    }
}
