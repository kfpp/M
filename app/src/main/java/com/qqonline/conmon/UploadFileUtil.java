package com.qqonline.conmon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

public class UploadFileUtil {

	private static final String TAG = "uploadFile";   
    private static final int TIME_OUT = 10*10000000; //超时时间   
    private static final String CHARSET = "utf-8"; //设置编码   
    public static final String SUCCESS="1"; 
    public static final String FAILURE="0";   
	/**
	 * 上传图片的缩略图，以提供微信端的管理界面使用
	 * @param requestUrl 服务器地址
	 * @param file 要上传的文件
	 * @param openId 传过张图片过来的微信号的OpenId
	 * @param machineId 这张图片是哪部机器上的图片
	 * @return
	 */
	public static String uploadImg(String requestUrl,File file,String openId,String machineId)
	{
		   String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 
		   String PREFIX = "--" , LINE_END = "\r\n";   
	         String CONTENT_TYPE = "multipart/form-data"; //内容类型   
	         try {  
	             URL url = new URL(requestUrl);   
	             HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
	             conn.setReadTimeout(TIME_OUT); 
	             conn.setConnectTimeout(TIME_OUT); 
	             conn.setDoInput(true); //允许输入流  
	             conn.setDoOutput(true); //允许输出流  
	             conn.setUseCaches(false); //不允许使用缓存   
	             conn.setRequestMethod("POST"); //请求方式   
	             conn.setRequestProperty("Charset", CHARSET);   
	             //设置编码   
	             conn.setRequestProperty("connection", "keep-alive");   
	             conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);  
	             if(file!=null) {   
	                 /** * 当文件不为空，把文件包装并且上传 */  
	                 OutputStream outputSteam=conn.getOutputStream();   
	                 DataOutputStream dos = new DataOutputStream(outputSteam);   
	                 StringBuffer sb = new StringBuffer();   
	                 sb.append(PREFIX);   
	                 sb.append(BOUNDARY); sb.append(LINE_END);   
	                 /**  
	                 * 这里重点注意：  
	                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件  
	                 * filename是文件的名字，包含后缀名的 比如:abc.png  
	                 */   
	                 sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+file.getName()+"\""+LINE_END);  
	                 sb.append("Content-Type: image/jpeg;"+LINE_END);   
	                 sb.append(LINE_END);   
	                 dos.write(sb.toString().getBytes());   
	                 InputStream is = new FileInputStream(file);  
	                 byte[] bytes = new byte[1024];   
	                 int len = 0;   
	                 while((len=is.read(bytes))!=-1)   
	                 {   
	                    dos.write(bytes, 0, len);   
	                 }   
	                 is.close();   
	                 dos.write(LINE_END.getBytes());   
	                 
	                 addParams(dos, "machineId", machineId, PREFIX, BOUNDARY, LINE_END);
	                 addParams(dos, "openId", openId, PREFIX, BOUNDARY, LINE_END);
	                 
	                 byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();   
	                 dos.write(end_data);   
	                 dos.flush();  
	                 /**  
	                 * 获取响应码 200=成功  
	                 * 当响应成功，获取响应的流  
	                 */   
	                 int res = conn.getResponseCode();   
	                 Log.e(TAG, "response code:"+res);   
	                 if(res==200)   
	                 {  	                	 
	                 return "Sucess:"+getStringFromStream(conn.getInputStream());
	                 } 
	                 
	             }   
	         } catch (Exception e)   
	         { e.printStackTrace(); }   
	         return FAILURE;   
	     }  
	/**
	 * 上传图片的缩略图，以提供微信端的管理界面使用
	 * @param requestUrl 服务器地址
	 * @param bitmap 要上传的图像
	 * @param openId 传过张图片过来的微信号的OpenId
	 * @param machineId 这张图片是哪部机器上的图片
	 * @param fileName 在服务器保存为哪个文件名
	 * @return
	 */
	public static String uploadImg(String requestUrl,Bitmap bitmap,String openId,String machineId,String fileName)
	{
		   String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 
		   String PREFIX = "--" , LINE_END = "\r\n";   
	         String CONTENT_TYPE = "multipart/form-data"; //内容类型   
	         try {  
	             URL url = new URL(requestUrl);   
	             HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
	             conn.setReadTimeout(TIME_OUT); 
	             conn.setConnectTimeout(TIME_OUT); 
	             conn.setDoInput(true); //允许输入流  
	             conn.setDoOutput(true); //允许输出流  
	             conn.setUseCaches(false); //不允许使用缓存   
	             conn.setRequestMethod("POST"); //请求方式   
	             conn.setRequestProperty("Charset", CHARSET);   
	             //设置编码   
	             conn.setRequestProperty("connection", "keep-alive");   
	             conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);  
	             if(bitmap!=null) {   
	                 /** * 当文件不为空，把文件包装并且上传 */  
	                 OutputStream outputSteam=conn.getOutputStream();   
	                 DataOutputStream dos = new DataOutputStream(outputSteam);   
	                 StringBuffer sb = new StringBuffer();   
	                 sb.append(PREFIX);   
	                 sb.append(BOUNDARY); sb.append(LINE_END);   
	                 /**  
	                 * 这里重点注意：  
	                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件  
	                 * filename是文件的名字，包含后缀名的 比如:abc.png  
	                 */   
	                 sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""+fileName+"\""+LINE_END);  
	                 sb.append("Content-Type: image/jpeg;"+LINE_END);   
	                 sb.append(LINE_END);   
	                 dos.write(sb.toString().getBytes());   
	                 InputStream is = getInputStreamFromBitmap(bitmap);
	                 byte[] bytes = new byte[1024];   
	                 int len = 0;   
	                 while((len=is.read(bytes))!=-1)   
	                 {   
	                    dos.write(bytes, 0, len);   
	                 }   
	                 is.close();   
	                 dos.write(LINE_END.getBytes());   
	                 
	                 addParams(dos, "machineId", machineId, PREFIX, BOUNDARY, LINE_END);
	                 addParams(dos, "openId", openId, PREFIX, BOUNDARY, LINE_END);
	                 
	                 byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();   
	                 dos.write(end_data);   
	                 dos.flush();  
	                 /**  
	                 * 获取响应码 200=成功  
	                 * 当响应成功，获取响应的流  
	                 */   
	                 int res = conn.getResponseCode();   
	                 Log.e(TAG, "response code:"+res);   
	                 if(res==200)   
	                 {  	                	 
	                 return "Sucess:"+getStringFromStream(conn.getInputStream());
	                 } 
	                 
	             }   
	         } catch (Exception e)   
	         { e.printStackTrace(); }   
	         return FAILURE;   
	     }  
	private static InputStream getInputStreamFromBitmap(Bitmap bm)
	{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}
	/**
	 * 将输入流转化为字符串
	 * @param is 输入流
	 * @return 字符串
	 * @throws IOException
	 */
	private static String getStringFromStream(InputStream is) throws IOException
	{
		int len=-1;
		byte[] buff=new byte[1024];
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		while ((len=is.read(buff)) != -1) {
			os.write(buff, 0, len);		
		}
		is.close();
		return new String(os.toByteArray(), "utf-8");
	}
	/**
	 * 上传图片的时候，向Post内容添加参数
	 * @param dos 加载Post内容的输出流
	 * @param name 要添加的参数名
	 * @param date 要添加的数据
	 * @param PREFIX --
	 * @param BOUNDARY 随机数
	 * @param LINE_END 换行
	 * @throws IOException
	 */
	private static void addParams(DataOutputStream dos,String name,String date,String PREFIX,String BOUNDARY,String LINE_END) throws IOException
	{
		StringBuffer sb=new StringBuffer();
		sb.append(PREFIX);   
        sb.append(BOUNDARY); sb.append(LINE_END);  
        sb.append("Content-Disposition: form-data; name=\""+name+"\" "+LINE_END);  
        sb.append(LINE_END);
        sb.append(date);
        sb.append(LINE_END);
        dos.write(sb.toString().getBytes()); 
	}
}
