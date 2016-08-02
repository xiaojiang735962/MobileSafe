package com.project.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.project.mobilesafe.R;
import com.project.mobilesafe.db.dao.AddressDao;

public class AddressService extends Service {

	private TelephonyManager mManager;
	private MyListener listener;
	private OutCallReceiver receiver;
	private WindowManager mWM;
	private View addressView;
	private SharedPreferences mPref;

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
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
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
				if (mWM != null && addressView != null) {
					mWM.removeView(addressView);
				}
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
	// 需要权限:permission.SYSTEM_ALERT_WINDOW
	private int startX;
	private int startY;
	private WindowManager.LayoutParams params;
	private int winWidth;
	private int winHeight;
	public void showAddress(String text) {
		mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		//获取屏幕的宽度和高度
		winWidth = mWM.getDefaultDisplay().getWidth();
		winHeight = mWM.getDefaultDisplay().getHeight();
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;//电话窗口，用于电话交互,置于所以应用之上，标题栏之下
		params.gravity = Gravity.LEFT + Gravity.TOP;//设置偏移的中心点为左上角
		params.setTitle("Toast");

		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		params.x = lastX;
		params.y = lastY;
		addressView = View.inflate(this, R.layout.toast_address, null);
		int[] bgs = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		int style = mPref.getInt("address_style", 0);
		//根据存储的样式更新背景
		addressView.setBackgroundResource(bgs[style]);
		TextView tvAddress = (TextView) addressView
				.findViewById(R.id.tv_address);
		tvAddress.setText(text);
		mWM.addView(addressView, params);
		
		//给浮窗设置触摸事件(使可以拖动浮窗)
		addressView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();
					
					//计算偏移量
					int dx = endX - startX;
					int dy = endY - startY;
					
					params.x += dx;
					params.y += dy;
					//防止坐标偏离屏幕
					if(params.x < 0){
						params.x = 0;
					}
					if(params.y < 0){
						params.y = 0;
					}
					if(params.x > winWidth-addressView.getWidth()){
						params.x = winWidth-addressView.getWidth();
					}
					if(params.y > winHeight-addressView.getHeight()){
						params.y = winHeight-addressView.getHeight();
					}
					//更新View的位置
					mWM.updateViewLayout(addressView, params);
					//重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					//记录坐标位置
					mPref.edit().putInt("lastX", params.x).commit();
					mPref.edit().putInt("lastY", params.y).commit();
					break;
				}
				return true;
			}
		});
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
