package com.project.mobilesafe.engine;/*
       Created by xiaojiang on 8/8/16.
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

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.project.mobilesafe.R;
import com.project.mobilesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

public class TaskInfos {

    public static List<TaskInfo> getTaskInfos(Context context){
        PackageManager packageManager = context.getPackageManager();
        List<TaskInfo>  taskInfos = new ArrayList<TaskInfo>();
        //获取进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取手机上所有的运行进程
        List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses){
            TaskInfo taskInfo = new TaskInfo();
            //获取进程的名字(也就是包名)
            String processName = runningAppProcessInfo.processName;
            taskInfo.setPackageName(processName);
            //获取进程内存大小(此数组只有一位)
            MemoryInfo[] memoryInfo = activityManager
                    .getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;
            taskInfo.setMemorySize(totalPrivateDirty);
            //taskInfo.setMemorySize();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(processName,0);
                //获取该app的图标
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);
                //获取该app的名字
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                taskInfo.setAppName(appName);

                //获取安装应用程序的标记
                int flags = packageInfo.applicationInfo.flags;
                //判断是用户app还是系统app
                if((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                    //表示系统app
                    taskInfo.setUserApp(false);
                }else{
                    //表示用户app
                    taskInfo.setUserApp(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //核心库中有些系统进程没有图标,必须设置默认图标(否则报错)
                taskInfo.setAppName(processName);
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }
}
