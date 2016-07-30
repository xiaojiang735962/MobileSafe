package com.project.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.project.mobilesafe.R;
import com.project.mobilesafe.service.AddressService;
import com.project.mobilesafe.utils.ServiceStatusUtils;
import com.project.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {

	private SettingItemView siv_update;
	private SettingItemView siv_address;
	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		initUpdateView();
		initAddressView();
		
	}
	//初始化自动更新的开关
	private void initUpdateView(){
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
//		siv_update.setTitle("自动更新设置");
		
		boolean auto_update = mPref.getBoolean("auto_update", true);
		if(auto_update){
//			siv_update.setDesc("自动更新已开启");
			siv_update.setChecked(true);
		}else{
//			siv_update.setDesc("自动更新已关闭");
			siv_update.setChecked(false);
		}
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//判读当前勾选状态
				if(siv_update.isChecked()){
					//点击时设置不勾选
					siv_update.setChecked(false);
//					siv_update.setDesc("自动更新已关闭");
					//更新SharedPreferences
					mPref.edit().putBoolean("auto_update", false).commit();
				}else{
					siv_update.setChecked(true);
//					siv_update.setDesc("自动更新已开启");
					//更新SharedPreferences
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
	}
	//初始化归属的开关
	private void initAddressView(){
		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "com.project.mobilesafe.service.AddressService");
		if(serviceRunning){
			siv_address.setChecked(true);
		}else{
			siv_address.setChecked(false);
		}
		siv_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(siv_address.isChecked()){
					siv_address.setChecked(false);
					//停止归属地服务
					stopService(new Intent(SettingActivity.this, AddressService.class));
				}else{
					siv_address.setChecked(true);
					//开启归属地服务
					startService(new Intent(SettingActivity.this, AddressService.class));
				}
			}
		});
	}
}
