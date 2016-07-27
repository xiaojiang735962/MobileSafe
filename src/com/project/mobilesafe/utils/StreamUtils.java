package com.project.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
//将输入流读取成字符串返回
public class StreamUtils {
	
	public static String getStringFromStream(InputStream in) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int len = 0;
		byte[] buffer = new byte[1024];
		while((len = in.read(buffer)) != -1){
			out.write(buffer, 0, len);
		}
		String result = out.toString();
		in.close();
		out.close();
		return result;
	}
}
