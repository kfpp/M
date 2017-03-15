package com.qqonline.Manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.qqonline.service.RebootService;

import java.util.Calendar;

/**
 * App ��������
 * Created by Administrator on 2015/12/14 0014.
 */
public class RebootManager {
    /**
     * �ڼ���������Ĭ���賿4��
     */
    private static int REBOOT_TIME_HOUR = 4;

    /**
     * ����һ����ʱ����������
     * <br />ʱ��Ĭ����������4��,��������ʱ���ѹ�������������
     *
     * @param context context
     */
    public static void setRebootAlarm(Context context) {
        Intent intent = new Intent(context, RebootService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,1);

        /*int  nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        if (nowHour >= REBOOT_TIME_HOUR) {
            //���������ʱ���ѹ�����������Ķ�ʱ����(�����ڼ�1�죬��λ������)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }
        calendar.set(Calendar.HOUR_OF_DAY, REBOOT_TIME_HOUR);*/
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        //TODO:��Ӷ�ʱ��Ҫ��ɵ�����
    }

    /**
     * ������������������һ�ζ�ʱ
     */
    public static void reBoot(Context context) {
        reBoot(context, true);
    }

    /**
     * �����������������Ƿ�������һ�ζ�ʱ
     *
     * @param context  context
     * @param isIntval �����Ƿ�������һ�ζ�ʱ
     */
    public static void reBoot(Context context, boolean isIntval) {
        if (isIntval) {
            setRebootAlarm(context);
        }
        //����Ӧ�õ�activity
        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        /*Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context. startActivity(intent);*/

    }
}
