package com.project.mobilesafe.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.project.mobilesafe.R;
import com.project.mobilesafe.utils.StreamUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROE = 3;

	private TextView tv_version;
	
	private String mVersionName; // 服务器获取版本名
	private int mVersionCode;// 服务器获取版本号
	private String mDescription;// 服务器获取版本描述
	private String mDownloadUrl;// 服务器获取下载链接
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;

			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
				break;
			case CODE_JSON_ERROE:
				Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("版本名：" + getVersionName());
		
		checkVersion();
	}
	//获取本地版本名
	private String getVersionName(){
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	//获取本地版本号
	private int getVersionCode(){
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			return versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	//检查是否升级新版本
	private void checkVersion(){
		new Thread(){
			private HttpURLConnection conn = null;

			public void run() {
				Message msg = new Message();
				try {
					//本机地址使用localhost,如果用模拟器加载本及地址，可以用ip(10.0.2.2)来替换
					URL url = new URL("http://10.0.2.2:8080/update.json");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");//设置请求发放
					conn.setConnectTimeout(5000);//设置连接超时
					conn.setReadTimeout(5000);//设置响应超时
					conn.connect();
					
					if(conn.getResponseCode() == 200){
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.getStringFromStream(inputStream);
//						System.out.println("网络返回" + result);
						//解析JSON
						JSONObject json = new JSONObject(result);
						mVersionName = json.getString("versionName");
						mVersionCode = json.getInt("versionCode");
						mDescription = json.getString("description");
						mDownloadUrl = json.getString("downloadUrl");
//						System.out.println("版本描述:" + mDescription);
						if(mVersionCode > getVersionCode()){
							//如果服务器版本号大于本地版本号，需要更新
//							showUpdateDialog();子线程不能刷新UI
							msg.what = CODE_UPDATE_DIALOG;
						}
					}
					
				} catch (MalformedURLException e) {
					// url错误异常
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// 网络错误异常
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = CODE_JSON_ERROE;
					// JSON解析错误
					e.printStackTrace();
				}finally{
					handler.sendMessage(msg);
					if(conn != null){
						conn.disconnect();//关闭网络连接
					}
				}
			}
		}.start();
	}
	//弹出版本升级对话框
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本:" + mVersionName);
		builder.setMessage(mDescription);
		builder.setPositiveButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("立即更新");
			}
		} );
		builder.setNegativeButton("以后再说", null);
		builder.show();
	}
	
}
