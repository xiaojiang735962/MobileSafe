package com.project.mobilesafe.view;

import com.project.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
//设置中心的自定义组合控件
public class SettingClickView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.project.mobilesafe";
	private TextView tv_title;
	private TextView tv_desc;
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
		// 有style样式的话调用此方法
		super(context, attrs, defStyleAttr);
		initView();
	}
	public SettingClickView(Context context, AttributeSet attrs) {
		//  有属性的话调用此方法
		super(context, attrs);
		initView();
	}
	public SettingClickView(Context context) {
		// 用代码new对象时，调用此方法
		super(context);
		initView();
	}
	//初始化布局文件
	private void initView(){
		//将自定义好的布局文件设置给当前的SettingClickView
		View.inflate(getContext(), R.layout.view_setting_click, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}
	public void setTitle(String title){
		tv_title.setText(title);
	}
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
}
