package com.project.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.project.mobilesafe.R;
import com.project.mobilesafe.service.AddressService;
import com.project.mobilesafe.service.CallSafeService;
import com.project.mobilesafe.service.WatchDogService;
import com.project.mobilesafe.utils.ServiceStatusUtils;
import com.project.mobilesafe.view.SettingClickView;
import com.project.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {

    private SettingItemView siv_update;
    private SettingItemView siv_address;
    private SettingItemView sivCallSafe;
    private SettingClickView scvAddressStyle;
    private SettingClickView scvAddressLocation;
    private SharedPreferences mPref;
    private SettingItemView sivWacthDog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        initUpdateView();
        initAddressView();
        initAddressStyle();
        initAddressLocation();
        initBlackNumberView();
        initWatchDogView();
    }
    //设置看门狗服务
    private void initWatchDogView() {
        sivWacthDog = (SettingItemView) findViewById(R.id.siv_watchdog);

        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "com.project.mobilesafe.service.WatchDogService");
        if (serviceRunning) {
            sivWacthDog.setChecked(true);
        } else {
            sivWacthDog.setChecked(false);
        }
        sivWacthDog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivWacthDog.isChecked()) {
                    sivWacthDog.setChecked(false);
                    //停止看门狗服务
                    stopService(new Intent(SettingActivity.this, WatchDogService.class));
                } else {
                    sivWacthDog.setChecked(true);
                    //开启看门狗服务
                    startService(new Intent(SettingActivity.this, WatchDogService.class));
                }
            }
        });
    }
    //黑名单服务设置
    private void initBlackNumberView() {
        sivCallSafe = (SettingItemView) findViewById(R.id.siv_callsafe);

        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "com.project.mobilesafe.service.CallSafeService");
        if (serviceRunning) {
            sivCallSafe.setChecked(true);
        } else {
            sivCallSafe.setChecked(false);
        }
        sivCallSafe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivCallSafe.isChecked()) {
                    sivCallSafe.setChecked(false);
                    //停止黑名单服务
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));
                } else {
                    sivCallSafe.setChecked(true);
                    //开启黑名单服务
                    startService(new Intent(SettingActivity.this, CallSafeService.class));
                }
            }
        });
    }

    //初始化自动更新的开关
    private void initUpdateView() {
        siv_update = (SettingItemView) findViewById(R.id.siv_update);
//		siv_update.setTitle("自动更新设置");

        boolean auto_update = mPref.getBoolean("auto_update", true);
        if (auto_update) {
//			siv_update.setDesc("自动更新已开启");
            siv_update.setChecked(true);
        } else {
//			siv_update.setDesc("自动更新已关闭");
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //判读当前勾选状态
                if (siv_update.isChecked()) {
                    //点击时设置不勾选
                    siv_update.setChecked(false);
//					siv_update.setDesc("自动更新已关闭");
                    //更新SharedPreferences
                    mPref.edit().putBoolean("auto_update", false).commit();
                } else {
                    siv_update.setChecked(true);
//					siv_update.setDesc("自动更新已开启");
                    //更新SharedPreferences
                    mPref.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    //初始化归属的开关
    private void initAddressView() {
        siv_address = (SettingItemView) findViewById(R.id.siv_address);

        boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this, "com.project.mobilesafe.service.AddressService");
        if (serviceRunning) {
            siv_address.setChecked(true);
        } else {
            siv_address.setChecked(false);
        }
        siv_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_address.isChecked()) {
                    siv_address.setChecked(false);
                    //停止归属地服务
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                } else {
                    siv_address.setChecked(true);
                    //开启归属地服务
                    startService(new Intent(SettingActivity.this, AddressService.class));
                }
            }
        });
    }

    final String[] items = new String[]{"半透明", "活力澄", "卫士蓝", "金属灰", "苹果绿"};

    //修改提示框显示风格
    private void initAddressStyle() {
        scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);

        scvAddressStyle.setTitle("归属地提示框风格");
        int style = mPref.getInt("address_style", 0);
        scvAddressStyle.setDesc(items[style]);

        scvAddressStyle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDialog();
            }
        });
    }

    //弹出选择风格的单选框
    protected void showSingleChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("归属地提示框风格");
        //读取保存的style
        int style = mPref.getInt("address_style", 0);
        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //保存选择的风格
                mPref.edit().putInt("address_style", which).commit();
                dialog.dismiss();//让dialog消失

                scvAddressStyle.setDesc(items[which]);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    //修改归属地显示位置
    private void initAddressLocation() {
        scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setTitle("归属地提示框显示位置");
        scvAddressLocation.setDesc("设置归属地提示框的显示位置");
        scvAddressLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
            }
        });
    }
}
