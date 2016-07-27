package com.project.mobilesafe.activity;

import com.project.mobilesafe.R;
import com.project.mobilesafe.R.id;
import com.project.mobilesafe.R.layout;
import com.project.mobilesafe.R.menu;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private TextView tv_version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("版本号：" + getVersionName());
	}
	private String getVersionName(){
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
}
