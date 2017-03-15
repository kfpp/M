package com.qqonline.mpf;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qqonline.Manager.db.MpfMachineService;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.async.AsyncGet;
import com.qqonline.conmon.async.AsyncRegister;
import com.qqonline.mpf.baseActivity.BaseActivity;
/**
 * fengcheng.ye 2014-7-21 
 * 激活序列号操作Activity
 * 
 */
public class ActiActivity extends BaseActivity {
	public Button Send;
	private EditText edtBindCode;
	private EditText MachineSerialNumber;
	private EditText edtPhone;

	/**
	 * 标志界面里输入的绑定码在服务器数据库中是否有重复
	 */
	private boolean isBindCodeRepeat;
	public MpfMachineService mms = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        TAG="ActiActivity";
		super.onCreate(savedInstanceState);


        initData();
        initCtrols();

		
		checkNetWork(this);
		checkUpdate(this);
	}	
	private void initData() {
		isBindCodeRepeat=false;
        activity=this;
        mms = new MpfMachineService(ActiActivity.this, DATA.DATAVERSION);
        if(mms.MpfMachineIsHave())
        {
            SetResultBack(true);
        }
        else
        {
            setContentView(R.layout.acti_main);
        }

	}
	private void initCtrols() {
		Send =(Button) findViewById(R.id.btn_acti_OK);
		
		Send.setOnClickListener(new sendOnClickListenter(ActiActivity.this));
		edtBindCode=(EditText)findViewById(R.id.etBindCode1);
		MachineSerialNumber = (EditText) findViewById(R.id.MachineSerialNumber);
		edtPhone=(EditText)findViewById(R.id.edt_acti_phone);
		edtBindCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                final View view = v;
                if (!hasFocus) {
                    AsyncGet get = new AsyncGet(ActiActivity.this, new AsyncGet.MyDoPostBack() {
                        @Override
                        public void DoPostBack(String json) {
                            if (json.equals("true")) {

                                Toast.makeText(ActiActivity.this, getString(R.string.bind_code_repeat), Toast.LENGTH_LONG).show();
                                ((EditText) view).setError(getString(R.string.bind_code_repeat));
                                isBindCodeRepeat = true;
                            } else {
                                isBindCodeRepeat = false;
                            }
                        }
                    }, false);
                    get.execute(DATA.CHECKBINDCODE + ((EditText) view).getText().toString());
                }
            }
        });
		TextView tvVersion=(TextView)findViewById(R.id.tvVersion);
		PackageManager manager=getPackageManager();
		try {
			PackageInfo info=manager.getPackageInfo(getPackageName(),0);
			String version=info.versionName;
			tvVersion.setText(version);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			app.finishAllActivity();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void SetResultBack(boolean flag)
	{
		Intent data = new Intent();
		data.putExtra("successed", flag);
		setResult(201,data);
		finish();
	}
	public class sendOnClickListenter implements OnClickListener {
		public ActiActivity activity;
		public sendOnClickListenter(ActiActivity activity)
		{
			this.activity=activity;
		}
		@Override
		public void onClick(View arg0) {
			
			if (isBindCodeRepeat) {
				Toast.makeText(ActiActivity.this, getString(R.string.bind_code_repeat), Toast.LENGTH_SHORT).show();
				return;
			}
//			edtBindCode=(EditText)findViewById(R.id.etBindCode);
			String serialNumber=MachineSerialNumber.getText().toString().trim();
			String bindCode=edtBindCode.getText().toString().trim();
			String phone=edtPhone.getText().toString().trim();
			Log.i("DDD",serialNumber);
			if(serialNumber.equals("") || bindCode.equals(""))
			{
				Log.i("DDD", "444");
				Toast.makeText(getApplicationContext(), R.string.ActiActivity_SendBtn_Null_Alert, Toast.LENGTH_LONG).show();
			}
			else
			{
				Send.setEnabled(false);
				Toast.makeText(getApplicationContext(), R.string.ActiActivity_Activating, Toast.LENGTH_LONG).show();
				AsyncRegister register=new AsyncRegister(ActiActivity.this,serialNumber);
				register.execute(DATA.SetBindCode,"id,bindingpassword,phone",serialNumber+","+bindCode+","+phone);
			}
		}
	}
}

