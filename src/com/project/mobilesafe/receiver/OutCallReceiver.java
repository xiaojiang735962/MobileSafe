package com.project.mobilesafe.receiver;

import com.project.mobilesafe.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
//此类在AddressService中被重新实例化(在此无效)
public class OutCallReceiver extends BroadcastReceiver {
	//监听去电的广播接受者
	//需要权限:android.permission.PROCESS_OUTGOING_CALLS
	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();//获取去电电话号码
		String address = AddressDao.getAddress(number);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}
}
