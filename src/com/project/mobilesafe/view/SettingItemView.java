package com.project.mobilesafe.view;

import com.project.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
//设置中心的自定义组合控件
public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.project.mobilesafe";
	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;
	private String mTitle;
	private String mDescOn;
	private String mDescOff;
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		// 有style样式的话调用此方法
		super(context, attrs, defStyleAttr);
		initView();
	}
	public SettingItemView(Context context, AttributeSet attrs) {
		//  有属性的话调用此方法
		super(context, attrs);
		
		mTitle = attrs.getAttributeValue(NAMESPACE, "set_title");
		mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
		mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
		
		initView();
	}
	public SettingItemView(Context context) {
		// 用代码new对象时，调用此方法
		super(context);
		initView();
	}
	//初始化布局文件
	private void initView(){
		//将自定义好的布局文件设置给当前的SettingItemView
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		//设置标题
		setTitle(mTitle);
	}
	public void setTitle(String title){
		tv_title.setText(title);
	}
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	public void setChecked(boolean check){
		cb_status.setChecked(check);
		//根据选择的状态更新文本描述
		if(check){
			setDesc(mDescOn);
		}else{
			setDesc(mDescOff);
		}
	}
	
}
