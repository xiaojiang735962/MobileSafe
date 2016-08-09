package com.project.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by xiaojiang on 8/7/16.
 * 短信备份工具
 */
public class SmsUtils {
    //备份短信接口
    public interface BackUpSms{
        //备份之前调用
        public void before(int count);
        //备份时调用
        public void onBackUp(int progress);
    }
    public static boolean backUp(Context context, BackUpSms callback){
        //判断手机是否有sd卡
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //由于短信数据库不能读写,所以使用内容解析者获取短信内容
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            //type:1接收短信 2发送短信
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            int count = cursor.getCount();
            //设置进度条的最大值
            //progressDialog.setMax(count);
            callback.before(count);
            //初始化progress的值
            int progress = 0;
            try {
                //使用XmlSerializer往sd卡中写数据
                File file = new File(Environment.getExternalStorageDirectory(),"message.xml");
                FileOutputStream out = new FileOutputStream(file);
                XmlSerializer xmlSerializer = Xml.newSerializer();
                xmlSerializer.setOutput(out,"utf-8");
                xmlSerializer.startDocument("utf-8",true);
                xmlSerializer.startTag(null,"sms");
                //设置标签<sms>上的属性值(size)
                xmlSerializer.attribute(null,"size",String.valueOf(count));
                while(cursor.moveToNext()){
                    xmlSerializer.startTag(null,"address");
                    xmlSerializer.text(cursor.getString(0));
                    xmlSerializer.endTag(null,"address");

                    xmlSerializer.startTag(null,"date");
                    xmlSerializer.text(cursor.getString(1));
                    xmlSerializer.endTag(null,"date");

                    xmlSerializer.startTag(null,"type");
                    xmlSerializer.text(cursor.getString(2));
                    xmlSerializer.endTag(null,"type");

                    xmlSerializer.startTag(null,"body");
                    //使用Crypto工具类实现短信内容加密
                    //参数1：表示加密种子(密钥)自己定义
                    //参数2：表示要加密的内容
                    xmlSerializer.text(Crypto.encrypt("123",cursor.getString(3)));
                    xmlSerializer.endTag(null,"body");
                    //备份一个<sms>标签则为备份一条短信
                    progress ++;
                    //progressDialog.setProgress(progress);
                    callback.onBackUp(progress);
                    //每备份一条短信睡200毫秒
                    SystemClock.sleep(200);
                }

                xmlSerializer.endTag(null,"sms");
                xmlSerializer.endDocument();
                cursor.close();
                out.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
