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
	 * ��פ��ǰ�����ID
	 */
	private final static int NOTIFICATION=1;
	/**
	 * �����µ�ͼƬ����ʱ��������ϢID
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
	 * ���յ���ͼƬʱҪ���ŵ������ļ���URI;
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
				
				Log.i("SocketServer","��������������"+String.valueOf(socket.isConnected()));
				if (!socket.isConnected()) {
					try {
						isConnected=false;
						Toast.makeText(getApplicationContext(), "������Ͽ����ӣ����������С�����", Toast.LENGTH_LONG).show();
						Log.i("SocketServer","�Ѷ��ߣ���������");					
						socketConnect();
					} catch (Exception e) {					
						e.printStackTrace();
					}
				}
				else {
					if (!isConnected) {
						isConnected=true;
						Toast.makeText(getApplicationContext(), "����������������", Toast.LENGTH_LONG).show();
					}
					onConnect();
				//	Log.i("SocketServer","��������������"+String.valueOf(socket.isConnected()));
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

		//���ܼҾӰ涨�ƹ��ܣ�����
		if (MPFApp.getInstance().getVersionType() == DATA.VERSIONTYPE.SmartHome)
		{
			//ȥ��Ĭ�ϵ�����
			keyguardManager=(KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
			keyguardLock=keyguardManager.newKeyguardLock("");
			keyguardLock.disableKeyguard();

			//�Զ�������Reciever,���ܼҾӰ汾����
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
		Log.i("SocketServer","΢����̨�����ѱ�����");
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
		Log.i("SocketServer", "�Ѷ��ߣ�onDisconnect()�ص�����");
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
		if(event.equals(DATA.eventData[1]))//ɾ��ͼƬ
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
		else if(event.equals(DATA.eventData[2]))//�����û���Ϣ
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
		else if(event.equals(DATA.eventData[0]))//������չ���������Ƭ
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
					//�������û�����ӽ�ȥ
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
					 * ����û��ѱ������ˣ������յ�����û�����������ͼƬʱ����������ͽ��գ�������ʾ;
					 */
					Log.i(TAG,"�յ��������û���OpenIdΪ��"+user.getString("OpenId")+" ��������ͼƬ����������ͽ��գ�������ʾ��������");
					return ;  
				}
				String message="�յ�����"+user.getString("Name")+"����ͼƬ";
				MpfPicture mPicture = new MpfPicture();
				mPicture.setAddTime(now);
				mPicture.setDbId(picture.getInt("ID"));
				mPicture.setOpenId(picture.getString("OpenId"));
				mPicture.setPicUrl(picture.getString("ImgUrl"));
				if (!picture.isNull("MediaId")) {
					mPicture.setMediaId(picture.getString("MediaId"));
				}
				picService.AddMpfPicture(mPicture);
				//�㲥֪ͨ����
				bundle.putString(DATA.bundleKey[0], mPicture.getPicUrl());
				bundle.putString(DATA.bundleKey[1], mPicture.getOpenId());
				sendBroadcast(DATA.BroadcastMainActionName,bundle);  //���͵�����ͼƬ�Ĺ㲥�����ߣ��Կ�ʼ����ͼƬ
				sendBroadcast("com.qqonline.broadcast.PicturePostReciever",bundle);  //���͵�PostͼƬ���ݵĹ㲥�����ߣ�Post��ͼƬ��Ϣ������������
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
		else if(event.equals("regResult")) //��������˳����°���
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
	 * ����Notification֪ͨ
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
	 * ������Ϣ���ѵı���
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
	 * ��������Ϣ���ѵ������ļ�Uri
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
         * ��ͨ���͹㲥
         * @param BroadcastName
         * @param bundle
         */
	public void sendBroadcast(String BroadcastName,Bundle bundle)
	{
		//�㲥֪ͨ����
		Intent intent = new Intent(); 
		intent.setAction(BroadcastName);
		intent.putExtras(bundle);
		sendBroadcast(intent);//��ͨ����
	}
	public void emit(String event,JSONObject json) throws JSONException{
		socket.emit(event, json);
	}
}
