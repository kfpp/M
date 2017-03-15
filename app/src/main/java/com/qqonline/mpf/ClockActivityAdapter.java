package com.qqonline.mpf;

import com.qqonline.conmon.DATA;

import android.view.Display;
import android.webkit.WebView;
/**
 * ���շֱ����Զ����ضԾ͵�ʱ�ӽ���͹����
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
		if(_isLandscape)           //����Ǻ���
		{
			if (_width <1000) {    //С�ֱ��ʺ���ʱ��
				return DATA.SmallHClockUrl;
			} else {               //��ֱ��ʺ���ʱ��
				return DATA.HClockUrl;
			}
		}
		else {                        //���������
			if (_width < 1000) {      //С�ֱ�������ʱ��
				return DATA.SmallClockUrl;
			} else {                  //��ֱ�������ʱ��
				return DATA.ClockUrl;
			}
		}
		//return "";
	}
	private String GetAdsURL()
	{
		if(_isLandscape)           //����Ǻ���
		{
			if (_width <1000) {    //С�ֱ��ʺ���ʱ��
				return DATA.HPicUrl;
			} else {               //��ֱ��ʺ���ʱ��
				return DATA.HPicUrl;
			}
		}
		else {                        //���������
			if (_width < 1000) {      //С�ֱ�������ʱ��
				return DATA.picUrl;
			} else {                  //��ֱ�������ʱ��
				return DATA.picUrl;
			}
		}
	}
}
