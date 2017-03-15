package com.qqonline.conmon;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ByteUtil {

	/**
	 * Ä¬ÈÏ×ªutf-8
	 * @param bytes
	 * @return
	 */
	public static String bytesToString(byte[] bytes)
	{
		return bytesToString(bytes, "utf-8");
	}
	public static String bytesToString(byte[] bytes,String charset)
	{
		if (bytes == null) {
			return null;
		}
		try {
			return new String(bytes,charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static byte[] inputStreamToByteArray(InputStream is)
	{
//		String text=null;
//		try {
//			BufferedReader reader=new BufferedReader(new InputStreamReader(is,"gbk"));
//			for (String temp=reader.readLine();temp!=null;text += temp,temp=reader.readLine());
//		} catch (Exception e) {
//			Log.e("inputStreamToByteArray", "reader");
//			e.printStackTrace();
//		}

		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		byte[] buff=new byte[1024];
		int len=-1;
		try {
			while ((len = is.read(buff)) != -1) {
				baos.write(buff, 0, len);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return baos.toByteArray();
//		return text.getBytes();
	}
}
