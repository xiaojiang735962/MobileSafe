package com.project.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	//检测系统服务是否正在运行
	public static boolean isServiceRunning(Context ctx,String serviceName){
		ActivityManager mManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统所以正在运行的服务，最多返回100个
		List<RunningServiceInfo> runningServices = mManager.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//获取服务名称
			String className = runningServiceInfo.service.getClassName();
			if(className.equals(serviceName)){
				return true;
			}
		}
		return false;
	}
}
