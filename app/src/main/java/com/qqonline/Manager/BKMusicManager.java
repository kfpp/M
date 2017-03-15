package com.qqonline.Manager;

import java.io.File;
import java.io.IOException;

import com.qqonline.conmon.async.AsyncDownload;
import com.qqonline.conmon.async.AsyncDownload.DoPostBack;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.mpf.CitySetting.MusicInfo;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.widget.Toast;
/**
 * ���š���������
 * @author YE
 *
 */
public class BKMusicManager {

	private Context context;
	private MPFApp application;
	private final static String path=DATA.MUSICCACHEPATH;
	public BKMusicManager(Activity context) {
		this.context=context;
		application=(MPFApp) context.getApplication();
	}
	/**
	 * ������������
	 * @param info
	 */
	public void playMusicWithoutDownload(MusicInfo info)
	{
		if (fileExistLocation(info)) {
			playLocationMusic(info);
		}
		else {
			playNetWorkMusic(info);
		}
	}
	/**
	 * ���ñ�������
	 * @param info
	 */
	public void setBkGMusic(MusicInfo info,OnDownloadFinished finished)
	{
		if (fileExistLocation(info)) {
			finished.onDownloadFinished(path+File.separator+info.getMusicName());
		}
		else {
			downloadMusic(info,false,finished);
		}
	}
	/**
	 * �������ֵ�ͬʱ����
	 * @param info
	 */
	public void playMusicAndDownload(MusicInfo info)
	{
		
	}
	/**
	 * �������� 
	 * @param music Ҫ���ص�����
	 * @param cover ����ļ����ڣ��Ƿ񸲸�
	 * @param finished ������ɣ��ص�
	 */
	public void downloadMusic(MusicInfo music,boolean cover,final OnDownloadFinished finished)
	{
	//	String path=GetExtSDCardPath.getSDCardPath()+File.separator+"BkgMusic";
		File pathDir=new File(path);
		if (!pathDir.exists()) {
			pathDir.mkdir();
		}
		File file=new File(pathDir, music.getMusicName());
		if (file.exists()) {
			if (!cover) {
				return;
			}
		}	
		AsyncDownload download=new AsyncDownload(path, music.getMusicName(), new DoPostBack() {			
			@Override
			public void doPostBack(String result) {
				if (result == null) {
					Toast.makeText(context, "������������ʧ��", Toast.LENGTH_SHORT).show();
					return;
				}
				finished.onDownloadFinished(result);
			}
		});
		download.execute(music.getMusicURL());
	}
	/**
	 * ���������ϵ�����
	 * @param info
	 * @param player2 
	 */
	private void playNetWorkMusic(MusicInfo info) {
		try {
			if (application.getListenPlayer() != null) {
				application.getListenPlayer().release();
				application.setListenPlayer(null);
			}
			MediaPlayer player = new MediaPlayer();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(context,
					Uri.parse(info.getMusicURL()));
			player.prepareAsync();
			player.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
				}
			});
			application.setListenPlayer(player);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ���ű��ص�����
	 * @param info
	 * @param player2 
	 */
	private void playLocationMusic(MusicInfo info) {
		try {
			if (application.getListenPlayer() != null) {
				application.getListenPlayer().release();
				application.setListenPlayer(null);
			}
//			if (player != null) {
//				player.stop();
//				player.release();
//				player=null;
//			}
			File pathDir=new File(path);
			if (!pathDir.exists()) {
				return;
			}
			File file=new File(pathDir, info.getMusicName());
			if (!file.exists()) {
				return;
			}
			MediaPlayer player = new MediaPlayer();
			
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(context,
					Uri.parse(file.getAbsolutePath()));
			player.prepareAsync();
			player.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
				}
			});
			application.setListenPlayer(player);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * �жϸ������Ƿ���ڱ���
	 * @param info
	 * @return
	 */
	public boolean fileExistLocation(MusicInfo info) {
	//	String path=GetExtSDCardPath.getSDCardPath()+File.separator+"BkgMusic";
		File pathDir=new File(path);
		if (!pathDir.exists()) {
			return false;
		}
		File file=new File(pathDir, info.getMusicName());
		if (!file.exists()) {
			return false;
		}	
		return true;
	}
	public interface OnDownloadFinished
	{
		public void onDownloadFinished(String path);		
	}
	
}
