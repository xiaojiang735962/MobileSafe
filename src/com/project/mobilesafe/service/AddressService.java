package com.project.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.widget.TextView;

import com.project.mobilesafe.db.dao.AddressDao;

public class AddressService extends Service {

	private TelephonyManager mManager;
	private MyListener listener;
	private OutCallReceiver receiver;
	private WindowManager mWM;
	private TextView view;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		// 监听来电电话状态
		mManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 动态注册去电广播接收者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			// 电话响铃状态
			case TelephonyManager.CALL_STATE_RINGING:
				// 根据来电号码查询归属地
				String address = AddressDao.getAddress(incomingNumber);
				// Toast.makeText(AddressService.this, address ,
				// Toast.LENGTH_LONG).show();
				showAddress(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (mWM != null && view != null) {
					mWM.removeView(view);
				}
				break;
			default:
				break;
			}
		}
	}

	// 监听去电的广播接受者
	// 需要权限:android.permission.PROCESS_OUTGOING_CALLS
	public class OutCallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();// 获取去电电话号码
			String address = AddressDao.getAddress(number);
			showAddress(address);
		}
	}

	// 自定义归属地浮窗
	public void showAddress(String text) {
		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");

		view = new TextView(this);
		view.setText(text);
		view.setTextColor(Color.RED);
		mWM.addView(view, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 当Activity销毁时，停止来电监听
		mManager.listen(listener, PhoneStateListener.LISTEN_NONE);
		// 注销广播接收者
		unregisterReceiver(receiver);
	}
}
