package com.project.mobilesafe.receiver;

import com.project.mobilesafe.R;
import com.project.mobilesafe.service.LocationService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

//短信拦截，获取GPS定位，播放报警音乐，远程清除数据(恢复出厂设置)
//远程锁屏并设置密码
public class SMSReceiver extends BroadcastReceiver {

	private SharedPreferences mPref;
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// 获取设备策略服务
		mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		// 设备管理组件
		mDeviceAdminSample = new ComponentName(context,
				AdminReceiver.class);
		
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		//短信超过70个字会分割短信，所以用for循环获取
		for (Object object : objects) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
			// 获取短信来电号码
			String originatingAddress = message.getOriginatingAddress();
			// 获取短信内容
			String messageBody = message.getMessageBody();
			if ("#*alarm*#".equals(messageBody)) {
				// 播放报警音乐，即使手机调为静音，也能播放，因为使用的是媒体声音系统
				MediaPlayer player = MediaPlayer.create(context, R.raw.because);
				player.setVolume(1f, 1f);// 音量大小
				player.setLooping(true);// 循环播放
				player.start();
				// 中断短信传递，从而使系统收不到短信
				abortBroadcast();
			} else if ("#*location*#".equals(messageBody)) {
				// 获取经纬度坐标
				context.startService(new Intent(context, LocationService.class));
				mPref = context.getSharedPreferences("config",
						context.MODE_PRIVATE);
				String location = mPref.getString("location",
						"getting location ...");
				// System.out.println("location:" + location);
				String phone = mPref.getString("safe_phone", "");
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(phone, null, location, null, null);
				// 中断短信传递，从而使系统收不到短信
				abortBroadcast();
			} else if ("#*wipedata*#".equals(messageBody)) {
				//远程清除数据，恢复出厂设置
				// 判断是否已经激活设备管理器
				if (mDPM.isAdminActive(mDeviceAdminSample)) {
					mDPM.wipeData(0);// 清除数据，恢复出厂设置
				} 
				// 中断短信传递，从而使系统收不到短信
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(messageBody)) {
				//远程锁屏，并设置密码
				// 判断是否已经激活设备管理器
				if (mDPM.isAdminActive(mDeviceAdminSample)) {
					// 锁屏
					mDPM.lockNow();
					// 设置开机密码
					mDPM.resetPassword("123456", 0);
				// 中断短信传递，从而使系统收不到短信
				abortBroadcast();
				}
			}
		}
	}

}
