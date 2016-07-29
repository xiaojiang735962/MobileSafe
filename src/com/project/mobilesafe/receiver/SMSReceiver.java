package com.project.mobilesafe.receiver;

import com.project.mobilesafe.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
//短信拦截，并播放报警音乐
public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		for (Object object : objects) {
			SmsMessage message = SmsMessage.createFromPdu((byte[])object);
			//获取短信来电号码
			String originatingAddress = message.getOriginatingAddress();
			//获取短信内容
			String messageBody = message.getMessageBody();
			if("#*alarm*#".equals(messageBody)){
				//播放报警音乐，即使手机调为静音，也能播放，因为使用的是媒体声音系统
				MediaPlayer player = MediaPlayer.create(context, R.raw.because);
				player.setVolume(1f, 1f);//音量大小
				player.setLooping(true);//循环播放
				player.start();
				//中断短信传递，从而使系统收不到短信
				abortBroadcast();
			}
		}
	}

}
