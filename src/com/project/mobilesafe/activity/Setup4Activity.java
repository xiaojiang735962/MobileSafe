package com.project.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.project.mobilesafe.R;

//设置向导第四个界面
public class Setup4Activity extends BaseSetupActivity {
	
	private CheckBox cbStatus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		cbStatus = (CheckBox) findViewById(R.id.cb_status);
		boolean protect = mPref.getBoolean("protect", false);
		if(protect){
			cbStatus.setText("防盗保护已经开启");
			cbStatus.setChecked(true);
		}else{
			cbStatus.setText("防盗保护没有开启");
			cbStatus.setChecked(false);
		}
		cbStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cbStatus.setText("防盗保护已经开启");
					//将CheckBox的状态保存在sharedPreferences中
					mPref.edit().putBoolean("protect", true).commit();
				}else{
					cbStatus.setText("防盗保护没有开启");
					mPref.edit().putBoolean("protect", false).commit();
				}
			}
		});
	}
	@Override
	public void showNextPage() {
		startActivity(new Intent(this, LostFindActivity.class));
		finish();
		//设置sharedPreferences，表示已经展示过想到，下次不用展示
		mPref.edit().putBoolean("configed", true).commit();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
	}
}
