package com.project.mobilesafe.utils;/*
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
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class SystemInfoUtils {

    //获取手机中进程的个数
    public static int getProcessCount(Context context) {
        //得到进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取当前手机上所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        //获取手机上一共有多少进程
        return runningAppProcesses.size();
    }

    //获取剩余内存
    public static long getAvailMem(Context context) {
        //得到进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取内存的基本信息
        activityManager.getMemoryInfo(memoryInfo);
        //获取剩余内存
        return memoryInfo.availMem;
    }

    //获取总共内存
    public static long getTotalMem(Context context) {
        //得到进程管理者
        //ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取总共内存(只有高版本有此变量,低版本需要从porc文件中读取)
        //return memoryInfo.totalMem;
        //匹配低版本的totalMem
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String readLine = reader.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : readLine.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
