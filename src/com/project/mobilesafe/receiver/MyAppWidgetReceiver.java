package com.project.mobilesafe.receiver;/*
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

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.project.mobilesafe.service.KillProcessWidgetService;

//广播的生命周期只有10秒钟，不能做耗时操作
public class MyAppWidgetReceiver extends AppWidgetProvider{
    //此方法第一次创建桌面小控件时调用
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context , KillProcessWidgetService.class);
        context.startService(intent);
    }
    //此方法删除所有桌面小控件时调用
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context , KillProcessWidgetService.class);
        context.stopService(intent);
    }
}
