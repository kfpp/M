package com.qqonline.mpf;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.qqonline.broadcast.ScreenOffReciever;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.getMd5;
import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;
import com.qqonline.domain.MpfMachine;
import com.qqonline.domain.MpfPicture;
import com.qqonline.domain.MpfUser;
import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.Manager.db.MpfPictureService;
import com.qqonline.Manager.db.MpfUserService;
import com.qqonline.Manager.db.ShieldListService;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class SocketIoClient extends Service implements IOCallback {
	private static final String TAG="SocketIoClient";
	private final static int RECONNECTTIME=1000 * 60 * 3;
	/**
	 * 常驻的前后服务ID
	 */
	private final static int NOTIFICATION=1;
	/**
	 * 当有新的图片进来时的提醒消息ID
	 */
	private final static int NOTIFICATION_NEW_PICTURE_IN=2;

	private enum NotificationType
	{
		Picture,
		Music
	}
	private WsBinder wb = new WsBinder();
	private MpfUserService userService = new MpfUserService(SocketIoClient.this, DATA.DATAVERSION);
	private MpfPictureService picService = new MpfPictureService(SocketIoClient.this, DATA.DATAVERSION);
	private MpfMachineService machineService = new MpfMachineService(SocketIoClient.this, DATA.DATAVERSION);
	private Handler handler;
	private Runnable heartBeat;
	public SocketIO socket;
	private KeyguardManager keyguardManager;
	private KeyguardManager.KeyguardLock keyguardLock;
	private ScreenOffReciever reciever;
	private NotificationManager notificationManager;
	private boolean isConnected=true;
	private List<String> shieldOpenIdList;
	private ShieldListService shieldService=new ShieldListService(SocketIoClient.this, DATA.DATAVERSION);
	/**
	 * 当收到新图片时要播放的声音文件的URI;
	 */
	private Uri pictureRecieveMusic;
	//private ActivityManager mActivityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);   
	@Override
	public IBinder onBind(Intent intent) {
		return wb;
	}
	public class WsBinder extends Binder{
        public SocketIoClient getService(){
            return SocketIoClient.this;
        }
	}
	
	@Override
	public void onCreate() {
		handler=new Handler();
		socket = new SocketIO();
		notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		pictureRecieveMusic=Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.audio);
		heartBeat=new Runnable() {			
			@Override
			public void run() {
				
				Log.i("SocketServer","网络连接心跳："+String.valueOf(socket.isConnected()));
				if (!socket.isConnected()) {
					try {
						isConnected=false;
						Toast.makeText(getApplicationContext(), "与网络断开连接，正在重连中。。。", Toast.LENGTH_LONG).show();
						Log.i("SocketServer","已断线，心跳重连");					
						socketConnect();
					} catch (Exception e) {					
						e.printStackTrace();
					}
				}
				else {
					if (!isConnected) {
						isConnected=true;
						Toast.makeText(getApplicationContext(), "已与网络重新连接", Toast.LENGTH_LONG).show();
					}
					onConnect();
				//	Log.i("SocketServer","网络连接心跳："+String.valueOf(socket.isConnected()));
				}
				handler.postDelayed(heartBeat, RECONNECTTIME);
			}
		};
		
		try {
			socketConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler.postDelayed(heartBeat,RECONNECTTIME );

		Notification.Builder builder=new Notification.Builder(this);
		PendingIntent intent=PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		builder.setAutoCancel(false);
		builder.setContentIntent(intent);
		builder.setContentTitle(getResources().getString(R.string.app_name));
		builder.setContentText(getResources().getString(R.string.show_test));
		builder.setWhen(System.currentTimeMillis());
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setOngoing(true);
		startForeground(NOTIFICATION, builder.build());

		//智能家居版定制功能，屏保
		if (MPFApp.getInstance().getVersionType() == DATA.VERSIONTYPE.SmartHome)
		{
			//去掉默认的屏保
			keyguardManager=(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
			keyguardLock=keyguardManager.newKeyguardLock("");
			keyguardLock.disableKeyguard();

			//自定义屏保Reciever,智能家居版本才用
			IntentFilter filter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
			reciever=new ScreenOffReciever();
			registerReceiver(reciever, filter);
		}
		super.onCreate();
	}

	
	public void socketConnect() throws Exception {
		try {		
			socket = new SocketIO();
			socket.connect(DATA.socketIoServerUrl,SocketIoClient.this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// Sends a string to the server.
		//socket.send("Hello Server");

		// Sends a JSON object to the server.
		//socket.send(new JSONObject().put("key", "value").put("key2","another value"));

		// Emits an event to the server.
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags=START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.i("SocketServer","微相框后台服务已被销毁");
	//	socket.disconnect();
		handler.removeCallbacks(heartBeat);
		super.onDestroy();
		
	}
	
	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		try {
			System.out.println("Server said:" + json.toString(2));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(String data, IOAcknowledge ack) {
		System.out.println("Server said: " + data);
	}

	@Override
	public void onError(SocketIOException socketIOException) {
		System.out.println("an Error occured"+socketIOException.getMessage());
		try {
			socketConnect();
		//	socket.reconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//socketIOException.printStackTrace();
	}

	@Override
	public void onDisconnect() {
		Log.i("SocketServer", "已断线，onDisconnect()回调重连");
		try {
		//	socketConnect();
		//	socket.reconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void reconnected()
	{
	//	if (socket.) {
			
		//}
	}
	@Override
	public void onConnect() {
		Log.i("DDD","Connection established");
		MpfMachine machine = machineService.getMpfMachine();
		String MachineSerialNumber = machine.getMachineSerialNumber();
		try {
			socket.emit("reg", MachineSerialNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		Log.i("SocketServer",event+ args[0].toString());
		Log.i("DDD", event);
		if(event.equals(DATA.eventData[1]))//删除图片
		{
			try {
				JSONObject json = new JSONObject(args[0].toString());
				JSONObject picture = json.getJSONObject("picture");
				picService.delMpfPicture(picture.getInt("ID"));
				String path = picture.getString("ImgUrl");
				String filename=getMd5.Md5(path,32);
//	            File cache = new File(Environment.getExternalStorageDirectory(), DATA.sdCardPicFile);
				File cache = new File(new File(GetExtSDCardPath.getSDCardPath()), DATA.sdCardPicFile);
				File file = new File(cache , filename);
				if(file.exists()){
					file.delete();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(event.equals(DATA.eventData[2]))//更新用户信息
		{
			try {
				JSONObject json = new JSONObject(args[0].toString());
				JSONObject user = json.getJSONObject("user");
				userService.updatempfUser(user.getString("Name"), user.getString("OpenId"));
				Bundle bundle = new Bundle();
				bundle.putString(DATA.bundleKey[0], "");
				bundle.putString(DATA.bundleKey[1], "");
				sendBroadcast(DATA.BroadcastMainActionName,bundle);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(event.equals(DATA.eventData[0]))//处理接收过来的新照片
		{
			try {
				Log.i("SocketIO",args[0].toString());
				JSONObject json = new JSONObject(args[0].toString());
				JSONObject user = json.getJSONObject("user");
				JSONObject picture = json.getJSONObject("picture");
				Bundle bundle = new Bundle();
				Date now = new Date();
				if(!userService.UserIsHaved(user.getString("OpenId")))
				{
					//不存在用户，添加进去
					MpfUser mUser = new MpfUser();
					mUser.setAddTime(now);
					mUser.setDbId(user.getInt("ID"));
					mUser.setOpenId(user.getString("OpenId"));
					mUser.setName(user.getString("Name"));
					bundle.putString(DATA.bundleKey[2], user.getString("Name"));
					userService.AddMpfUser(mUser);
					//MpfPicture mPicture = mapper.readValue(picture.toString(), MpfPicture.class);
				}
				else
				{
					MpfUser mpfUser=userService.GetUserByOpenId(user.getString("OpenId"));
					String newName=user.getString("Name");
					if(!newName.equals(mpfUser.getName()) )
					{
						userService.updatempfUser(newName, mpfUser.getOpenId());
					}
					bundle.putString(DATA.bundleKey[2], newName);
				}
				shieldOpenIdList=shieldService.getAllShiledOpenId();
				if (shieldOpenIdList != null && shieldOpenIdList.contains(user.getString("OpenId"))) {
					/**
					 * 这个用户已被屏蔽了，所以收到这个用户所发过来的图片时，不做保存和接收，包括提示;
					 */
					Log.i(TAG,"收到被屏蔽用户，OpenId为："+user.getString("OpenId")+" 发过来的图片，不做保存和接收，包括提示等任务处理");
					return ;  
				}
				String message="收到来自"+user.getString("Name")+"的新图片";
				MpfPicture mPicture = new MpfPicture();
				mPicture.setAddTime(now);
				mPicture.setDbId(picture.getInt("ID"));
				mPicture.setOpenId(picture.getString("OpenId"));
				mPicture.setPicUrl(picture.getString("ImgUrl"));
				if (!picture.isNull("MediaId")) {
					mPicture.setMediaId(picture.getString("MediaId"));
				}
				picService.AddMpfPicture(mPicture);
				//广播通知下载
				bundle.putString(DATA.bundleKey[0], mPicture.getPicUrl());
				bundle.putString(DATA.bundleKey[1], mPicture.getOpenId());
				sendBroadcast(DATA.BroadcastMainActionName,bundle);  //发送到下载图片的广播接收者，以开始下载图片
				sendBroadcast("com.qqonline.broadcast.PicturePostReciever",bundle);  //发送到Post图片数据的广播接收者，Post新图片信息给服务器下载
				//
				if (MPFApp.getInstance().getVersionType() == DATA.VERSIONTYPE.SmartHome)
				{
					showMessage(NotificationType.Picture,message);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if(event.equals("regResult")) //心跳包，顺便更新绑定码
		{
			try {
				JSONObject json = new JSONObject(args[0].toString());
				JSONObject data = json.getJSONObject("data");
				String newBindCode=data.getString("BindingPassword");
			//	userService.updatempfUser(data.getString("Name"), data.getString("OpenId"));
				Bundle bundle = new Bundle();
				bundle.putString(DATA.bundleKey[3], newBindCode);
				sendBroadcast(DATA.BroadcastBindCodeName,bundle);

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * 发送Notification通知
	 */
	private void showMessage(NotificationType type,String message)
	{

		Notification.Builder builder=new Notification.Builder(this);
		PendingIntent intent=PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		builder.setAutoCancel(true);
		builder.setContentIntent(intent);
		builder.setContentTitle(getTitle(type));
		builder.setTicker(message);
		builder.setContentText(message);
		builder.setWhen(System.currentTimeMillis());
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setSound(getSound(type));
		notificationManager.notify(NOTIFICATION_NEW_PICTURE_IN, builder.build());
	}
	/**
	 * 返回消息提醒的标题
	 * @param type
	 * @return
	 */
	private String getTitle(NotificationType type)
	{
		if (type == NotificationType.Picture) {
			return getResources().getString(R.string.new_picture_in);
		}
		else if (type == NotificationType.Music) {
			return getResources().getString(R.string.new_music_in);
		}
		return null;
	}
	/**
	 * 返回新消息提醒的声音文件Uri
	 * @param type
	 * @return
	 */
	private Uri getSound(NotificationType type) {
        if (type == NotificationType.Picture) {
            return pictureRecieveMusic;
        } else if (type == NotificationType.Music) {
            return null;
        }
        return null;
    }
		/**
         * 普通发送广播
         * @param BroadcastName
         * @param bundle
         */
	public void sendBroadcast(String BroadcastName,Bundle bundle)
	{
		//广播通知下载
		Intent intent = new Intent(); 
		intent.setAction(BroadcastName);
		intent.putExtras(bundle);
		sendBroadcast(intent);//普通发送
	}
	public void emit(String event,JSONObject json) throws JSONException{
		socket.emit(event, json);
	}
}
