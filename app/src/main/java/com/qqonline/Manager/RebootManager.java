package com.qqonline.Manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.qqonline.service.RebootService;

import java.util.Calendar;

/**
 * App 重启管理
 * Created by Administrator on 2015/12/14 0014.
 */
public class RebootManager {
    /**
     * 在几点重启，默认凌晨4点
     */
    private static int REBOOT_TIME_HOUR = 4;

    /**
     * 设置一个定时重启的闹钟
     * <br />时间默认设在上午4点,如果今天的时间已过，则设在明天
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
            //今天的重启时间已过，设置明天的定时任务(将日期加1天，定位到明天)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }
        calendar.set(Calendar.HOUR_OF_DAY, REBOOT_TIME_HOUR);*/
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        //TODO:添加定时器要完成的任务
    }

    /**
     * 启动机器，并启动下一次定时
     */
    public static void reBoot(Context context) {
        reBoot(context, true);
    }

    /**
     * 启动机器，并设置是否启动下一次定时
     *
     * @param context  context
     * @param isIntval 设置是否启动下一次定时
     */
    public static void reBoot(Context context, boolean isIntval) {
        if (isIntval) {
            setRebootAlarm(context);
        }
        //重启应用的activity
        Intent i = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        /*Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context. startActivity(intent);*/

    }
}
