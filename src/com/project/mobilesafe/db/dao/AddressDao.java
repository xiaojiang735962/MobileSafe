package com.project.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//归属地查询工具
public class AddressDao {
	//该路径必须为data/data目录的路径，否则SQLiteDatabase无法访问
	private static final String PATH = "data/data/com.project.mobilesafe/files/address.db";

	public static String getAddress(String number) {
		String address = "未知号码";
		// 获取数据库对象(只读)
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		
		//验证用户输入的内容是否是手机号码(使用正则表达式)
		if(number.matches("^1[3-8]\\d{9}$")){//匹配手机号码
			Cursor cursor = database.rawQuery("select location from data2 where id = " +
					"(select outkey from data1 where id = ?)",new String[]{number.substring(0, 7)});
			if(cursor.moveToNext()){
				address = cursor.getString(0);
			}
			cursor.close();
		}else if(number.matches("^\\d+$")){//匹配数字
			switch (number.length()) {
			case 3:
				if("110".equals(number)){
					address = "报警电话";
				}else if("120".equals(number)){
					address = "救援电话";
				}else if("119".equals(number)){
					address = "火警电话";
				}else if("114".equals(number)){
					address = "服务电话";
				}else{
					address = "紧急电话";
				}
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
			case 8:
				address = "本地电话";
				break;
			default:
				if(number.startsWith("0") && number.length() > 10){//可能为长途电话
					//查询区号为4位的电话
					Cursor cursor = database.rawQuery("select location from data2 where area = ?", 
							new String[]{number.substring(1, 4)});
					if(cursor.moveToNext()){
						address = cursor.getString(0);
					}else{
						cursor.close();
						//查询区号为3位的电话
						cursor = database.rawQuery("select location from data2 where area = ?", 
								new String[]{number.substring(1, 3)});
						if(cursor.moveToNext()){
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}
				break;
			}
		}
		database.close();//关闭数据库
		return address;
	}
}
