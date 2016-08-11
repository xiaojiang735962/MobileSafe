package com.project.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//MD5加密算法
public class MD5Utils {
	public static String encoding(String password){
		try {
			//获取MD5算法对象
			MessageDigest instance = MessageDigest.getInstance("MD5");
			//对字符串加密，返回字节数组
			byte[] digest = instance.digest(password.getBytes());
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;//获取字节的低八位的有效值
				String hexString = Integer.toHexString(i);//将整数转换为十六进制的字符
				if(hexString.length() < 2){
					hexString = "0" + hexString;//如果是１位，前面补０
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// 找不到此算法
			e.printStackTrace();
		}
		
		return "";
	}

	public static String getFileMd5(String sourceDir) {
		File file = new File(sourceDir);
		try {
			FileInputStream fis = new FileInputStream(file);
			//获取MD5算法对象
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			int len = 0 ;
			while ((len = fis.read(buffer)) != -1){
				messageDigest.update(buffer , 0 , len);
			}

			byte[] digest = messageDigest.digest();
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : digest){
				int i = b & 0xff;//获取字节的低八位的有效值
				String hexString = Integer.toHexString(i);//将整数转换为十六进制的字符
				if(hexString.length() < 2){
					hexString = "0" + hexString;//如果是１位，前面补０
				}
				stringBuffer.append(hexString);
			}
			return stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
