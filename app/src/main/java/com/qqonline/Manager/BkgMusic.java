package com.qqonline.Manager;

import com.qqonline.conmon.MPFApp;
import com.qqonline.mpf.CitySetting;
import com.qqonline.mpf.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class BkgMusic {

	private Activity activity;
	private MPFApp mApp;
	private String musicSwitch;
	private SharedPreferences shared;
	public BkgMusic(Activity activity) {
		this.activity = activity;
		mApp = (MPFApp) this.activity.getApplication();
		musicSwitch=this.activity.getResources().getStringArray(R.array.PreferenceArray)[1];
		shared=this.activity.getSharedPreferences(this.activity.getResources().getStringArray(R.array.PreferenceFileNameArray)[0], 
				CitySetting.MODE_PRIVATE);
	}

	public void play() {
		if (mApp.getBkPlayer() == null) {
			String flagStr= shared.getString(musicSwitch, "music1");
			MediaPlayer player = MediaPlayer.create(activity, getMusicResourceId(flagStr));
			player.setLooping(true);
			mApp.setBkPlayer(player);
		}
		if (mApp.isBkgMusicEnabled() && !mApp.getBkPlayer().isPlaying()) {
			mApp.getBkPlayer().start();
		}

	}
	public void changeBkgMusicAndPlay(int id)
	{
		mApp.getBkPlayer().stop();
		MediaPlayer player = MediaPlayer.create(activity, id);
//		SharedPreferenceUtil.setPreference(shared, musicSwitch, "music"+String.valueOf(id));
		player.setLooping(true);
		mApp.setBkPlayer(player);
		if (mApp.isBkgMusicEnabled() && !mApp.getBkPlayer().isPlaying()) {
			mApp.getBkPlayer().start();
		}
	}
	private int getMusicResourceId(String music)
	{
		if (music.equals("false") || music.equals("music1")) {
			return R.raw.bkmusic;
		}
		else if(music.equals("music2"))
		{
			return R.raw.bkmusic;
		}
		else if(music.equals("music3"))
		{
			return R.raw.bkmusic;
		}
		return R.raw.bkmusic;
	}

}
