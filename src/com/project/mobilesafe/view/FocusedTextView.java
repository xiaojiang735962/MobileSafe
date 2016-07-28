package com.project.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusedTextView extends TextView {

	public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		// 有style样式的话调用此方法
		super(context, attrs, defStyleAttr);
	}

	public FocusedTextView(Context context, AttributeSet attrs) {
		// 有属性的话调用此方法
		super(context, attrs);
	}

	public FocusedTextView(Context context) {
		// 用代码new对象时，调用此方法
		super(context);
	}
	/**
	 * 判断是否获取焦点
	 * 跑马灯要运行，首先调用此方法判断是否有焦点
	 * 强制返回true，让跑马灯已知获取焦点
	 */
	@Override
	public boolean isFocused() {
		return true;
	}
	
}
