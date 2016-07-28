package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
//设置向导第四个界面
public class Setup4Activity extends Activity {
	private SharedPreferences mPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
	}
	//跳转到上一页
	public void previous(View v){
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		mPref.edit().putBoolean("configed", true).commit();
	}
	//跳转到下一页
	public void next(View v){
		startActivity(new Intent(this, LostFindActivity.class));
		finish();
	}
}
