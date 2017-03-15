package com.qqonline.conmon.async;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.qqonline.conmon.DATA;
import com.qqonline.domain.MpfMachine;
import com.qqonline.mpf.ActiActivity;
import com.qqonline.Manager.db.MpfMachineService;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncRegister extends AsyncTask<String, Integer, String> {
	private ActiActivity _active;
	private String _serialNumber;
	public AsyncRegister(ActiActivity active,String serialNumber) {
		// TODO Auto-generated constructor stub
		_active=active;
		_serialNumber=serialNumber;
	}
	private void SetResultBack(boolean flag)
	{
		Intent data = new Intent();
		data.putExtra("successed", flag);
		_active.setResult(201,data);
		_active.finish();
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String result="";
		String url=params[0];
		String[] keys=params[1].split(",");
		String[] values=params[2].split(",");
		if (keys.length != values.length) {
			return "参数与值个数不对应";
		}
		HttpPost httpRequest=new HttpPost(url);
		List<NameValuePair> listParams=new ArrayList<NameValuePair>();
		for (int i = 0; i < keys.length; i++) {
			listParams.add(new BasicNameValuePair(keys[i], values[i]));
		}
		try {
			HttpEntity entity=new UrlEncodedFormEntity(listParams, HTTP.UTF_8);
			httpRequest.setEntity(entity);
			HttpResponse response=new DefaultHttpClient().execute(httpRequest);
			if (response.getStatusLine().getStatusCode()== 200) {
				result=EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			// TODO: handle exception
			result=e.getMessage();
		}
		return result;
	}
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		JSONObject json=null,data=null;
		String value=null;
		int success=-1;		
		_active.Send.setEnabled(true);
		try {
			json=new JSONObject(result);
			value=json.getString("value");
			success=json.getInt("success");
			data=json.getJSONObject("data");
		} catch (Exception e) {
			Log.w("mpf",result);
			e.printStackTrace();
		}
		if (json==null || value ==null || success== -1) {
			Toast.makeText(_active, "注册失败，网络超时或其它网络原因", Toast.LENGTH_SHORT).show();
			//SetResultBack(false);
			return;
		}
		if (value.equals("false") || success== 1) {
			Toast.makeText(_active, "注册失败，绑定码不存在或已被激活", Toast.LENGTH_SHORT).show();
			//SetResultBack(false);
			return;
		}
		if (success==0 && value.equals("true") ) {
			try {
				
				MpfMachineService mms = new MpfMachineService(_active,DATA.DATAVERSION);
				MpfMachine machine = new MpfMachine();
				machine.setDbId(data.getInt("ID"));
				machine.setBindingPassword(data.getString("BindingPassword"));
				machine.setMachineCode(data.getString("MachineCode"));
				machine.setMachineSerialNumber(data.getString("MachineSerialNumber"));
				mms.addMpfMachine(machine);
				Toast.makeText(_active, "注册成功，绑定码为："+machine.getBindingPassword()+",绑定信息可以到设置界面查看", Toast.LENGTH_LONG).show();
				SetResultBack(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//ActiAsync actiAsync = new ActiAsync((ActiActivity)_active);
			//	actiAsync.execute(DATA.checkUrl+_serialNumber);
		}
		super.onPostExecute(result);
	}
}
