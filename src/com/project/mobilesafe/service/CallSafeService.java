package com.project.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.project.mobilesafe.db.dao.BlackNumberDao;

public class CallSafeService extends Service {

    private BlackNumberDao dao;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        //初始化短信的广播
        InnerReceiver innerReceiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(innerReceiver, filter);
    }

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {//短信最多140字超出分几个
                SmsMessage message = SmsMessage.createFromPdu((byte[])object);
                String originatingAddress = message.getOriginatingAddress();//获取短信来源号码
                String messageBody = message.getMessageBody();//获取短信内容
                String number = originatingAddress.replace("-","").trim();
                //通过电话号码查询拦截模式
                String mode = dao.findNumber(number);
                if(mode.equals("1")){
                    abortBroadcast();
                }else if(mode.equals("3")){
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
