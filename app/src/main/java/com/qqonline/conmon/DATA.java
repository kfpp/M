package com.qqonline.conmon;

import java.io.File;
import java.net.URLEncoder;

import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;


public class DATA {
	/**
	 * ���ݿ�汾��
	 */
	public static final int DATAVERSION=8;//
	public static  enum VERSIONTYPE{Topiserv,SmartHome}
	public static final String loadUserUrl = "http://";//�����û�
	public static final String loadPictureUrl = "http://";//����δ��ͼƬ
	/**
	 * �������ӳ�ʱ
	 */
	public static final int NETWORDCONNECTTIMEOUT=2000;
	/**
	 * �����ȡ���ݳ�ʱ
	 */
	public static final int NETWORK_READ_TIME_OUT=60000;
	/**
	 * �ϴ����������õ���Ƶ����ͼ�Ŀ��
	 */
	public static final int VIDEO_THUMBNAILS_WIDTH_TO_SERVICE=400;
	/**
	 * �ϴ����������õ���Ƶ����ͼ�ĸ߶�
	 */
	public static final int VIDEO_THUMBNAILS_HEIGHT_TO_SERVICE=300;
	/**
	 * ��Ƭ���ŵ�����Url
	 */
	public static final String picUrl = "http://mpf.qq-online.net/Advertisement/simg";
	/**
	 * ��Ƭ���ŵĺ���Url
	 */
	public static final String HPicUrl = "http://mpf.qq-online.net/Advertisement/himg";
	/**
	 * ʱ�ӵ�����Url
	 */
	public static final String ClockUrl = "http://mpf.qq-online.net/Advertisement/bigclock";
	/**
	 * ʱ�ӵĺ���Url
	 */
	public static final String HClockUrl = "http://mpf.qq-online.net/Advertisement/bighclock";
	/**
	 * ���󶨿����Ƿ��ѱ�ʹ���Ľӿڣ�get
	 * true���Ѵ��ڰ󶨿���
	 *	false����û����
	 */
	public static final String CHECKBINDCODE = "http://mpf.qq-online.net/ClientAPI/CheckBindingPassword?password=";
	/**
	 * ���󶨿����Ƿ��ѱ�ʹ���Ľӿ�.post
	 * true���޸ĳɹ�
	 *false���޸�ʧ��
	 */
	public static final String CHANGEBINDCODE = "http://mpf.qq-online.net/ClientAPI/EditBindingPassword/���к�?password=";
	/**
	 * ��ȡָ�������İ��б�
	 */
	public static final String BINDLISTURL="http://mpf.qq-online.net/ClientAPI/GetBinding/" ;//+���к�
	/*
	 * С�ֱ���ʱ�ӵ�����URL
	 * */
	public static final String SmallClockUrl="http://mpf.qq-online.net/Advertisement/clock";
	/*
	 * С�ֱ���ʱ�ӵĺ���URL
	 * */
	public static final String SmallHClockUrl="http://mpf.qq-online.net/Advertisement/hclock";
	/**
	 * ����Ԥ����Url
	 */
	public static final String WeatherUrl = "http://www.qq-online.net/Mpf/weather.html";
	
	/**
	 * ����apk����������IP��ַ
	 */
	public static final String server ="http://mpf.qq-online.net/";//
	
	/**
	 * ���´����ҳ��
	 */
	public static  String updateUrl = "http://mpf.qq-online.net/ClientAPI/SoftUpdate/2";
	
	/**
	 * ����Ƿ񲥷������ַ
	 */
	public static final String adsIsPlayUrl = "http://mpf.qq-online.net/ClientAPI/IsAdsDisplay/ads";
	/**
	 * ����Ƿ񲥷������ַ
	 */
	public static final String adsIsPlayPicAcitiveUrl = "http://mpf.qq-online.net/ClientAPI/IsAdsDisplay/adstwo";
	/**
	 * ��������ĵ�ַ:������http://mpf.qq-online.net/ClientAPI/Register/
	 *192.168.254.21:105
	 */
	public static final String checkUrl = "http://mpf.qq-online.net/ClientAPI/Register/";
	/**
	 * Android �ͻ��˼���ӿڣ���������ֻ����룩
	 */
	public static final String SetBindCode="http://mpf.qq-online.net/ClientAPI/Activation";
	/**
	* socketIoServer��ַ,������http://124.172.245.242:5000
	*/
	//public static final String socketIoServerUrl = "http://192.168.254.21:5000"; http://124.172.245.242:5001/
	public static final String socketIoServerUrl = "http://115.28.208.144:5000";
	/**
	 * ����΢�ӷ������ϵ���Ƶ����Ҫ��token
	 */
	public static final String ACCESSTOKENURL = "http://115.28.208.144:5001/";
	/**
	 * ǿ��ˢ�·����Token,��Ҫ���ڵ������Token���ڲ���ԭ��token����ʱ����Token
	 */
	public static final String UPDATETOKENURL = "http://115.28.208.144:5001/updateToken/";
	/**
	* "sendPic","delCPic","updateCUser"
	*/
	public static final String[] eventData = {"sendPic","delCPic","updateCUser"};
	/**
	* sd�������Ƭ��Ŀ¼
	*/
	public static final String sdCardPicFile = "MPFCache";
	public static final String SDCARDVIDEOPATH = "ViedoCache";
	public static final String sdCardAdPicFile="MPFCache/Advertisement";
	public static final String advertisementName="ad.jpg";

