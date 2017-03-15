package com.qqonline.mpf;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.getUrlResponse;
import com.qqonline.conmon.net.PostByHttpClient;
import com.qqonline.interfaces.IUrlPostable;
/**
 * 
 * @author fengcheng.ye,YE
 *
 */
public class getIsPlay implements Runnable {
	private MPFApp mApp;
	private IUrlPostable post;
	private boolean isStop;
	public getIsPlay(MPFApp mApp) {
		this.mApp = mApp;
		isStop=false;
	}

	public void stop()
	{
		if (post != null) {
			isStop=true;
			post.release();
		}
	}
	@Override
	public void run() {
		post=new PostByHttpClient();
		String arr=null;
		try {
			arr =ByteUtil.bytesToString( post.postUrlWithNoParams(DATA.adsIsPlayUrl));
			if(arr.equals("true"))
				mApp.setAdsIsPlay(true);
			else
				mApp.setAdsIsPlay(false);
			if (isStop) {
				return;
			}
			arr=ByteUtil.bytesToString( post.postUrlWithNoParams(DATA.adsIsPlayPicAcitiveUrl));
			if(arr.equals("true"))
				mApp.setAdsIsPlayPicActive(true);
			else
				mApp.setAdsIsPlayPicActive(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
