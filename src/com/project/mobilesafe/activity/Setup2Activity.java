package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
//设置向导第二个界面
public class Setup2Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
	}
	//跳转到上一页
	public void previous(View v){
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
	}
	//跳转到下一页
	public void next(View v){
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
	}
}
