package com.qqonline.conmon;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class getMd5 {
	/**
	 * 返回Md5加密字符串
	 * 
	 * @param plainText
	 *            要加密的文字
	 * @param type
	 *            16,32位
	 */
	public static String Md5(String plainText, int type) {

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			if (type == 32) {
				return buf.toString()+".jpg";// 32位的加密
			} else if (type == 16) {
				return buf.toString().substring(8, 24)+".jpg";
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}

}
