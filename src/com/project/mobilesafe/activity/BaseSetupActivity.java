package com.project.mobilesafe.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
//此Activity为设置向导的基类,不用在清单文件中注册，因为此页面不用展示
public abstract class  BaseSetupActivity extends Activity {
	
	private GestureDetector mDetector;
	public SharedPreferences mPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		mDetector = new GestureDetector(this,new SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//判断纵向滑动幅度是否过大，过大的话不允许切换界面
				if(Math.abs(e2.getRawY() - e1.getRawY()) > 100){
					return true;
				}
				//判断是否滑动过慢
				if(Math.abs(velocityX) < 100){
					return true;
				}
				//向右划,上一页
				if((e2.getRawX() - e1.getRawX()) > 200){
					showPreviousPage();
				}
				//向左划，下一页
				if((e1.getRawX() - e2.getRawX()) > 200){
					showNextPage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	// 跳转到下一页
		public abstract void showNextPage() ;
		// 跳转到上一页
		public abstract void showPreviousPage();

		// 点击按钮跳转到上一页
		public void previous(View v) {
			showPreviousPage();
		}

		// 跳转到下一页
		public void next(View v) {
			showNextPage();
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			mDetector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
}
