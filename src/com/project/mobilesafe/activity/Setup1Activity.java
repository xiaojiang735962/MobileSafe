package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//设置向导第一个界面
public class Setup1Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	//跳转到下一页
	public void next(View v){
		finish();
		startActivity(new Intent(this, Setup2Activity.class));
	}
}
