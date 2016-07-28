package com.project.mobilesafe.activity;


import com.project.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	
	private GridView gv_home;
	private String[] mItems = new String[]{"手机防盗","通讯卫士","软件管理","进程管理",
			"流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
	private int[] mPictures = new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
			R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
			R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new HomeAdapter());
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				switch (position) {
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
