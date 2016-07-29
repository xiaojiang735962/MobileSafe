package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {

	private SharedPreferences mPref;
	private TextView tvSafePhone;
	private ImageView ivProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		boolean configed = mPref.getBoolean("configed", false);
		// 判断是否进入过设置向导
		if (configed) {// 进入过，转向手机防盗主页
			setContentView(R.layout.activity_lost_find);
			
			//根据sharedPreferences更新安全号码
			tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
			String phone = mPref.getString("safe_phone", "");
			tvSafePhone.setText(phone);
			//根据sharedPreferences更新安全锁
			ivProtect = (ImageView) findViewById(R.id.iv_protect);
			boolean protect = mPref.getBoolean("protect", false);
			if(protect){
				ivProtect.setImageResource(R.drawable.lock);
			}else{
				ivProtect.setImageResource(R.drawable.unlock);
			}
			
		} else {// 跳转到设置向导
			finish();
			startActivity(new Intent(this, Setup1Activity.class));
		}

	}

	// 从新进入设置向导
	public void reEnter(View v) {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}
}
