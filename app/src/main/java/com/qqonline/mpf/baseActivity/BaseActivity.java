package com.qqonline.mpf.baseActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.qqonline.Manager.CheckNetWork;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.update.update;

public class BaseActivity extends AppCompatActivity {

	protected String TAG="BaseActivity";

	/**
	 * Application的全局引用
	 */
	protected MPFApp app;

	/**
	 * 当前Activity的引用，由子类Activity传进来，
	 * 主要用于：将当前Activity添加进Activity队列或从队列中移除
	 */
	protected Activity activity;
	/**
	 * 软件更新工具类的引用
	 */
	private update up;
	/**
	 * 是否取消顶部标题栏，该标志需要在子类调用本基类的 OnCreate 方法前设置。
	 */
	protected boolean noTitleBar;
	/**
	 *  是否取消底部导航栏。该标志需要在子类调用本基类的 OnCreate 方法前设置。
	 */
	protected boolean noNavigationBar;
	protected boolean noStateBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");

		if (noNavigationBar) {
			disableNavigationBar();
		}
		if (noStateBar) {
			disableStateBar();
		}
		super.onCreate(savedInstanceState);
/*		if (noTitleBar) {
			disableTitleBar();
		}*/
		app=MPFApp.getInstance();

	}
	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		if (activity != null) {
			app.addActivity(activity);
		}
	}
	@Override
	protected void onPause() {
		Log.i(TAG,"onPause");
		super.onPause();

	}
	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();

		if (up != null) {
			up.stop(true);
			up.releaseActivity();
		}

	}
	@Override
	protected void onRestart() {
		Log.i(TAG,"onRestart");
		super.onRestart();
	}
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		if (activity != null) {
			app.removeActivity(activity);
		}
		this.activity=null;
	}
	protected void checkNetWork(Activity activity)
	{
		CheckNetWork check=new CheckNetWork(activity);
		check.check();
	}
	protected void checkUpdate(Activity activity)
	{
		up = new update(DATA.server,DATA.updateUrl,activity);
		try {
			up.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void disableTitleBar()
	{
	//	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActionBar actionBar=getSupportActionBar();
		actionBar.hide();

	}
	private void disableNavigationBar()
	{
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}

	private void disableStateBar() {
		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//			startExternalMusicPlayer();
//			return true;
//		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 主要功能：响应按钮以弹出外部音乐软件
	 */
	/*private void startExternalMusicPlayer() {

		try {
			Intent intent = getPackageManager().getLaunchIntentForPackage("com.kugou.playerHD");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setType("audio*//*");
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(activity, R.string.download_music_player,Toast.LENGTH_LONG).show();
			downloadMusicPlayer();
			e.printStackTrace();
		}

	}

	private void downloadMusicPlayer() {
		MyDownloadManager downloadManager=new MyDownloadManager(DATA.URL_KUGO_PAD_VERSION,"Kugou.apk");
		downloadManager.downloadAndOpen();
	}*/
}
