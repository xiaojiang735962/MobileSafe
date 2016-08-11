package com.project.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.project.mobilesafe.R;
import com.project.mobilesafe.receiver.MyAppWidgetReceiver;
import com.project.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

public class KillProcessWidgetService extends Service {

    private AppWidgetManager appWidgetManager;

    public KillProcessWidgetService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //实现每隔5秒更新一次桌面小控件
    @Override
    public void onCreate() {
        super.onCreate();
        //获取桌面小控件管理者
        appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        //初始化一个定时器
        Timer timer = new Timer();
        //初始化一个定时任务
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //System.out.println("更新桌面小控件数据");
                //把当前的布局文件添加进来
                RemoteViews views = new RemoteViews(getPackageName() , R.layout.process_widget);
                //远程View不能使用findViewById拿取资源
                int processCount = SystemInfoUtils.getProcessCount(getApplicationContext());
                views.setTextViewText(R.id.process_count ,"正在运行的软件:" + String.valueOf(processCount));

                long availMem = SystemInfoUtils.getAvailMem(getApplicationContext());
                views.setTextViewText(R.id.process_memory , "可用内存:" + Formatter.formatFileSize(getApplicationContext() , availMem));

                //设置点击事件
                Intent intent = new Intent();
                intent.setAction("com.project.mobilesafe");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext() , 0 , intent ,0);
                views.setOnClickPendingIntent(R.id.btn_clear , pendingIntent);
                //第一个参数为上下文，第二个参数为要处理桌面小控件的广播
                ComponentName provider = new ComponentName(getApplicationContext() , MyAppWidgetReceiver.class);
                //更新桌面
                appWidgetManager.updateAppWidget(provider , views);
            }
        };
        //从0开始每隔5秒更新一次
        timer.schedule(timerTask , 0 , 5000);
    }
}
