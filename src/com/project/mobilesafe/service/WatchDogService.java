package com.project.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.project.mobilesafe.activity.EnterPwdActivity;
import com.project.mobilesafe.db.dao.AppLockDao;

import java.util.List;

public class WatchDogService extends Service {

    private ActivityManager activityManager;
    private AppLockDao appLockDao;
    private boolean flag = false;
    private WatchDogReceiver receiver;
    private List<String> appLockDaoAll;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    private String tempStopProtectPackageName;

    private class WatchDogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("com.itheima.mobileguard.stopprotect")){
                tempStopProtectPackageName = intent.getStringExtra("packageName");
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                tempStopProtectPackageName = null;
                flag = false;
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                if(flag == false){
                    startWatchDog();
                }
            }


        }

    }
    private class AppLockContentObserver extends ContentObserver{

        public AppLockContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            appLockDaoAll = appLockDao.findAll();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getContentResolver().registerContentObserver(Uri.parse("content://com.project.mobilesafe.change") , true ,
                new AppLockContentObserver(new Handler()));

        appLockDao = new AppLockDao(this);
        appLockDaoAll = appLockDao.findAll();
        //动态注册广播接收者
        receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.itheima.mobileguard.stopprotect");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
        //获取进程管理器
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        startWatchDog();
    }

    private void startWatchDog() {
        new Thread(){
            @Override
            public void run() {
                flag = true;
                while(flag){
                    //由于此服务一直在后台运行，避免主线程阻塞
                    //获取当前正在运行的任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
                    //获取最上面的进程
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //获取顶端应用程序包名
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    SystemClock.sleep(30);
                    if(appLockDaoAll.contains(packageName)) {
                        if (packageName.equals(tempStopProtectPackageName)) {

                        } else {
                            //System.out.println("在程序锁数据库中");
                            //此应用被设置程序锁(跳转锁屏界面)
                            Intent intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
                            //在服务中向activity跳转时，需要新建任务栈,否则不能跳转
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packageName", packageName);
                            startActivity(intent);
                        }
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
    }
}
