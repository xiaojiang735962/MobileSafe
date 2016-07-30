package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;
import com.project.mobilesafe.db.dao.AddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

public class AddressActivity extends Activity {
	
	private EditText etNumber;
	private TextView tvAddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		
		etNumber = (EditText) findViewById(R.id.et_number);
		tvAddress = (TextView) findViewById(R.id.tv_address);
		
		//监听EditText的变化(实时查询)
		etNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 内容发生变化时调用(实时查询)
				String address = AddressDao.getAddress(s.toString());
				tvAddress.setText(address);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// 内容发生变化前调用
			}
			@Override
			public void afterTextChanged(Editable s) {
				// 内容发生变化后调用
			}
		});
	}
	//调用AddressDao中的getAddress()方法开始查询
	public void query(View v){
		String number = etNumber.getText().toString();
		if(!TextUtils.isEmpty(number)){
			String address = AddressDao.getAddress(number);
			tvAddress.setText(address);
		}else{
			//文本输入框的抖动动画(Interpolator插补器)
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//			shake.setInterpolator(new Interpolator() {
//				@Override//辅助动画特效
//				public float getInterpolation(float input) {
//					// 函数算法(比如匀速：return input ; 相当于y = x)
//					//input 相当于x，return 相当于y ,实现算法
//					return 0;
//				}
//			});
			etNumber.startAnimation(shake);
			vibrate();
		}
	}
	//手机震动，需要权限android.permission.VIBRATE
	private void vibrate(){
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(1000);//震动1秒
		//参数1：先等待一秒然后震动两秒，再等待一秒，再震动两秒
		//参数2：如果为-1表示循环一次，如果为0表示一直循环(数字表示从第几个位置开始循环)
		//vibrator.vibrate(new long[]{1000,2000,1000,2000},-1);
		//取消震动vibrator.cancel();
	}
}
