package com.project.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.project.mobilesafe.R;
import com.project.mobilesafe.bean.VirusInfo;
import com.project.mobilesafe.db.dao.AntivirusDao;
import com.project.mobilesafe.utils.StreamUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.youmi.android.AdManager;

public class SplashActivity extends Activity {

	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROE = 3;
	protected static final int CODE_ENTER_HOME = 4;

	private TextView tv_version;
	private TextView tv_progress;
	
	private String mVersionName; // 服务器获取版本名
	private int mVersionCode;// 服务器获取版本号
	private String mDescription;// 服务器获取版本描述
	private String mDownloadUrl;// 服务器获取下载链接
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDialog();
				break;

			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_JSON_ERROE:
				Toast.makeText(SplashActivity.this, "数据解析错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_ENTER_HOME:
				enterHome();
				break;
			}
		}
	};
	private SharedPreferences mPref;
	private RelativeLayout rl_root;
	private AntivirusDao antivirusDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AdManager.getInstance(this).init("86adaf6654db1969", "2c68bfe4a3a52a55",false);
		setContentView(R.layout.activity_splash);
		
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_version.setText("版本名：" + getVersionName());
		tv_progress = (TextView) findViewById(R.id.tv_progress);
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		//拷贝归属地查询数据库
		copyDB("address.db");
		//拷贝病毒数据库
		copyDB("antivirus.db");
		//更新病毒数据库
		updateVirus();

		//判断是否需要自动更新
		boolean auto_update = mPref.getBoolean("auto_update", true);
		if(auto_update){
			checkVersion();
		}else{
			//延迟三秒后发送消息
			mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 3000);
		}
		//闪屏添加渐变的动画效果(从透明到不透明)
		AlphaAnimation alpha = new AlphaAnimation(0.2f, 1);
		alpha.setDuration(2000);
		rl_root.startAnimation(alpha);

		//创建桌面快捷方式
		createShortCut();
	}
	//更新病毒数据库
	private void updateVirus() {

		antivirusDao = new AntivirusDao();
		//从服务器获取最新的md5的病毒特征码
		HttpUtils httpUtils = new HttpUtils();

		//本机地址使用localhost,如果用模拟器加载本及地址，可以用ip(10.0.2.2)来替换
		String url = "http://10.0.2.2:8080/virus.json";
		httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println(responseInfo.result);
				try {
//					JSONObject jsonObject = new JSONObject(responseInfo.result);
//					String md5 = jsonObject.getString("md5");
//					String desc = jsonObject.getString("desc");
					Gson gson = new Gson();
					//第一个参数为Json数据,第二个参数为与Json相对应的bean
					VirusInfo virusInfo = gson.fromJson(responseInfo.result, VirusInfo.class);
					antivirusDao.addVirus(virusInfo.md5 , virusInfo.desc);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException e, String s) {

			}
		});
	}

	//创建安全卫士的桌面快捷方式
	private void createShortCut() {
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//设置创建快捷方式的个数(false表示只能创建一个,true表示可以创建无数个)
		intent.putExtra("duplicate" , false);

		Intent shortCutIntent = new Intent();
		shortCutIntent.setAction("HomeActivity");
		shortCutIntent.addCategory(Intent.CATEGORY_DEFAULT);

		//设置快捷方式名字
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME , "手机卫士");
		//设置快捷方式图标
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON , BitmapFactory.decodeResource(getResources() , R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT , shortCutIntent);
		sendBroadcast(intent);
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
		final long startTime = System.currentTimeMillis();
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
						}else{
							//版本不更新直接跳转到主页面
							msg.what = CODE_ENTER_HOME;
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
					long endTime = System.currentTimeMillis();
					long usedTime = endTime - startTime;
					if(usedTime < 3000){
						//强制休眠一段时间，保证闪屏存在２秒
						try {
							Thread.sleep(3000 - usedTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(msg);
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
//		builder.setCancelable(false);//用户点返回键无用(不建议使用，太粗暴，体验太差)
		builder.setPositiveButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				System.out.println("立即更新");
				downloadNewVersion();
			}
		} );
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();
			}
		});
		//用户点击返回键时触发
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}
		});
		
		builder.show();
	}
	//下载新版本apk
	protected void downloadNewVersion() {
		tv_progress.setVisibility(View.VISIBLE);
		//使用XUtils第三方jar包实现下载
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			HttpUtils utils = new HttpUtils();
			String target = Environment.getExternalStorageDirectory() + "/MobileSafe2.0.apk";
			utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					// 下载文件进度
					super.onLoading(total, current, isUploading);
//					System.out.println("下载进度" + current + "/" + total);
					tv_progress.setText("下载进度:" + current * 100 / total +"%");
				}
				@Override
				public void onSuccess(ResponseInfo<File> target) {
					// 下载成功,跳转到系统下载页面
//					System.out.println("下载成功");
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(target.result), 
							"application/vnd.android.package-archive");
					//如果用户取消安装会返回结果
					startActivityForResult(intent, 0);
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// 下载失败
					Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
				}
			});
		}else {
			Toast.makeText(SplashActivity.this, "检测不到SD卡", Toast.LENGTH_SHORT).show();
		}
		
	}
	//如果用户取消安装,调用此方法
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}
	//进入主页面
	private void enterHome(){
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
	//拷贝数据库(初始化数据库，将数据库从assets中拷贝到data/data/com.project.mobilesafe/files目录下)
	private void copyDB(String dbName){
		//getFilesDir() ->　data/data/com.project.mobilesafe/files
		File destFile = new File(getFilesDir(), dbName);//要拷贝的目标地址
		if(destFile.exists()){
			System.out.println("数据库" + dbName + "以存在");
			return;
		}
		InputStream in = null ;
		FileOutputStream out = null ;
		try {
			in = getAssets().open(dbName);
			out = new FileOutputStream(destFile);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = in.read(buffer)) != -1){
				out.write(buffer , 0 , len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
