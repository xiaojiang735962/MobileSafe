package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;
import com.project.mobilesafe.utils.SmsUtils;
import com.project.mobilesafe.utils.SmsUtils.BackUpSms;
import com.project.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;

public class AToolsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	//跳转到归属地查询界面
	public void numberAddressQuery(View v){
		startActivity(new Intent(this, AddressActivity.class));
	}
	//短信备份功能
	public void backUpSms(View v){
		//初始化一个进度条对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("提示");
		progressDialog.setMessage("正在备份短信,请稍后...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
		new Thread(){
			@Override
			public void run() {
				boolean backUp = SmsUtils.backUp(AToolsActivity.this, new BackUpSms() {
					@Override
					public void before(int count) {
						progressDialog.setMax(count);
					}

					@Override
					public void onBackUp(int progress) {
						progressDialog.setProgress(progress);
					}
				});
				if(backUp){
					//通过Looper在子线程刷新UI
					//Looper.prepare();
					ToastUtils.showToast(AToolsActivity.this,"备份成功");
					//Looper.loop();
				}else{
					//Looper.prepare();
					ToastUtils.showToast(AToolsActivity.this,"备份失败");
					//Looper.loop();
				}
				progressDialog.dismiss();
			}
		}.start();
	}
	//程序锁功能
	public void appLock(View v){
		Intent intent = new Intent(this , AppLockActivity.class);
		startActivity(intent);
	}
	//软件推荐功能(广告)
	public void appRecomment(View v){
		OffersManager.getInstance(this).showOffersWall(new Interface_ActivityListener() {

			/**
			 * 当积分墙销毁的时候，即积分墙的Activity调用了onDestory的时候回调
			 */
			@Override
			public void onActivityDestroy(Context context) {
				Toast.makeText(context, "全屏积分墙退出了", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
