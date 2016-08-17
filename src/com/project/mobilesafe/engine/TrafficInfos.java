package com.project.mobilesafe.engine;/*
       Created by xiaojiang on 8/17/16.
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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;

import com.project.mobilesafe.bean.TrafficInfo;

import java.util.ArrayList;
import java.util.List;

public class TrafficInfos {
    public static List<TrafficInfo> getTrafficInfos(Context context){
        List<TrafficInfo> trafficInfos = new ArrayList<TrafficInfo>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages){
            TrafficInfo trafficInfo = new TrafficInfo();
            //获取应用程序图标
            Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
            trafficInfo.setIcon(icon);
            //获取app的名字
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            trafficInfo.setAppName(appName);
            //获取手机的下载流量
            long mobileRxBytes = TrafficStats.getMobileRxBytes();
            trafficInfo.setDownload(mobileRxBytes);
            //获取手机的上传流量
            long mobileTxBytes = TrafficStats.getMobileTxBytes();
            trafficInfo.setUpdate(mobileTxBytes);
            //获取手机消耗的总流量
            long total = mobileRxBytes + mobileTxBytes ;
            trafficInfo.setTotal(total);

            trafficInfos.add(trafficInfo);
        }
        return trafficInfos;
    }
}
