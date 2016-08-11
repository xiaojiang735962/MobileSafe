package com.project.mobilesafe.db.dao;/*
       Created by xiaojiang on 8/10/16.
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntivirusDao {
    //该路径必须为data/data目录的路径，否则SQLiteDatabase无法访问
    private static final String PATH = "data/data/com.project.mobilesafe/files/antivirus.db";

    //检测当前md5是否在数据库中
    public static String checkFileVirus(String md5){
        String desc = null ;
        // 获取数据库对象(只读)
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READONLY);
        //查询当前传过来的参数md5是否在病毒数据库中
        Cursor cursor = database.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
        if(cursor.moveToNext()){
            desc = cursor.getString(0);
        }
        cursor.close();
        database.close();
        return desc;
    }
    //添加病毒到病毒数据库
    public static void addVirus(String md5 , String desc){
        // 获取数据库对象
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("md5" , md5);
        values.put("type" , 6);
        values.put("name" , "Android.XXX");
        values.put("desc" , desc);
        database.insert("datable" , null ,values);
        database.close();
    }
}
