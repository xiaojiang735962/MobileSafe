package com.project.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KillProcessService extends Service {
    public KillProcessService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class LockScreenReceiver extends BroadcastReceiver{
        //锁屏广播接收者
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取进程管理器
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            //获取手机上所以正在运行的进程
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses){
                activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
            }
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //注册锁屏的广播接收者
        LockScreenReceiver receiver = new LockScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver , filter);

        //计时器
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //业务逻辑
            }
            //每个一秒调用一次TimerTask方法
        }, 1000 , 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}