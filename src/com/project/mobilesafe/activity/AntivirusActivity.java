package com.project.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.project.mobilesafe.R;
import com.project.mobilesafe.db.dao.AntivirusDao;
import com.project.mobilesafe.utils.MD5Utils;

import java.util.List;

public class AntivirusActivity extends Activity {

    private static final int BEGIN = 1;
    private static final int SCANING = 2;
    private static final int FINISH = 3;
    private Message message;
    private TextView tvInitVirus;
    private ProgressBar pbVirus;
    private ImageView ivScanning;
    private LinearLayout llVirus;
    private ScrollView svVirus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        initUI();
        initData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BEGIN:
                    tvInitVirus.setText("初始化双核杀毒引擎");
                    break;
                case SCANING:
                    //病毒扫描
                    TextView tvVirus = new TextView(AntivirusActivity.this);
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    if(scanInfo.desc){
                        tvVirus.setText(scanInfo.appNameVirus + "------存在木马病毒");
                        tvVirus.setTextColor(Color.RED);
                    }else{
                        tvVirus.setText(scanInfo.appNameVirus + "------扫描安全");
                        tvVirus.setTextColor(Color.GRAY);
                    }
                    llVirus.addView(tvVirus);
                    svVirus.post(new Runnable() {
                        @Override
                        public void run() {
                            svVirus.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    break;
                case FINISH:
                    //当扫描结束时，停止动画
                    ivScanning.clearAnimation();
                    break;
            }
        }
    };
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                //初始化消息
                message = Message.obtain();
                message.what = BEGIN;
                handler.sendMessage(message);
                //获取包管理器
                PackageManager packageManager = getPackageManager();
                //获取手机上安装的所以应用
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                //获取应用程序的数据
                int size = installedPackages.size();
                pbVirus.setMax(size);
                int progress = 0;
                for (PackageInfo packageInfo : installedPackages){
                    ScanInfo scanInfo = new ScanInfo();

                    //获取当前应用的名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    scanInfo.appNameVirus = appName;
                    //获取当前应用的包名
                    String packageName = packageInfo.applicationInfo.packageName;
                    scanInfo.packageNameVirus = packageName;
                    //获取到应用程序的目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    //获取文件的md5
                    String md5 = MD5Utils.getFileMd5(sourceDir);
                    //判断当前的md5值是否在病毒数据库中
                    String desc = AntivirusDao.checkFileVirus(md5);
                    //如果当前的desc为空，则没有病毒
                    if(desc == null){
                        scanInfo.desc = false;
                    }else{
                        scanInfo.desc = true;
                    }
                    progress ++ ;
                    SystemClock.sleep(100);
                    pbVirus.setProgress(progress);

                    message = Message.obtain();
                    message.what = SCANING;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                }
                message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }
    static class ScanInfo{
        boolean desc;
        String appNameVirus;
        String packageNameVirus;
    }
    private void initUI() {
        ivScanning = (ImageView) findViewById(R.id.iv_scanning);
        //初始化旋转动画
        RotateAnimation rotateAnimation = new RotateAnimation(0 , 360 , Animation.RELATIVE_TO_SELF , 0.5f , Animation.RELATIVE_TO_SELF , 0.5f);
        rotateAnimation.setDuration(5000);
        //无限循环
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        ivScanning.startAnimation(rotateAnimation);

        tvInitVirus = (TextView) findViewById(R.id.tv_init_virus);
        pbVirus = (ProgressBar) findViewById(R.id.pb_virus);
        llVirus = (LinearLayout) findViewById(R.id.ll_virus);
        svVirus = (ScrollView) findViewById(R.id.sv_virus);
    }
}
