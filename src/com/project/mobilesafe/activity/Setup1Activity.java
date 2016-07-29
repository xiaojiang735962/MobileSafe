package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//设置向导第一个界面
public class Setup1Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void showNextPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);

	}

	@Override
	public void showPreviousPage() {
	}
}
