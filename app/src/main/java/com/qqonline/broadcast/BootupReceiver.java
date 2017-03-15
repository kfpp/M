package com.qqonline.broadcast;

import com.qqonline.Manager.RebootManager;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.mpf.MainActivity;
import com.qqonline.mpf.SocketIoClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author fengcheng.ye
 */
public class BootupReceiver extends BroadcastReceiver {

    private MPFApp app;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED) &&
                intent.getData().getSchemeSpecificPart().equals(context.getPackageName())) {
            /**��������Զ�����*/
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
            RebootManager.setRebootAlarm(context);
            if (app == null) {
                app = MPFApp.getInstance();
            }
            if (app.getVersionType() == DATA.VERSIONTYPE.Topiserv) {
                //����ֱ�ӽ��뵽������
                Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else if (app.getVersionType() == DATA.VERSIONTYPE.SmartHome) {
                //���ܼҾ��뵽�Ŀ���ֻ������̨����
                Intent i = new Intent(context, SocketIoClient.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(i);
            }
        }

    }

}
