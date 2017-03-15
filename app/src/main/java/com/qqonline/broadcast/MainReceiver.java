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
 * MainActivity�Ĺ㲥������<br/>
 * ���յ���ͼƬ��Reciever���ɡ����������ߺ;ɰ��ͼƬ���Ž������<br/>
 * �°��뿴 MainReceiver2
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
		//ͼƬ�����ַ
		final String picUrl = bundle.getString(DATA.bundleKey[0]);
		//��������ͼƬ��΢���û���OpenID
		final String openId=bundle.getString(DATA.bundleKey[1]);
		//������ͼƬ�����û�������
		final String name = bundle.getString(DATA.bundleKey[2]);
		//ͼƬ�����걣�浽����ʱ������
		String localPictureName=getMd5.Md5(picUrl, 32);
		//ͼƬ�ŵ��ĸ�Ŀ¼
		File cache = new File(new File(GetExtSDCardPath.getSDCardPath()),DATA.sdCardPicFile);
		if (!cache.exists()) {
			cache.mkdir();
		}
		if(!"".equals(picUrl))//���֪ͨ�����µ���Ƭ
		{
			new Thread(new Runnable(){
				@Override
				public void run() {
					if (!picRecievePlayer.isPlaying()) {
						picRecievePlayer.start();
					}				
				}
			}).start();//�����µ��̲߳�������
			activity.showToast("��������"+name+"����Ƭ");
			
			PicPlayActivity.newPictureRecieved++;
			/**
			 * ����Ǵ������Ქ��ͼƬ�Ľ��棬��ֹͣͼƬ���ֲ����Է�ֹ�е�ʱ��ͼƬ����ʾ�����ͱ��ֲ���ʱ���л���ȥBUG;
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
		else {//Ϊ�����Ǹ����û���Ϣ
//			activity.loadUser();
		}
//		if (bindCode!=null && !bindCode.equals("")) {
//			MpfMachineService service=new MpfMachineService(activity, DATA.DATAVERSION);
//			service.MpfMachineUpdateBindCode(bindCode);
//		}
	}
	/**
	 * ��ͼƬ�첽�������ʱ�Ļص�����
	 * @param result
	 */
	private void downloadCallback(String result)
	{
		final Activity topActivity=getTopActivity();
		/**
		 * �����ǰ���治�ǲ��Ž��棬����ת�����Ž���,ÿ���������Ž���ʱ�����Զ����¼���ͼƬ�б������µ����ſ�ʼ��ʾ��
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
		else {                           //�����ǰ�����ǲ��Ž��棬�����¼��ز���ʾͼƬ�б�
			try {
				//���openid���ݣ���ʾȫ��ͼƬ����ֹ����ͼƬ������ѡ����ʾĳ���û���ͼƬʱ������ͼƬ��������ʾ������ʾ��ͼƬ
				((PicPlayActivity)topActivity).checkOpenId="";  
				//������ʼͼƬIDΪ0����ʾ��ͷ��ʼ��ʾ��ͬ����Ϊ�˷�ֹ����ʾĳһ��ͼƬ�����뵽ͼƬ��ʾ����󣬵�����ͼƬ������Ĭ�ϴ�֮ǰ��DbIdͼƬ��ʼ��ʾ��BUG
				((PicPlayActivity)topActivity).startPictureDbId=0;
				//���¼���ͼƬ����ʾ
				((PicPlayActivity)topActivity).loadPic(false);         
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	/**
	 * �жϵ�ǰ�ǲ��������ڲ���ͼƬ�Ľ��棬
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
	 * ���ص�ǰ����Ķ���Activity
	 * @return
	 */
	private Activity getTopActivity()
	{
		final Activity topActivity=application.getActivityList().get(0);
		return topActivity;
	}
	

}
