package com.project.mobilesafe.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mobilesafe.R;
import com.project.mobilesafe.utils.MD5Utils;

public class HomeActivity extends Activity {
	
	private GridView gv_home;
	private String[] mItems = new String[]{"手机防盗","通讯卫士","软件管理","进程管理",
			"流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
	private int[] mPictures = new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
			R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
			R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};
	private SharedPreferences mPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new HomeAdapter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch (position) {
				case 0:
					//跳转至设置密码对话框(手机防盗)
					showPasswordDialog();
					break;
				case 8:
					//跳转至SettingActivity页面(设置中心)
					startActivity(new Intent(HomeActivity.this, SettingActivity.class));
					break;

				default:
					break;
				}
			}
		});
	}
	protected void showPasswordDialog() {
		// 判读是否设置密码，如果没有设置密码，弹出设置密码对话框
		String savedPassword = mPref.getString("password", null);
		if(!TextUtils.isEmpty(savedPassword)){
			//弹出输入密码弹窗
			showPasswordInputDialog();
		}else{
			//弹出设置密码弹窗
			showPasswordSetDialog();
		}
	}
	private void showPasswordInputDialog() {
		// 输入密码弹窗
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_input_password, null); 
		//将自定义布局文件设置给dialog，并设置dialog的上下左右边距
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		Button btConfirm = (Button) view.findViewById(R.id.bt_confirm);
		Button btCancel =(Button) view.findViewById(R.id.bt_cancel);
		final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
		btConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = etPassword.getText().toString();
				String savedPassword = mPref.getString("password", null);
				//TextUtils替换了password!=null && !password.equals("")
				if(!TextUtils.isEmpty(password)){
					if(MD5Utils.encoding(password).equals(savedPassword)){
//						Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						//跳转到手机防盗页面
						startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
					}else{
						Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
				}
			}
		});
		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	private void showPasswordSetDialog() {
		// 设置密码弹窗
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_set_password, null); 
		//将自定义布局文件设置给dialog，并设置dialog的上下左右边距
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		Button btConfirm = (Button) view.findViewById(R.id.bt_confirm);
		Button btCancel =(Button) view.findViewById(R.id.bt_cancel);
		final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
		final EditText etConfirm = (EditText) view.findViewById(R.id.et_password_confirm);
		btConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = etPassword.getText().toString();
				String confirm = etConfirm.getText().toString();
				//TextUtils替换了password!=null && !password.equals("")
				if(!TextUtils.isEmpty(password) && !confirm.isEmpty()){
					if(password.equals(confirm)){
//						Toast.makeText(HomeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
						mPref.edit().putString("password", MD5Utils.encoding(password)).commit();//保存密码到sharedPreferences
						dialog.dismiss();
						//跳转到手机防盗页面
						startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
					}else{
						Toast.makeText(HomeActivity.this, "输入密码不一致", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
				}
			}
		});
		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	class HomeAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mItems.length;
		}
		@Override
		public Object getItem(int position) {
			return mItems[position];
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			
			iv_item.setImageResource(mPictures[position]);
			tv_item.setText(mItems[position]);
			return view;
		}
		
	}
}
