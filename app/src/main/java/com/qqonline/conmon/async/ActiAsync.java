package com.qqonline.conmon.async;

import org.json.JSONException;
import org.json.JSONObject;

import com.qqonline.conmon.ByteUtil;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.net.PostByHttpClient;
import com.qqonline.domain.MpfMachine;
import com.qqonline.interfaces.IUrlPostable;
import com.qqonline.mpf.ActiActivity;
import com.qqonline.mpf.R;
import com.qqonline.Manager.db.MpfMachineService;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * extends AsyncTask<String, Integer, String> 
 * get提交到对应地址激活设备，onPostExecute 回传201ResultCode给主Activity
 * @author fengcheng.ye 2014-7-28
 *
 */
public class ActiAsync extends AsyncTask<String, Integer, Boolean>  {
	private static final String TAG="ActiAsync";
	private ActiActivity activity;
	public ActiAsync(ActiActivity activity)
	{
		this.activity = activity;
	}
	
	@Override
	protected Boolean doInBackground (String... url) {
		Log.i(TAG,"doInBackground");
		String jsonData = "";
		byte[] bytes=null;
		try {
			IUrlPostable post=new PostByHttpClient();
			bytes =  post.postUrlWithNoParams(url[0]);
			jsonData=ByteUtil.bytesToString(bytes, "utf-8");
			//jsonData= "{\"successed\":\"1\",\"data\":{\"DbId\":\"2\",\"BindingPassword\":\"144只猴子\",\"MachineCode\":\"qqonlineMPF001\",\"MachineSerialNumber\":\"xx22-113e-rfd3-ee44\"}}";
			JSONObject json;
			json = new JSONObject(jsonData);
			JSONObject content = json.getJSONObject("data");
			Log.i("DDD", "6");
			if(json.getInt("success")==0){
				MpfMachineService mms = new MpfMachineService(activity,DATA.DATAVERSION);
				MpfMachine machine = new MpfMachine();
				machine.setDbId(content.getInt("ID"));
				machine.setBindingPassword(content.getString("BindingPassword"));
				machine.setMachineCode(content.getString("MachineCode"));
				machine.setMachineSerialNumber(content.getString("MachineSerialNumber"));
				mms.addMpfMachine(machine);
				return true;
			}
			else
			{
			
				return false;
			} 
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		Log.i(TAG,"onPostExecute");
		if (!result) {
			Toast.makeText(activity, R.string.ActiActivity_ActivateFail, Toast.LENGTH_LONG).show();
		}
		Log.i("DDD", "onPostExecute");
		Intent data = new Intent();
		data.putExtra("successed", result);
		activity.setResult(201,data);
		activity.finish();
		//super.onPostExecute(result);
	}
}
