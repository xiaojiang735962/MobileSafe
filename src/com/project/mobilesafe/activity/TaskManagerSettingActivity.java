package com.project.mobilesafe.activity;/*
       Created by xiaojiang on 8/9/16.
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.project.mobilesafe.R;
import com.project.mobilesafe.service.KillProcessService;
import com.project.mobilesafe.utils.ServiceStatusUtils;
import com.project.mobilesafe.utils.SharedPreferencesUtils;

public class TaskManagerSettingActivity extends Activity{

    private CheckBox cbClearStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);
        initUI();
    }

    private void initUI() {
        CheckBox cbSystemStatus = (CheckBox) findViewById(R.id.cb_system_status);
        //设置是否选中
        cbSystemStatus.setChecked(SharedPreferencesUtils.getBoolean(TaskManagerSettingActivity.this , "is_show_system", true));
        cbSystemStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this , "is_show_system" , true);
                }else {
                    SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this , "is_show_system" , false);
                }
            }
        });

        //定时清理进程(需要开启服务)
        cbClearStatus = (CheckBox) findViewById(R.id.cb_clear_status);
        final Intent intent = new Intent(this, KillProcessService.class);
        cbClearStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }
            }
        });
    }
    //判断服务是否开启(设置CheckBox勾选状态)
    @Override
    protected void onStart() {
        super.onStart();
        if(ServiceStatusUtils.isServiceRunning(TaskManagerSettingActivity.this ,
                "com.project.mobilesafe.service.KillProcessService")){
            cbClearStatus.setChecked(true);
        }else {
            cbClearStatus.setChecked(false);
        }
    }
}
