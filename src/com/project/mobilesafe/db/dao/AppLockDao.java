package com.project.mobilesafe.db.dao;/*
       Created by xiaojiang on 8/11/16.
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
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class AppLockDao {
    private Context context;
    private AppLockOpenHelper helper;

    public AppLockDao(Context context){
        this.context = context;
        helper = new AppLockOpenHelper(context);
    }
    //添加程序锁
    public void add(String packageName){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename" , packageName);
        db.insert("info" , null , values);
        db.close();

        //自定义注册一个内容观察者
        context.getContentResolver().notifyChange(Uri.parse("content://com.project.mobilesafe.change") , null);
    }
    //删除程序锁
    public void delete(String packageName){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("info" , "packagename = ?" , new String[]{packageName});
        db.close();

        //自定义注册一个内容观察者
        context.getContentResolver().notifyChange(Uri.parse("content://com.project.mobilesafe.change") , null);
    }
    //根据包名查询程序锁
    public boolean find(String packageName){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", null, "packagename = ?", new String[]{packageName}, null, null, null);
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }
    //查询所以程序锁的包名
    public List<String> findAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("info", new String[]{"packagename"}, null, null, null, null, null);
        List<String> packnames = new ArrayList<String>();
        while(cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }
}
