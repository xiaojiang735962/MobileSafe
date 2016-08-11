package com.project.mobilesafe.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.project.mobilesafe.R;
import com.project.mobilesafe.fragment.LockFragment;
import com.project.mobilesafe.fragment.UnlockFragment;

public class AppLockActivity extends FragmentActivity implements View.OnClickListener {

    private FrameLayout flContent;
    private TextView tvUnlock;
    private TextView tvLock;
    private UnlockFragment unlockFragment;
    private LockFragment lockFragment;
    private FragmentManager supportFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();
    }

    private void initUI() {
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        tvUnlock = (TextView) findViewById(R.id.tv_unlock);
        tvLock = (TextView) findViewById(R.id.tv_lock);

        tvUnlock.setOnClickListener(this);
        tvLock.setOnClickListener(this);

        //获取Fragment的管理者
        supportFragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction mTransaction =  supportFragmentManager.beginTransaction();
        unlockFragment = new UnlockFragment();
        lockFragment = new LockFragment();
        /**
         * 替换界面,参数1：表示要替换界面的id 参数2：表示要替换界面的Fragment对象
         */
        mTransaction.replace(R.id.fl_content , unlockFragment);
        mTransaction.commit();

    }

    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();

        switch (view.getId()){
            case R.id.tv_unlock:
                //没有加锁
                tvUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tvLock.setBackgroundResource(R.drawable.tab_right_default);

                fragmentTransaction.replace(R.id.fl_content , unlockFragment);
                break;
            case R.id.tv_lock:
                //已经加锁
                tvUnlock.setBackgroundResource(R.drawable.tab_left_default);
                tvLock.setBackgroundResource(R.drawable.tab_right_pressed);

                fragmentTransaction.replace(R.id.fl_content , lockFragment);
        }
        fragmentTransaction.commit();
    }
}
