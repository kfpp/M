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
        //图片网络地址
        final String picUrl = bundle.getString(DATA.bundleKey[0]);
        //发送这张图片的微信用户的OpenID
        final String openId = bundle.getString(DATA.bundleKey[1]);
        //发这张图片到的用户的名字
        final String name = bundle.getString(DATA.bundleKey[2]);
        //图片下载完保存到本地时的名字
        String localPictureName = getMd5.Md5(picUrl, 32);
        //图片放到哪个目录
        File cache = new File(new File(GetExtSDCardPath.getSDCardPath()), DATA.sdCardPicFile);
        if (!cache.exists()) {
            cache.mkdir();
        }
        if (!"".equals(picUrl))//这个通知是有新的照片
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!picRecievePlayer.isPlaying()) {
                        picRecievePlayer.start();
                    }
                }
            }).start();//启用新的线程播放声音
            Toast.makeText(activity,"您有来自"+name+"的相片",Toast.LENGTH_SHORT).show();

            if (isPlayingPictures()) {
                reloadPictures();
            } else {
                showPlayActivity();
            }
        }
    }
    /**
     * 返回当前程序的顶层Activity
     * @return
     */
    private Activity getTopActivity()
    {
        final Activity topActivity=app.getActivityList().get(0);
        return topActivity;
    }
    /**
     * 判断当前是不是正处于播放图片的界面，
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
     * 跳转到图片播放界面
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
     * 如果现在已经在播放了，就重新加载图片，以显示最新的那张
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
