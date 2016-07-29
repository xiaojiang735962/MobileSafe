package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;
import com.project.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;

//设置向导第三个界面
public class Setup3Activity extends BaseSetupActivity {
	private EditText etPhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		etPhone = (EditText) findViewById(R.id.et_phone);
		String phone = mPref.getString("safe_phone", "");
		etPhone.setText(phone);
	}
	@Override
	public void showNextPage() {
		String phone = etPhone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			ToastUtils.showToast(this, "安全号码不能为空");
			return;
		}
		//将安全号码保存在sharedPreferences
		mPref.edit().putString("safe_phone", phone).commit();
		startActivity(new Intent(this, Setup4Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}
	//选择联系人
	public void selectContant(View v){
		startActivityForResult(new Intent(this, ContactActivity.class), 0);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			String phone = data.getStringExtra("phone");
			//将电话号码中的－和空格替换
			phone = phone.replaceAll("-", "").replaceAll(" ", "");
			//把电话号码设置给输入框
			etPhone.setText(phone);
		}
	}
}
