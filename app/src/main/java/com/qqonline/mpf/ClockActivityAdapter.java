package com.qqonline.mpf;

import com.qqonline.conmon.DATA;

import android.view.Display;
import android.webkit.WebView;
/**
 * 按照分辨率自动加载对就的时钟界面和广告条
 * @author YE
 *
 */
public class ClockActivityAdapter {
	
	private WebView _clockView,_adsView;
	private Boolean _isLandscape;
	private String _clockPageURL;
	private int _width;
	private int _height;
	public ClockActivityAdapter(WebView clock,WebView piw,Boolean isLandscape,Display display) {
		// TODO Auto-generated constructor stub
		_clockView=clock;
		_adsView=piw;
		_isLandscape=isLandscape;
		_width=display.getWidth();
		_height=display.getHeight();
	}
	public void ShowClockView() {
		String url=GetClockURL();
		_clockView.loadUrl(url);
		_adsView.loadUrl(GetAdsURL());
	}
	private String GetClockURL()
	{
		if(_isLandscape)           //如果是横屏
		{
			if (_width <1000) {    //小分辨率横屏时钟
				return DATA.SmallHClockUrl;
			} else {               //大分辨率横屏时钟
				return DATA.HClockUrl;
			}
		}
		else {                        //如果是竖屏
			if (_width < 1000) {      //小分辨率竖屏时钟
				return DATA.SmallClockUrl;
			} else {                  //大分辨率竖屏时钟
				return DATA.ClockUrl;
			}
		}
		//return "";
	}
	private String GetAdsURL()
	{
		if(_isLandscape)           //如果是横屏
		{
			if (_width <1000) {    //小分辨率横屏时钟
				return DATA.HPicUrl;
			} else {               //大分辨率横屏时钟
				return DATA.HPicUrl;
			}
		}
		else {                        //如果是竖屏
			if (_width < 1000) {      //小分辨率竖屏时钟
				return DATA.picUrl;
			} else {                  //大分辨率竖屏时钟
				return DATA.picUrl;
			}
		}
	}
}
