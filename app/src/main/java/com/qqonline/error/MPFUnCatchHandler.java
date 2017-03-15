package com.qqonline.error;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qqonline.conmon.DATA;
import com.qqonline.conmon.HttpUtil;
import com.qqonline.conmon.MPFApp;
import com.qqonline.conmon.email.EmailSender;
import com.qqonline.mpf.MainActivity;
import com.qqonline.mpf.R;
import com.qqonline.Manager.db.MpfMachineService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;

public class MPFUnCatchHandler implements UncaughtExceptionHandler {

    private static final String TAG = "MPFUnCatchHandler";

    private MPFApp application;
    private UncaughtExceptionHandler exceptionHandler;

    public MPFUnCatchHandler(MPFApp application) {
        this.application = application;
        exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && exceptionHandler != null) {  //由代码显示捕获取的异常
            //如果用户没有处理则让系统默认的异常处理器来处理
            exceptionHandler.uncaughtException(thread, ex);
        } else {                                                  //没有由代码捕获到的异常
            //退出程序并重启
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    application.getApplicationContext(), 0, intent,
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            //退出程序
            AlarmManager mgr = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    restartIntent); // 1秒钟后重启应用
            application.finishAllActivity();
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 错误信息
     * @return true:处理了该异常；false:没有处理
     */
    private boolean handleException(Throwable ex) {
        final Throwable error = ex;
        if (error == null) {
            return false;
        }
        /**
         * 此处可以自定义末捕获异常出现后的处理，如显示提示、发送异常邮件等
         */
/*		new Thread(){
            @Override
			public void run() {
				Looper.prepare();
				MpfMachineService service=new MpfMachineService(application.getActivityList().get(0),DATA.DATAVERSION);
				String serializeNumber =service.getMpfMachine().getMachineSerialNumber();
				Toast.makeText(application.getApplicationContext(), R.string.uncatch_error_occured,   
                        Toast.LENGTH_SHORT).show();  							
			        EmailSender sender=new EmailSender();
			        sender.sendTo163("序列号为："+serializeNumber+" 的微视亲错误信息", getErrorInfo(error));
				error.printStackTrace();
				Looper.loop();
			};
		}.start();*/
        boolean hasNetwork = false;
        //判断是否有网络
        Context context = application.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
        if (infos != null && infos.length > 0) {
            for (NetworkInfo info : infos) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    hasNetwork = true;
                    break;
                }
            }
        }
        if (hasNetwork) return dealError(error);
        else return false;
    }

    private boolean dealError(Throwable error) {
        StringWriter sw = new StringWriter();
        error.printStackTrace(new PrintWriter(sw, true));
        try {
            if (application.getActivityList().size() > 0) {
            MpfMachineService service = new MpfMachineService(application.getActivityList().get(0), DATA.DATAVERSION);
            String serializeNumber = service.getMpfMachine().getMachineSerialNumber();
            String errStr = sw.toString();
            RequestParams params = new RequestParams();
            params.put("MechineId", serializeNumber);
            params.put("Error", errStr);
            HttpUtil.post(DATA.URL_ERROR_REPORT, params, new ErrorHttpHandler());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 从抛出错误信息中，包装出错信息并返回
     *
     * @param error 要包装出错信息的错误
     * @return 出错信息
     */
    private String getErrorInfo(Throwable error) {
        StringBuilder sb = new StringBuilder();
        sb.append(error.getLocalizedMessage() + "\n\r");
        StackTraceElement[] se = error.getStackTrace();
        for (int i = 0; i < se.length; i++) {
            sb.append(se[i].toString() + "\n\r");
        }
        return sb.toString();
    }

    /**
     * 上报错误后对返回值进行处理
     */
    public class ErrorHttpHandler extends TextHttpResponseHandler {
        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

        }

        @Override
        public void onSuccess(int i, Header[] headers, String s) {

        }
    }

}
