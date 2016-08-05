package com.project.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mobilesafe.R;
import com.project.mobilesafe.adapter.MyBaseAdapter;
import com.project.mobilesafe.bean.BlackNumberInfo;
import com.project.mobilesafe.db.dao.BlackNumberDao;
//分页加载数据
public class CallSafeActivity2 extends Activity {

	private ListView listView;
	private List<BlackNumberInfo> blackNumberInfos;
	
	//设置当前页，和每页显示的数据
	private int startIndex = 0;
	private int maxCount = 20;
	private int totalNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_safe2);
		initUI();
		initData();
	}
	private CallSafeAdapter adapter;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			llPb.setVisibility(View.INVISIBLE);
			if(adapter == null){
				adapter = new CallSafeAdapter(blackNumberInfos,CallSafeActivity2.this);
				listView.setAdapter(adapter);
			}else{
				adapter.notifyDataSetChanged();
			}
		};
	};
	private LinearLayout llPb;
	private BlackNumberDao dao;
	private void initData() {
		dao = new BlackNumberDao(CallSafeActivity2.this);
		//一共有多少数据
		totalNumber = dao.getTotalNumber();
		new Thread(){
			public void run() {
				//分批加载数据
				if(blackNumberInfos == null){
					blackNumberInfos = dao.findPar2(startIndex, maxCount);
				}else{
					//把后面的数据追加到blackNumberInfos集合中，防止数据覆盖
					blackNumberInfos.addAll(dao.findPar2(startIndex, maxCount));
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		llPb = (LinearLayout) findViewById(R.id.ll_pb);
		//展示加载的ProgressBar
		llPb.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.list_view);
		//设置listView的滚动监听
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			/**
			 * scrollState 表示滚动的状态
			 * 		AbsListView.OnScrollListener.SCROLL_STATE_IDLE 闲置状态
			 * 		AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手指触摸的时候状态
			 * 		AbsListView.OnScrollListener.SCROLL_STATE_FLING 惯性
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 状态改变时回调的方法
				switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
					//获取最后一条显示的数据
					int lastVisiblePosition = listView.getLastVisiblePosition();
					if(lastVisiblePosition == blackNumberInfos.size()-1){
						if(startIndex >= totalNumber){
							Toast.makeText(CallSafeActivity2.this, "已经没有数据了", Toast.LENGTH_SHORT).show();
							return;
						}
						initData();
					}
					startIndex += maxCount;
					break;
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// listView滚动时候回调的方法，时时调用，手指触摸就会调用
				
			}
		});
	}
	private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo>{
		public CallSafeAdapter(List lists, Context mContext) {
			super(lists, mContext);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = View.inflate(CallSafeActivity2.this, R.layout.item_call_safe, null);
				holder = new ViewHolder();
				holder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
				holder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
				holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvNumber.setText(lists.get(position).getNumber());
			String mode = lists.get(position).getMode();
			if(mode.equals("1")){
				holder.tvMode.setText("来电拦截+短信");
			}else if(mode.equals("2")){
				holder.tvMode.setText("来电拦截");
			}else if(mode.equals("3")){
				holder.tvMode.setText("短信拦截");
			}
			//点击图片删除条目
			final BlackNumberInfo info = lists.get(position);
			holder.ivDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean result = dao.delete(number);
					if(result){
						Toast.makeText(CallSafeActivity2.this, "删除成功", Toast.LENGTH_SHORT).show();
						lists.remove(info);
						adapter.notifyDataSetChanged();
					}else{
						Toast.makeText(CallSafeActivity2.this, "删除失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return convertView;
		}
		
	}
	static class ViewHolder{
		TextView tvNumber;
		TextView tvMode;
		ImageView ivDelete;
	}
	
	public void addBlackNumber(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(CallSafeActivity2.this, R.layout.dialog_add_black_number, null);
		final EditText etBlackPhone = (EditText) view.findViewById(R.id.et_blackphone);
		final CheckBox cbPhone = (CheckBox) view.findViewById(R.id.cb_phone);
		final CheckBox cbSms = (CheckBox) view.findViewById(R.id.cb_sms);
		Button btOk = (Button) view.findViewById(R.id.bt_ok);
		Button btCancel = (Button) view.findViewById(R.id.bt_cancel);
		btCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String etPhone = etBlackPhone.getText().toString().trim();
				if(TextUtils.isEmpty(etPhone)){
					return;
				}
				String mode = "";
				if(cbPhone.isChecked() && cbSms.isChecked()){
					mode = "1";
				}else if(cbPhone.isChecked()){
					mode = "2";
				}else if(cbSms.isChecked()){
					mode = "3";
				}else{
					Toast.makeText(CallSafeActivity2.this, "请选择拦截模式", Toast.LENGTH_SHORT).show();
					return;
				}
				BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
				blackNumberInfo.setNumber(etPhone);
				blackNumberInfo.setMode(mode);
				blackNumberInfos.add(0 , blackNumberInfo);
				//把电话号码和拦截模式添加到数据库
				dao.add(etPhone, mode);
				if(adapter == null ){
					adapter = new CallSafeAdapter(blackNumberInfos, CallSafeActivity2.this);
					listView.setAdapter(adapter);
				}else{
					adapter.notifyDataSetChanged();
				}
				dialog.dismiss();
			}
		});
		dialog.setView(view);
		dialog.show();
	}
}
