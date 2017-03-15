package com.qqonline.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.qqonline.Manager.RebootManager;

/**
 * Created by Administrator on 2015/12/14 0014.
 */
public class RebootService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //RebootManager.reBoot(this);
        return super.onStartCommand(intent, flags, startId);
    }
}
