package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//设置向导第三个界面
public class Setup3Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
	}
	//跳转到上一页
	public void previous(View v){
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
	}
	//跳转到下一页
	public void next(View v){
		startActivity(new Intent(this, Setup4Activity.class));
		finish();
	}
}
