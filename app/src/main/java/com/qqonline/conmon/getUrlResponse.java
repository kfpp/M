package com.qqonline.conmon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.util.Log;

/**
 * 本类已废弃,请使用IUrlGetable,IUrlPostable接口;
 * @author YE
 *
 */

@Deprecated
public class getUrlResponse {
	/**
	 * get
	 */
	private HttpURLConnection conn = null;
	private int getTimeout=0;
	/**
	 * 用于中断当前连接
	 */
	private  HttpClient currentGetClient;
	/**
	 * 用于中断当前连接
	 */
	private  HttpClient currentPostClient;
	public void stopCurrentPostClient()
	{
		Log.i("POST","stop");
		if (currentPostClient != null) {
			try {
				currentPostClient.getConnectionManager().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 中断当前连接
	 */
	public  void stopCurrentGetClient()
	{
		Log.i("GET","stop");
		if (currentGetClient != null) {
			try {
				currentGetClient.getConnectionManager().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(conn != null)
		{
			conn.disconnect();
		}
	}
	
	public  String getUtfString(String urlString) throws Exception{
		byte[] content=getByteArray(urlString);
		if (content!= null) {
			return new String(content,"utf-8");
		}
        return null;
    }
	public void setGetReadTimeout(int timeoutMillis)
	{
		if (timeoutMillis > 0) {
			getTimeout=timeoutMillis;
		}		
	}
	public  byte[] getByteArray(String urlString) throws Exception{		
        InputStream inputStream = null;
        byte[] buffer = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(DATA.NETWORDCONNECTTIMEOUT);
            if (getTimeout > 0) {
            	conn.setReadTimeout(getTimeout);
			}
//            
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");           
            if(conn.getResponseCode() == 200){
            	inputStream = conn.getInputStream();
                buffer = new byte[1024];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                while ((len = inputStream.read(buffer)) != -1)
                {
                    out.write(buffer, 0, len);
                }
                buffer = out.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("sjr","Network-error");
            return null;
        }
        finally{
            try {
                if(inputStream != null){
                    inputStream.close();
                }
                if(conn != null){
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("sjr","InvokeWebServiceHelper类中释放资源出错");
            }
        }
        return buffer;
    }	
	public String postUtfString(String url) throws Exception{
		return postUtfStringWithParams(url, null);
    }
	public String postUtfStringWithParams(String url,List<NameValuePair> params) throws Exception{
		Log.i("POST",url);
		currentPostClient = new DefaultHttpClient();  
		currentPostClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DATA.NETWORDCONNECTTIMEOUT);
		HttpPost post = new HttpPost(url);
		if (params != null) {
			UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params);
			post.setEntity(entity);
		}
        HttpResponse response = currentPostClient.execute(post); 
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
            HttpEntity entity = response.getEntity();  
            InputStream is = entity.getContent(); 
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            byte[] buf = new byte[1024];
            int length = -1;  
            while ((length = is.read(buf)) != -1) {  
                baos.write(buf, 0, length);
            }
            return new String(baos.toByteArray(),"utf-8");
        }
    	return null;
    }
	public String postUtfStringWithJsonParams(String url,String params) throws Exception{
		Log.i("POST",url);
		currentPostClient = new DefaultHttpClient();  
		currentPostClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		HttpPost post = new HttpPost(url);
		if (params != null) {
			HttpEntity entity=new StringEntity(params,"utf-8");
			post.setEntity(entity);
		}
        HttpResponse response = currentPostClient.execute(post); 
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
            HttpEntity entity = response.getEntity();  
            InputStream is = entity.getContent(); 
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            byte[] buf = new byte[1024];
            int length = -1;  
            while ((length = is.read(buf)) != -1) {  
                baos.write(buf, 0, length);
            }
            return new String(baos.toByteArray(),"utf-8");
        }
    	return null;
    }
}
