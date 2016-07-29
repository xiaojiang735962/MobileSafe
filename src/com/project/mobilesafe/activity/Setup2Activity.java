package com.project.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.project.mobilesafe.R;
import com.project.mobilesafe.utils.ToastUtils;
import com.project.mobilesafe.view.SettingItemView;

//设置向导第二个界面
public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView sivSim;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		//实现SIM卡的绑定
		sivSim = (SettingItemView) findViewById(R.id.siv_sim);
		String sim = mPref.getString("sim", null);
		if(!TextUtils.isEmpty(sim)){
			sivSim.setChecked(true);
		}else{
			sivSim.setChecked(false);
		}
		sivSim.setOnClickListener(new OnClickListener() {
			//如果是勾选状态，点击后为不勾选状态
			@Override
			public void onClick(View v) {
					if(sivSim.isChecked()){
						sivSim.setChecked(false);
						mPref.edit().remove("sim").commit();
					}else{
						sivSim.setChecked(true);
						//获取SIM卡的序列号并保存
						TelephonyManager mManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
						String simSerialNumber = mManager.getSimSerialNumber();
						System.out.println("sim卡序列号:" + simSerialNumber);
						mPref.edit().putString("sim", simSerialNumber).commit();
					}
			}
		});
	}
	// 跳转到下一页
	public void showNextPage() {
		//判断如果sim没有绑定不能跳转到下一个页面
		String sim = mPref.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			ToastUtils.showToast(this, "必须绑定SIM卡");
			return;
		}
		startActivity(new Intent(this, Setup3Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}
	// 跳转到上一页
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup1Activity.class));
		finish();
		// 两个界面切换的动画
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);
	}

}
