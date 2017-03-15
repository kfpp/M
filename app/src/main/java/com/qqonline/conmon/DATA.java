package com.qqonline.conmon;

import java.io.File;
import java.net.URLEncoder;

import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;


public class DATA {
	/**
	 * 数据库版本号
	 */
	public static final int DATAVERSION=8;//
	public static  enum VERSIONTYPE{Topiserv,SmartHome}
	public static final String loadUserUrl = "http://";//下载用户
	public static final String loadPictureUrl = "http://";//下载未读图片
	/**
	 * 网络连接超时
	 */
	public static final int NETWORDCONNECTTIMEOUT=2000;
	/**
	 * 网络读取数据超时
	 */
	public static final int NETWORK_READ_TIME_OUT=60000;
	/**
	 * 上传到服务器用的视频缩略图的宽度
	 */
	public static final int VIDEO_THUMBNAILS_WIDTH_TO_SERVICE=400;
	/**
	 * 上传到服务器用的视频缩略图的高度
	 */
	public static final int VIDEO_THUMBNAILS_HEIGHT_TO_SERVICE=300;
	/**
	 * 照片播放的竖屏Url
	 */
	public static final String picUrl = "http://mpf.qq-online.net/Advertisement/simg";
	/**
	 * 照片播放的横屏Url
	 */
	public static final String HPicUrl = "http://mpf.qq-online.net/Advertisement/himg";
	/**
	 * 时钟的竖屏Url
	 */
	public static final String ClockUrl = "http://mpf.qq-online.net/Advertisement/bigclock";
	/**
	 * 时钟的横屏Url
	 */
	public static final String HClockUrl = "http://mpf.qq-online.net/Advertisement/bighclock";
	/**
	 * 检查绑定口令是否已被使命的接口，get
	 * true：已存在绑定口令
	 *	false：还没存在
	 */
	public static final String CHECKBINDCODE = "http://mpf.qq-online.net/ClientAPI/CheckBindingPassword?password=";
	/**
	 * 检查绑定口令是否已被使命的接口.post
	 * true：修改成功
	 *false：修改失败
	 */
	public static final String CHANGEBINDCODE = "http://mpf.qq-online.net/ClientAPI/EditBindingPassword/序列号?password=";
	/**
	 * 获取指定机器的绑定列表
	 */
	public static final String BINDLISTURL="http://mpf.qq-online.net/ClientAPI/GetBinding/" ;//+序列号
	/*
	 * 小分辨率时钟的竖屏URL
	 * */
	public static final String SmallClockUrl="http://mpf.qq-online.net/Advertisement/clock";
	/*
	 * 小分辨率时钟的恒屏URL
	 * */
	public static final String SmallHClockUrl="http://mpf.qq-online.net/Advertisement/hclock";
	/**
	 * 天气预报的Url
	 */
	public static final String WeatherUrl = "http://www.qq-online.net/Mpf/weather.html";
	
	/**
	 * 更新apk的域名或者IP地址
	 */
	public static final String server ="http://mpf.qq-online.net/";//
	
	/**
	 * 更新处理的页面
	 */
	public static  String updateUrl = "http://mpf.qq-online.net/ClientAPI/SoftUpdate/2";
	
	/**
	 * 广告是否播放请求地址
	 */
	public static final String adsIsPlayUrl = "http://mpf.qq-online.net/ClientAPI/IsAdsDisplay/ads";
	/**
	 * 广告是否播放请求地址
	 */
	public static final String adsIsPlayPicAcitiveUrl = "http://mpf.qq-online.net/ClientAPI/IsAdsDisplay/adstwo";
	/**
	 * 激活机器的地址:外网：http://mpf.qq-online.net/ClientAPI/Register/
	 *192.168.254.21:105
	 */
	public static final String checkUrl = "http://mpf.qq-online.net/ClientAPI/Register/";
	/**
	 * Android 客户端激活接口（带绑定码和手机号码）
	 */
	public static final String SetBindCode="http://mpf.qq-online.net/ClientAPI/Activation";
	/**
	* socketIoServer地址,外网：http://124.172.245.242:5000
	*/
	//public static final String socketIoServerUrl = "http://192.168.254.21:5000"; http://124.172.245.242:5001/
	public static final String socketIoServerUrl = "http://115.28.208.144:5000";
	/**
	 * 下载微视服务器上的视频所需要的token
	 */
	public static final String ACCESSTOKENURL = "http://115.28.208.144:5001/";
	/**
	 * 强制刷新服务端Token,主要用于当服务端Token由于不明原因，token作废时更新Token
	 */
	public static final String UPDATETOKENURL = "http://115.28.208.144:5001/updateToken/";
	/**
	* "sendPic","delCPic","updateCUser"
	*/
	public static final String[] eventData = {"sendPic","delCPic","updateCUser"};
	/**
	* sd卡存放照片的目录
	*/
	public static final String sdCardPicFile = "MPFCache";
	public static final String SDCARDVIDEOPATH = "ViedoCache";
	public static final String sdCardAdPicFile="MPFCache/Advertisement";
	public static final String advertisementName="ad.jpg";

