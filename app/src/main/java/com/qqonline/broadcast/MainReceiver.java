package com.qqonline.broadcast;

import java.io.File;

import com.qqonline.conmon.async.AsyncDownload;
import com.qqonline.conmon.async.AsyncDownload.DoPostBack;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.getMd5;
import com.qqonline.mpf.MainActivity;
import com.qqonline.mpf.PicPlayActivity;
import com.qqonline.mpf.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

/**
 * MainActivity的广播接收者<br/>
 * 接收到新图片的Reciever《旧》，本接收者和旧版的图片播放界面关联<br/>
 * 新版请看 MainReceiver2
 * @author fengcheng.ye
 *
 */
public class MainReceiver extends BroadcastReceiver {
	private MainActivity activity;
	private MediaPlayer picRecievePlayer;
	private MPFApp application;
	public MainReceiver(MainActivity activity){
		this.activity = activity;
		application=(MPFApp)activity.getApplication();
		picRecievePlayer=application.getPicRecievePlayer();
		if(picRecievePlayer == null)
		{
			picRecievePlayer=MediaPlayer.create(activity, R.raw.audio);
			((MPFApp)activity.getApplication()).setPicRecievePlayer(picRecievePlayer);
		}
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
//		String bindCode = bundle.getString(DATA.bundleKey[3]);
//		if (bindCode!=null || !"".equals(bindCode)) {
//			MpfMachineService service=new MpfMachineService(activity, DATA.DATAVERSION);
//			service.MpfMachineUpdateBindCode(bindCode);
//			return;
//		}
		//图片网络地址
		final String picUrl = bundle.getString(DATA.bundleKey[0]);
		//发送这张图片的微信用户的OpenID
		final String openId=bundle.getString(DATA.bundleKey[1]);
		//发这张图片到的用户的名字
		final String name = bundle.getString(DATA.bundleKey[2]);
		//图片下载完保存到本地时的名字
		String localPictureName=getMd5.Md5(picUrl, 32);
		//图片放到哪个目录
		File cache = new File(new File(GetExtSDCardPath.getSDCardPath()),DATA.sdCardPicFile);
		if (!cache.exists()) {
			cache.mkdir();
		}
		if(!"".equals(picUrl))//这个通知是有新的照片
		{
			new Thread(new Runnable(){
				@Override
				public void run() {
					if (!picRecievePlayer.isPlaying()) {
						picRecievePlayer.start();
					}				
				}
			}).start();//启用新的线程播放声音
			activity.showToast("您有来自"+name+"的相片");
			
			PicPlayActivity.newPictureRecieved++;
			/**
			 * 如果是处于正丰播放图片的界面，则停止图片的轮播，以防止有的时候图片刚显示出来就被轮播定时器切换过去BUG;
			 */
			if (isPlayingPictures()) {
				PicPlayActivity picActivity = (PicPlayActivity) getTopActivity();
				picActivity.StopAutoChangePicture();
			}
			
//			AsyncImageTask imageDownload = new AsyncImageTask(activity,bundle,false);
//			imageDownload.execute(picUrl);
			AsyncDownload download=new AsyncDownload(cache.getAbsolutePath(), localPictureName, new DoPostBack() {				
				@Override
				public void doPostBack(String result) {
					downloadCallback(result);
					
				}
			});
			download.execute(picUrl);
		}
		else {//为空则是更新用户信息
//			activity.loadUser();
		}
//		if (bindCode!=null && !bindCode.equals("")) {
//			MpfMachineService service=new MpfMachineService(activity, DATA.DATAVERSION);
//			service.MpfMachineUpdateBindCode(bindCode);
//		}
	}
	/**
	 * 新图片异步下载完成时的回调函数
	 * @param result
	 */
	private void downloadCallback(String result)
	{
		final Activity topActivity=getTopActivity();
		/**
		 * 如果当前界面不是播放界面，则跳转到播放界面,每次启动播放界面时都会自动重新加载图片列表，从最新的那张开始显示。
		 */
		if (!isPlayingPictures()) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("OpenId", "");
			bundle.putString("type", "");
			intent.putExtras(bundle);
			intent.setClassName(topActivity, "com.qqonline.mpf.PicPlayActivity");
			topActivity.startActivity(intent);
		}	
		else {                           //如果当前界面是播放界面，则重新加载并显示图片列表
			try {
				//清空openid数据，显示全部图片，阻止当从图片总览中选择显示某个用户的图片时，有新图片进来有提示但不显示新图片
				((PicPlayActivity)topActivity).checkOpenId="";  
				//设置起始图片ID为0，表示从头开始显示，同样是为了防止以显示某一张图片而进入到图片显示界面后，当有新图片进来都默认从之前的DbId图片开始显示的BUG
				((PicPlayActivity)topActivity).startPictureDbId=0;
				//重新加载图片并显示
				((PicPlayActivity)topActivity).loadPic(false);         
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	/**
	 * 判断当前是不是正处于播放图片的界面，
	 * @return 
	 */
	private boolean isPlayingPictures()
	{
		final Activity topActivity=getTopActivity();
		if (topActivity instanceof PicPlayActivity) {
			return true;			
		}	
		return false;
	}
	/**
	 * 返回当前程序的顶层Activity
	 * @return
	 */
	private Activity getTopActivity()
	{
		final Activity topActivity=application.getActivityList().get(0);
		return topActivity;
	}
	

}
