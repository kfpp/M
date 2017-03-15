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
    private static final int TIME_OUT = 10*10000000; //��ʱʱ��   
    private static final String CHARSET = "utf-8"; //���ñ���   
    public static final String SUCCESS="1"; 
    public static final String FAILURE="0";   
	/**
	 * �ϴ�ͼƬ������ͼ�����ṩ΢�Ŷ˵Ĺ������ʹ��
	 * @param requestUrl ��������ַ
	 * @param file Ҫ�ϴ����ļ�
	 * @param openId ������ͼƬ������΢�źŵ�OpenId
	 * @param machineId ����ͼƬ���Ĳ������ϵ�ͼƬ
	 * @return
	 */
	public static String uploadImg(String requestUrl,File file,String openId,String machineId)
	{
		   String BOUNDARY = UUID.randomUUID().toString(); //�߽��ʶ ������� 
		   String PREFIX = "--" , LINE_END = "\r\n";   
	         String CONTENT_TYPE = "multipart/form-data"; //��������   
	         try {  
	             URL url = new URL(requestUrl);   
	             HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
	             conn.setReadTimeout(TIME_OUT); 
	             conn.setConnectTimeout(TIME_OUT); 
	             conn.setDoInput(true); //����������  
	             conn.setDoOutput(true); //���������  
	             conn.setUseCaches(false); //������ʹ�û���   
	             conn.setRequestMethod("POST"); //����ʽ   
	             conn.setRequestProperty("Charset", CHARSET);   
	             //���ñ���   
	             conn.setRequestProperty("connection", "keep-alive");   
	             conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);  
	             if(file!=null) {   
	                 /** * ���ļ���Ϊ�գ����ļ���װ�����ϴ� */  
	                 OutputStream outputSteam=conn.getOutputStream();   
	                 DataOutputStream dos = new DataOutputStream(outputSteam);   
	                 StringBuffer sb = new StringBuffer();   
	                 sb.append(PREFIX);   
	                 sb.append(BOUNDARY); sb.append(LINE_END);   
	                 /**  
	                 * �����ص�ע�⣺  
	                 * name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ�  
	                 * filename���ļ������֣�������׺���� ����:abc.png  
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
	                 * ��ȡ��Ӧ�� 200=�ɹ�  
	                 * ����Ӧ�ɹ�����ȡ��Ӧ����  
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
	 * �ϴ�ͼƬ������ͼ�����ṩ΢�Ŷ˵Ĺ������ʹ��
	 * @param requestUrl ��������ַ
	 * @param bitmap Ҫ�ϴ���ͼ��
	 * @param openId ������ͼƬ������΢�źŵ�OpenId
	 * @param machineId ����ͼƬ���Ĳ������ϵ�ͼƬ
	 * @param fileName �ڷ���������Ϊ�ĸ��ļ���
	 * @return
	 */
	public static String uploadImg(String requestUrl,Bitmap bitmap,String openId,String machineId,String fileName)
	{
		   String BOUNDARY = UUID.randomUUID().toString(); //�߽��ʶ ������� 
		   String PREFIX = "--" , LINE_END = "\r\n";   
	         String CONTENT_TYPE = "multipart/form-data"; //��������   
	         try {  
	             URL url = new URL(requestUrl);   
	             HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
	             conn.setReadTimeout(TIME_OUT); 
	             conn.setConnectTimeout(TIME_OUT); 
	             conn.setDoInput(true); //����������  
	             conn.setDoOutput(true); //���������  
	             conn.setUseCaches(false); //������ʹ�û���   
	             conn.setRequestMethod("POST"); //����ʽ   
	             conn.setRequestProperty("Charset", CHARSET);   
	             //���ñ���   
	             conn.setRequestProperty("connection", "keep-alive");   
	             conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);  
	             if(bitmap!=null) {   
	                 /** * ���ļ���Ϊ�գ����ļ���װ�����ϴ� */  
	                 OutputStream outputSteam=conn.getOutputStream();   
	                 DataOutputStream dos = new DataOutputStream(outputSteam);   
	                 StringBuffer sb = new StringBuffer();   
	                 sb.append(PREFIX);   
	                 sb.append(BOUNDARY); sb.append(LINE_END);   
	                 /**  
	                 * �����ص�ע�⣺  
	                 * name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ�  
	                 * filename���ļ������֣�������׺���� ����:abc.png  
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
	                 * ��ȡ��Ӧ�� 200=�ɹ�  
	                 * ����Ӧ�ɹ�����ȡ��Ӧ����  
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
	 * ��������ת��Ϊ�ַ���
	 * @param is ������
	 * @return �ַ���
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
	 * �ϴ�ͼƬ��ʱ����Post������Ӳ���
	 * @param dos ����Post���ݵ������
	 * @param name Ҫ��ӵĲ�����
	 * @param date Ҫ��ӵ�����
	 * @param PREFIX --
	 * @param BOUNDARY �����
	 * @param LINE_END ����
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
