package com.project.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Handler;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.project.mobilesafe.ITelephony;
import com.project.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class CallSafeService extends Service {

    private BlackNumberDao dao;
    private TelephonyManager mManager;

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

        //设置监听器实现电话拦截
        //获取系统的电话服务
        mManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        mManager.listen(listener , PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {//短信最多140字超出分几个
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();//获取短信来源号码
                String messageBody = message.getMessageBody();//获取短信内容
                String number = originatingAddress.replace("-", "").trim();
                //通过电话号码查询拦截模式
                String mode = dao.findNumber(number);
                if (mode.equals("1")) {
                    abortBroadcast();
                } else if (mode.equals("3")) {
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //电话状态改变时监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            /**
             * TelephonyManager.CALL_STATE_IDLE 电话闲置状态
             * TelephonyManager.CALL_STATE_RINGING  电话响铃状态
             * TelephonyManager.CALL_STATE_OFFHOOK  电话接听状态(挂载)
             */
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING :
                    String mode = dao.findNumber(incomingNumber);
                    if(mode.equals("1") || mode.equals("2")){

                        Uri uri = Uri.parse("content://call_log/calls");

                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber));

                        //电话拦截(挂断电话)
                        endCall();
                    }
                    break;
            }
        }
    }
    private class MyContentObserver extends ContentObserver {
        String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //当数据改变的时候调用的方法
        @Override
        public void onChange(boolean selfChange) {

            getContentResolver().unregisterContentObserver(this);

            deleteCallLog(incomingNumber);

            super.onChange(selfChange);
        }
    }
    //删掉电话号码
    private void deleteCallLog(String incomingNumber) {

        Uri uri = Uri.parse("content://call_log/calls");

        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});

    }
    //电话挂断
    private void endCall() {
        try {
            //通过反射机制(类加载器)加载ServiceManager类
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射机制拿到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
