package com.project.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
//监听手机开机重启
public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences mPref;

	@Override
	public void onReceive(Context context, Intent intent) {
			mPref = context.getSharedPreferences("config", context.MODE_PRIVATE);
			boolean protect = mPref.getBoolean("protect", false);
			//只有防盗保护开启下才进行开机启动SIM卡判断
			if(protect){
				String sim = mPref.getString("sim", null);
				if(!TextUtils.isEmpty(sim)){
					//获取当前的SIM序列号
					TelephonyManager mManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
					//String currentSIM = mManager.getSimSerialNumber() + "1";//(SIM卡切换时检测)
					String currentSIM = mManager.getSimSerialNumber();
					if(currentSIM.equals(sim)){
						System.out.println("SIM卡安全");
					}else{
						//System.out.println("SIM卡已更换，发送报警短信");
						String phone = mPref.getString("safe_phone", "");
						//发送短信给安全号码
						SmsManager smsManager = SmsManager.getDefault();
						smsManager.sendTextMessage(phone, null, "SIM card changed , please look it", null, null);
					}
				}
			}
	}

}
