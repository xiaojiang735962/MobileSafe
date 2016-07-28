package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LostFindActivity extends Activity {

	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = mPref.getBoolean("configed", false);
		//判断是否进入过设置向导
		if(configed){//进入过，转向手机防盗主页
			setContentView(R.layout.activity_lost_find);
		}else{//跳转到设置向导
			finish();
			startActivity(new Intent(this,Setup1Activity.class));
		}
		
	}
}