	/**
	 * ���°���Ľ�����
	 */
	public static final String BroadcastBindCodeName = "com.qqonline.BindCodeUpdateReciever";
	/**
	 * MainActivity�Ĺ㲥actionName
	 */
	public static final String BroadcastMainActionName = "com.qqonline.MPFReceiver";
	/**
	 * PicActivity�Ĺ㲥actionName
	 */
	public static final String BroadcastPicActionName = "com.qqonline.MPFPReceiver";
	/**
	 * PicClockActivity�Ĺ㲥actionName
	 */
	public static final String BroadcastPicClockActionName = "com.qqonline.MPFPicClockReceiver";
	/**
	 * PlayActivity�Ĺ㲥actionName
	 */
	public static final String BroadcastPlayActionName = "com.qqonline.MPFPlayeceiver";
	/**
	 * �㲥Bundle����ֵkey
	 * {"PicUrl","OpenId","Name"};
	 */
	public static final String[] bundleKey = {"PicUrl","OpenId","Name","BindCode"};
	/**
	 * û��ͼƬû�����û�������
	 */
	public static final String NoNewUserName = "no_user_just_for_check";
	/**
	 * �й��������Զ���ȡ���д���ӿ�
	 */
//	public static final String CityCodeURL="http://61.4.185.48:81/g/";
	public static final String CityCodeURL="http://int.dpool.sina.com.cn/iplookup/iplookup.php";
	/**
	 * �й��������ӿ�
	 */
	public static final String WeatherURL="http://m.weather.com.cn/atad/";
	/**
	 * �Զ�����������ʱ��������λ��Сʱ
	 */	
	public static final int AutoUpdataWeatherIntevalHours=3;
//	private static final String URL="http://wthrcdn.etouch.cn/WeatherApi?city="+URLEncoder.encode("����");
//	private static final String URL1="http://wthrcdn.etouch.cn/WeatherApi?citykey=101280101";
	/**
	 * ����IP�Զ���λ����ȡ������URL�����й�������API�ȶ���
	 */
//	public static final String AutoWeatherByIP="http://api.36wu.com/Weather/GetMoreWeatherByIp?ip=%s1&format=json"; 
	public static final String AutoWeatherByIP="http://wthrcdn.etouch.cn/WeatherApi?citykey=%s1"; 
	/**
	 * ���ݳ�������ȡ������URL�����й�������API�ȶ���
	 */
//	public static final String WeatherByCityName="http://api.36wu.com/Weather/GetMoreWeather?district=%s1&format=json";
	public static final String WeatherByCityName="http://wthrcdn.etouch.cn/WeatherApi?city=%s1";
	/**
	 * ����������ص�ַ
	 */
	public static final String HAdvertisementURL="http://mpf.qq-online.net/Advertisement/himg";
	/**
	 * ����������ص�ַ
	 */
	public static final String VAdvertisementURL="http://mpf.qq-online.net/Advertisement/simg";
	/**
	 * һ�������б���ȡ��ַ
	 */
	public static final String Level1CityURL="http://weather.com.cn/data/city3jdata/china.html";
	/**
	 * ���������б���ȡ��ַ
	 */
	public static final String Level2CityURL="http://weather.com.cn/data/city3jdata/provshi/";//+���+.html
	/**
	 * ���������б���ȡ��ַ
	 */
	public static final String Level3CityURL="http://weather.com.cn/data/city3jdata/station/";//+���+.html
	/**
	 * С�ֱ����������������С
	 */
	public static final int CalendarTextSize=12;
	/**
	 * С�ֱ�������ũ�������С
	 */
	public static final int CalendarLunarTextSize=9;
	/**
	 * ��ֱ�������ũ�������С
	 */
	public static final int BigCalendarLunarTextSize=14;
	/**
	 * ��ֱ����������������С
	 */
	public static final int BigCalendarTextSize=16;
	/**
	 * ����һ������ʱ�����������ݳ������ʱ��û�з��أ����������󣨵�λ�����룩
	 */
	public static final int PeriodWhenFirstStartUpdateWeatherFail=5000;
	/**
	 * ���յ���ͼƬ���ص�ַ��Ϣ���͵����������÷�������������ͼƬ��
	 */
	public static final String PicturePostUrl="http://mpf.qq-online.net/ClientAPI/PictureDownloadFromWeiChat";
	/**
	 * ��ȡ���������б�Ľӿڵ�ַ
	 */
	public static final String MUSICLISTURL="http://mpf.qq-online.net/ClientAPI/GetAllMusic";
	/**
	 * �������ֻ���·��
	 */
	public static final String MUSICCACHEPATH=GetExtSDCardPath.getSDCardPath()+File.separator+"BkgMusic";
	public static final String VIDIODOWNLOADURL="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s1&media_id=%s2"; //token,mid
	public static final String VIDEOTYPE=".mp4";
	public static final String PICTURETYPE=".jpg";
	/**
	 * �ϴ���Ƶ����ͼ�ķ�������ַ
	 */
	public static final String THUMBNAILS_UPLOAD_ADDRESS="http://mpf.qq-online.net/ClientAPI/PictureUpload";
	/**
	 * ��ȡ��ά��Ticket���õ�URL
	 */
	public static final String TICKETURL="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
	/**
	 * ����Ticket��ȡ��ά��ͼƬ���õ�URL
	 */
	public static final String QRCODEIMAGEURL="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
	public static final String URL_KUGO_PAD_VERSION="http://downmobile.kugou.com/upload/android_beta/KugouPlayerForPad_202_V1.2.0.apk";
	public static final String URL_WEICHAT_MEDIA_BY_ID="https://api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";
	public static final String URL_ERROR_REPORT = "http://mpf.qq-online.net/WxApi/ErrorReport";
}
