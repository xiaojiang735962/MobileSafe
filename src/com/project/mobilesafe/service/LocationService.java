package com.project.mobilesafe.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

	private LocationManager mManager;
	private MyLocationListener listener;
	private SharedPreferences mPref;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		mManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 获取所有位置提供者
		// List<String> allProviders = mManager.getAllProviders();
		// System.out.println(allProviders);
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);//是否允许付费，比如使用3G网络
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//获取最佳位置提供者
		String bestProvider = mManager.getBestProvider(criteria, true);
		listener = new MyLocationListener();
		// 参数1:表示位置提供者 参数2:表示最短更新时间　参数3:表示最短更新距离
		mManager.requestLocationUpdates(bestProvider, 0, 0,listener);
	}

	class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
//			System.out.println("get location");
			// 位置发生变化时调用
			double longitude = location.getLongitude();// 获取经度
			double latitude = location.getLatitude();// 获取纬度
			float accuracy = location.getAccuracy();// 获取精确度
			double altitude = location.getAltitude();// 获取海拔
			//将获取的经纬度信息保存到sharedPreferences中
			mPref.edit().putString("location", "longitude:" + longitude + ";\n latitude:" + latitude + ";\n accuracy:"
					+ accuracy + ";\n altitude:" + altitude).commit();
			stopSelf();//停止service
		}

		@Override
		public void onProviderDisabled(String provider) {
			// 用户手动关闭GPS
//			System.out.println("onProviderDisabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// 用户手动打开GPS
//			System.out.println("onProviderEnabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 位置提供者状态发生变化时
//			System.out.println("onStatusChanged");
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 当Activity销毁时，停止更新
		mManager.removeUpdates(listener);
	}
}