	/**
	 * 更新绑定码的接收者
	 */
	public static final String BroadcastBindCodeName = "com.qqonline.BindCodeUpdateReciever";
	/**
	 * MainActivity的广播actionName
	 */
	public static final String BroadcastMainActionName = "com.qqonline.MPFReceiver";
	/**
	 * PicActivity的广播actionName
	 */
	public static final String BroadcastPicActionName = "com.qqonline.MPFPReceiver";
	/**
	 * PicClockActivity的广播actionName
	 */
	public static final String BroadcastPicClockActionName = "com.qqonline.MPFPicClockReceiver";
	/**
	 * PlayActivity的广播actionName
	 */
	public static final String BroadcastPlayActionName = "com.qqonline.MPFPlayeceiver";
	/**
	 * 广播Bundle的数值key
	 * {"PicUrl","OpenId","Name"};
	 */
	public static final String[] bundleKey = {"PicUrl","OpenId","Name","BindCode"};
	/**
	 * 没有图片没有新用户的名称
	 */
	public static final String NoNewUserName = "no_user_just_for_check";
	/**
	 * 中国天气网自动获取城市代码接口
	 */
//	public static final String CityCodeURL="http://61.4.185.48:81/g/";
	public static final String CityCodeURL="http://int.dpool.sina.com.cn/iplookup/iplookup.php";
	/**
	 * 中国天气网接口
	 */
	public static final String WeatherURL="http://m.weather.com.cn/atad/";
	/**
	 * 自动更新天气的时间间隔，单位：小时
	 */	
	public static final int AutoUpdataWeatherIntevalHours=3;
//	private static final String URL="http://wthrcdn.etouch.cn/WeatherApi?city="+URLEncoder.encode("广州");
//	private static final String URL1="http://wthrcdn.etouch.cn/WeatherApi?citykey=101280101";
	/**
	 * 根据IP自动定位并获取天气的URL（比中国天气网API稳定）
	 */
//	public static final String AutoWeatherByIP="http://api.36wu.com/Weather/GetMoreWeatherByIp?ip=%s1&format=json"; 
	public static final String AutoWeatherByIP="http://wthrcdn.etouch.cn/WeatherApi?citykey=%s1"; 
	/**
	 * 根据城市名获取天气的URL（比中国天气网API稳定）
	 */
//	public static final String WeatherByCityName="http://api.36wu.com/Weather/GetMoreWeather?district=%s1&format=json";
	public static final String WeatherByCityName="http://wthrcdn.etouch.cn/WeatherApi?city=%s1";
	/**
	 * 横屏广告下载地址
	 */
	public static final String HAdvertisementURL="http://mpf.qq-online.net/Advertisement/himg";
	/**
	 * 竖屏广告下载地址
	 */
	public static final String VAdvertisementURL="http://mpf.qq-online.net/Advertisement/simg";
	/**
	 * 一级城市列表拉取地址
	 */
	public static final String Level1CityURL="http://weather.com.cn/data/city3jdata/china.html";
	/**
	 * 二级城市列表拉取地址
	 */
	public static final String Level2CityURL="http://weather.com.cn/data/city3jdata/provshi/";//+编号+.html
	/**
	 * 三级城市列表拉取地址
	 */
	public static final String Level3CityURL="http://weather.com.cn/data/city3jdata/station/";//+编号+.html
	/**
	 * 小分辨率日历国历字体大小
	 */
	public static final int CalendarTextSize=12;
	/**
	 * 小分辨率日历农历字体大小
	 */
	public static final int CalendarLunarTextSize=9;
	/**
	 * 大分辨率日历农历字体大小
	 */
	public static final int BigCalendarLunarTextSize=14;
	/**
	 * 大分辨率日历国历字体大小
	 */
	public static final int BigCalendarTextSize=16;
	/**
	 * 当第一次启动时请求天气数据超过这个时间没有返回，则重新请求（单位：毫秒）
	 */
	public static final int PeriodWhenFirstStartUpdateWeatherFail=5000;
	/**
	 * 将收到的图片下载地址信息发送到服务器，让服务器下载这张图片。
	 */
	public static final String PicturePostUrl="http://mpf.qq-online.net/ClientAPI/PictureDownloadFromWeiChat";
	/**
	 * 获取背景音乐列表的接口地址
	 */
	public static final String MUSICLISTURL="http://mpf.qq-online.net/ClientAPI/GetAllMusic";
	/**
	 * 背景音乐缓存路径
	 */
	public static final String MUSICCACHEPATH=GetExtSDCardPath.getSDCardPath()+File.separator+"BkgMusic";
	public static final String VIDIODOWNLOADURL="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s1&media_id=%s2"; //token,mid
	public static final String VIDEOTYPE=".mp4";
	public static final String PICTURETYPE=".jpg";
	/**
	 * 上传视频缩略图的服务器地址
	 */
	public static final String THUMBNAILS_UPLOAD_ADDRESS="http://mpf.qq-online.net/ClientAPI/PictureUpload";
	/**
	 * 获取二维码Ticket所用的URL
	 */
	public static final String TICKETURL="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
	/**
	 * 根据Ticket获取二维码图片所用的URL
	 */
	public static final String QRCODEIMAGEURL="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
	public static final String URL_KUGO_PAD_VERSION="http://downmobile.kugou.com/upload/android_beta/KugouPlayerForPad_202_V1.2.0.apk";
	public static final String URL_WEICHAT_MEDIA_BY_ID="https://api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";
	public static final String URL_ERROR_REPORT = "http://mpf.qq-online.net/WxApi/ErrorReport";
}
