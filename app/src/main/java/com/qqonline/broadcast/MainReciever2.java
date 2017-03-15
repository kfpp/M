package com.qqonline.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.getMd5;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.mpf.PicPlayActivity;
import com.qqonline.mpf.PicPlayActivity2;
import com.qqonline.mpf.R;

import java.io.File;

/**
 * Created by YE on 2015/8/26 0026.
 */
public class MainReciever2 extends BroadcastReceiver {
    private MediaPlayer picRecievePlayer;
    private MPFApp app;
    private Activity activity;

    public MainReciever2(Activity activity) {
        app = MPFApp.getInstance();
        this.activity = activity;
        picRecievePlayer = app.getPicRecievePlayer();
        if (picRecievePlayer == null) {
            picRecievePlayer = MediaPlayer.create(activity, R.raw.audio);
            app.setPicRecievePlayer(picRecievePlayer);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        //ͼƬ�����ַ
        final String picUrl = bundle.getString(DATA.bundleKey[0]);
        //��������ͼƬ��΢���û���OpenID
        final String openId = bundle.getString(DATA.bundleKey[1]);
        //������ͼƬ�����û�������
        final String name = bundle.getString(DATA.bundleKey[2]);
        //ͼƬ�����걣�浽����ʱ������
        String localPictureName = getMd5.Md5(picUrl, 32);
        //ͼƬ�ŵ��ĸ�Ŀ¼
        File cache = new File(new File(GetExtSDCardPath.getSDCardPath()), DATA.sdCardPicFile);
        if (!cache.exists()) {
            cache.mkdir();
        }
        if (!"".equals(picUrl))//���֪ͨ�����µ���Ƭ
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!picRecievePlayer.isPlaying()) {
                        picRecievePlayer.start();
                    }
                }
            }).start();//�����µ��̲߳�������
            Toast.makeText(activity,"��������"+name+"����Ƭ",Toast.LENGTH_SHORT).show();

            if (isPlayingPictures()) {
                reloadPictures();
            } else {
                showPlayActivity();
            }
        }
    }
    /**
     * ���ص�ǰ����Ķ���Activity
     * @return
     */
    private Activity getTopActivity()
    {
        final Activity topActivity=app.getActivityList().get(0);
        return topActivity;
    }
    /**
     * �жϵ�ǰ�ǲ��������ڲ���ͼƬ�Ľ��棬
     * @return
     */
    private boolean isPlayingPictures()
    {
        final Activity topActivity=getTopActivity();
        if (topActivity instanceof PicPlayActivity2) {
            return true;
        }
        return false;
    }

    /**
     * ��ת��ͼƬ���Ž���
     */
    private void showPlayActivity() {
        final Activity topActivity=getTopActivity();
        Intent intent = new Intent(topActivity, PicPlayActivity2.class);
        Bundle bundle = new Bundle();
        bundle.putString("OpenId", "");
        bundle.putString("type", "");
        intent.putExtras(bundle);
        topActivity.startActivity(intent);
    }

    /**
     * ��������Ѿ��ڲ����ˣ������¼���ͼƬ������ʾ���µ�����
     */
    private void reloadPictures(){
        PicPlayActivity2 activity=null;
        try {
            activity=(PicPlayActivity2)getTopActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activity != null) {
            activity.refreshPictureList();
        }
    }


}
