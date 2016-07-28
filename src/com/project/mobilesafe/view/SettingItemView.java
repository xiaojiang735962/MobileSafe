package com.project.mobilesafe.view;

import com.project.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
//设置中心的自定义控件
public class SettingItemView extends RelativeLayout {

	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		// 有style样式的话调用此方法
		super(context, attrs, defStyleAttr);
		initView();
	}
	public SettingItemView(Context context, AttributeSet attrs) {
		//  有属性的话调用此方法
		super(context, attrs);
		initView();
	}
	public SettingItemView(Context context) {
		// 用代码new对象时，调用此方法
		super(context);
		initView();
	}
	
	private void initView(){
		//将自定义好的布局文件设置给当前的SettingItemView
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
	}

	
}
