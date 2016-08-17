package com.project.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.project.mobilesafe.R;
import com.project.mobilesafe.utils.ToastUtils;

public class EnterPwdActivity extends Activity implements View.OnClickListener {

    private EditText etPwd;
    private Button bt0;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button bt4;
    private Button bt5;
    private Button bt6;
    private Button bt7;
    private Button bt8;
    private Button bt9;
    private Button btClear;
    private Button btDelete;
    private Button btOk;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        initUI();
    }

    private void initUI() {
        Intent intent = getIntent();
        if (intent != null) {
            packageName = intent.getStringExtra("packageName");
        }
        etPwd = (EditText) findViewById(R.id.et_pwd);
//        etPwd.setInputType(InputType.TYPE_NULL);
        bt0 = (Button) findViewById(R.id.bt_0);
        bt1 = (Button) findViewById(R.id.bt_1);
        bt2 = (Button) findViewById(R.id.bt_2);
        bt3 = (Button) findViewById(R.id.bt_3);
        bt4 = (Button) findViewById(R.id.bt_4);
        bt5 = (Button) findViewById(R.id.bt_5);
        bt6 = (Button) findViewById(R.id.bt_6);
        bt7 = (Button) findViewById(R.id.bt_7);
        bt8 = (Button) findViewById(R.id.bt_8);
        bt9 = (Button) findViewById(R.id.bt_9);
        btClear = (Button) findViewById(R.id.bt_clear);
        btDelete = (Button) findViewById(R.id.bt_delete);
        btOk = (Button) findViewById(R.id.bt_ok);

        bt0.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt9.setOnClickListener(this);
        btClear.setOnClickListener(this);
        btDelete.setOnClickListener(this);
        btOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String str = etPwd.getText().toString();
        switch (view.getId()){
            case R.id.bt_0:
                etPwd.setText(str + bt0.getText().toString());
                break;
            case R.id.bt_1:
                etPwd.setText(str + bt1.getText().toString());
                break;
            case R.id.bt_2:
                etPwd.setText(str + bt2.getText().toString());
                break;
            case R.id.bt_3:
                etPwd.setText(str + bt3.getText().toString());
                break;
            case R.id.bt_4:
                etPwd.setText(str + bt4.getText().toString());
                break;
            case R.id.bt_5:
                etPwd.setText(str + bt5.getText().toString());
                break;
            case R.id.bt_6:
                etPwd.setText(str + bt6.getText().toString());
                break;
            case R.id.bt_7:
                etPwd.setText(str + bt7.getText().toString());
                break;
            case R.id.bt_8:
                etPwd.setText(str + bt8.getText().toString());
                break;
            case R.id.bt_9:
                etPwd.setText(str + bt9.getText().toString());
                break;
            case R.id.bt_clear:
                etPwd.setText("");
                break;
            case R.id.bt_delete:
                if(str.length() == 0){
                    return;
                }
                etPwd.setText(str.substring(0 , str.length() - 1));
                break;
            case R.id.bt_ok:
                if("123".equals(str)){
//                    System.out.println("密码正确，跳转到应用");
                    Intent intent = new Intent();
                    intent.setAction("com.itheima.mobileguard.stopprotect");
                    intent.putExtra("packageName", packageName);
                    sendBroadcast(intent);
                    finish();
                }else{
                    ToastUtils.showToast(EnterPwdActivity.this , "密码错误");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }
}
