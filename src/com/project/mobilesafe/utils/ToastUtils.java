package com.project.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	//Toast工具,不管在主线程还是子线程都可以刷新UI
	public static void showToast(final Activity context, final String text){
		if("main".equals(Thread.currentThread().getName())){
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}else{
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
				}
			});
		}

	}
}
