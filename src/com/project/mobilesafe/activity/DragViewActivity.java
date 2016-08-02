package com.project.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.project.mobilesafe.R;
//修改归属地显示位置
public class DragViewActivity extends Activity {
	
	private TextView tvTop;
	private TextView tvBottom;
	private ImageView ivDrag;
	
	private int startX;
	private int startY;
	long[] mHits = new long[2];//数组长度为点击次数(双击事件)
	private SharedPreferences mPref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);
		
		tvTop = (TextView) findViewById(R.id.tv_top);
		tvBottom = (TextView) findViewById(R.id.tv_bottom);
		ivDrag = (ImageView) findViewById(R.id.iv_drag);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		
		int lastX = mPref.getInt("lastX", 0);
		int lastY = mPref.getInt("lastY", 0);
		
		//获取屏幕宽和高
		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
		final int winHeight = getWindowManager().getDefaultDisplay().getHeight();
		//初始化时候判断图片在上面还是下面
		if(lastY > winHeight / 2){//上面显示，下面隐藏
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		}else{//下面显示，上面隐藏
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}
		//获取布局对象
		RelativeLayout.LayoutParams layoutParams = (LayoutParams) ivDrag.getLayoutParams();
		layoutParams.leftMargin = lastX;//设置左边距
		layoutParams.topMargin = lastY;//设置上边距
		ivDrag.setLayoutParams(layoutParams);
		//设置双击事件
		ivDrag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//数组下标为1的值拷贝到下标为0，一次循环直到mHits.length-1(也就是最后一个值)
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				//开机后开始计算时间，并赋值给数组最后一位
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				//如果数组最后一位值 - 500 小于 数组第一位的值，则说明为多击事件
				if(mHits[0] >= (SystemClock.uptimeMillis() - 500)){
					//双击使图片居中
					ivDrag.layout((winWidth - ivDrag.getWidth()) / 2, (winHeight - ivDrag.getHeight()) / 2,
							(winWidth + ivDrag.getWidth()) / 2, (winHeight + ivDrag.getHeight()) / 2);
					//由于文本显示内容过长，获取的中心点与图片有差别，所以需要调节
					mPref.edit().putInt("lastX", (winWidth - ivDrag.getWidth()) / 2 - 30).commit();
					mPref.edit().putInt("lastY", (winHeight - ivDrag.getHeight()) / 2).commit();
				}
			}
		});
		//设置触摸监听
		ivDrag.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://按下时触发
					//获取起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE://移动时触发
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();
					
					//计算移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;
					//更新左上右下距离
					int l = ivDrag.getLeft() + dx;
					int t = ivDrag.getTop() + dy ;
					int r = ivDrag.getRight() + dx ;
					int b = ivDrag.getBottom() + dy;
					
					//判断图片是否拖出Window界面(状态栏高度，大概多出25dp)
					if(l < 0 || t < 0 || r > winWidth || b > winHeight-25){
						break;
					}
					if(t > winHeight / 2 ){//上面显示，下面隐藏
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					}else{//上面隐藏，下面显示
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}
					//更新界面
					ivDrag.layout(l, t, r, b);
					//重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP://松开时触发
					mPref.edit().putInt("lastX", ivDrag.getLeft()).commit();
					mPref.edit().putInt("lastY", ivDrag.getTop()).commit();
					break;
				}
				return false;//事件向下传递，让别的事件可以响应(使双击事件相应)
			}
		});
	}
}
