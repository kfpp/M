package com.qqonline.conmon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

import com.qqonline.conmon.sdcardutils.GetExtSDCardPath;

import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

public class GetUrlPicUri {
	private static boolean isPictureExistLocation=true;
//	public static boolean 
	/**
	 * 
	 * @param path
	 *            ͼƬ·��
	 * @return
	 * @throws Exception
	 */
	public static Uri getSdPicURI(String path) throws Exception {
		// File cache = new File(Environment.getExternalStorageDirectory(),
		// DATA.sdCardPicFile);
		File cache = new File(new File(GetExtSDCardPath.getSDCardPath()),
				DATA.sdCardPicFile);
		if (!cache.exists()) {
			cache.mkdirs();
		}
		;
		String filename = getMd5.Md5(path, 32)
				+ path.substring(path.lastIndexOf("."));
		File file = new File(cache, filename);
		if (file.exists()) {
			return Uri.fromFile(file);// Uri.fromFile(path)��������ܵõ��ļ���URI
		}
		return null;
	}

	/**
	 * �������ϻ�ȡͼƬ�����ͼƬ�ڱ��ش��ڵĻ���ֱ���ã������������ȥ������������ͼƬ �����path��ͼƬ�ĵ�ַ
	 */
	public static Uri getImageURI(String path, File cache) throws Exception {
		// String name = Base64.encodeToString(path.getBytes(),Base64.DEFAULT) +
		// path.substring(path.lastIndexOf("."));
		// SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss")
		// ;
		// String filename =
		// Base64.encodeToString(path.getBytes(),Base64.DEFAULT).substring(0,30)
		// + path.substring(path.lastIndexOf("."));
		String filename = getMd5.Md5(path, 32);
		File file = new File(cache, filename);
		Log.i("file", file.toString());
		// ���ͼƬ���ڱ��ػ���Ŀ¼����ȥ����������
		if (file.exists()) {
			return Uri.fromFile(file);// Uri.fromFile(path)��������ܵõ��ļ���URI
		} else {
			// Log.i("dp", "����ͼƬ");
			// Log.i("dp", path);
			// �������ϻ�ȡͼƬ file.mkdir();
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Log.i("dp", "yes");
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					// Log.i("dp", fos.toString());
				}
				is.close();
				fos.close();
				// Log.i("dp", "д��");
				// ����һ��URI����
				return Uri.fromFile(file);
			}
		}
		return null;
	}
	public static boolean isLocalFileExist(String path, File cache)
	{
		//String filename = getMd5.Md5(path, 32);
		String filename = path;
		File file = new File(cache, filename);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	/**
	 * �������ϻ�ȡͼƬ�����ͼƬ�ڱ��ش��ڵĻ���ֱ���ã������������ȥ������������ͼƬ �����path��ͼƬ�ĵ�ַ
	 */
	public static String getImageFile(String path, File cache) throws Exception {
		
		return getImageFile(path, cache, true);
	}
	public static String getImageFile(String path, File cache,boolean needMD5) throws Exception {
		// String name = Base64.encodeToString(path.getBytes(),Base64.DEFAULT) +
		// path.substring(path.lastIndexOf("."));
		// SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss")
		// ;
		// String filename =
		// Base64.encodeToString(path.getBytes(),Base64.DEFAULT).substring(0,30)
		// + path.substring(path.lastIndexOf("."));
		String filename=null;
		if (needMD5) {
			filename = getMd5.Md5(path, 32);
		}
		else {
			filename=path;
		}
		File file = new File(cache, filename);
		Log.i("file", file.toString());
		// ���ͼƬ���ڱ��ػ���Ŀ¼����ȥ����������
		String filePath=null;
		if (file.exists()) {
			try {
				filePath=file.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
			}			
			return filePath;// Uri.fromFile(path)��������ܵõ��ļ���URI
		} else {
			// Log.i("dp", "����ͼƬ");
			// Log.i("dp", path);
			// �������ϻ�ȡͼƬ file.mkdir();
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Log.i("dp", "yes");
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					// Log.i("dp", fos.toString());
				}
				is.close();
				fos.close();
				// Log.i("dp", "д��");
				// ����һ��URI����
				Log.i("file", file.toString());
				return file.getAbsolutePath();
			}
		}
		return null;
	}

	/**
	 * ��ȡ����ͼƬ���������Ѵ���ͼƬ��
	 * @param path  ͼƬURL
	 * @param cache ���ش洢·��
	 * @param isCover �Ƿ�ֱ�Ӹ����Ѵ����ļ�
	 * @return �ɹ�������ͼƬ����·����ʧ�ܣ�null
	 * @throws Exception
	 */
	public static String getImageFileAndCover(String path, File cache)
			throws Exception {
		
		String filename = getMd5.Md5(path, 32);
		File file = new File(cache, filename);
		if (file.exists()) {
			file.delete();
		}
		Log.i("file", file.toString());
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
			Log.i("file", file.toString());
			return file.getAbsolutePath();
		}

		return null;
	}

}
