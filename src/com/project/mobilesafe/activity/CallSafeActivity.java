package com.project.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
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
public class CallSafeActivity extends Activity {

	private ListView listView;
	private List<BlackNumberInfo> blackNumberInfos;
	
	//设置当前页，和每页显示的数据
	private int mCurrentPageNumber = 1;
	private int mPageSize = 20;
	private int totalPage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_safe);
		initUI();
		initData();
	}
	private CallSafeAdapter adapter;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tvPageSize.setText(mCurrentPageNumber + "/" + totalPage );
			llPb.setVisibility(View.INVISIBLE);
			adapter = new CallSafeAdapter(blackNumberInfos,CallSafeActivity.this);
			listView.setAdapter(adapter);
		};
	};
	private LinearLayout llPb;
	private TextView tvPageSize;
	private EditText etPageNumber;
	private BlackNumberDao dao;
	private void initData() {
		new Thread(){
			public void run() {
				dao = new BlackNumberDao(CallSafeActivity.this);
				//获取总的页数
				totalPage = dao.getTotalNumber() / mPageSize;
				
				blackNumberInfos = dao.findPar(mCurrentPageNumber - 1, mPageSize);
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		llPb = (LinearLayout) findViewById(R.id.ll_pb);
		//展示加载的ProgressBar
		llPb.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.list_view);
		tvPageSize = (TextView) findViewById(R.id.tv_page_size);
		etPageNumber = (EditText) findViewById(R.id.et_page_number);
	}
	private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo>{
		public CallSafeAdapter(List lists, Context mContext) {
			super(lists, mContext);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
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
						Toast.makeText(CallSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
						lists.remove(info);
						adapter.notifyDataSetChanged();
					}else{
						Toast.makeText(CallSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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
	//上一页
	public void prePage(View v){
		if(mCurrentPageNumber <= 1){
			Toast.makeText(this, "没有上一页", Toast.LENGTH_SHORT).show();
			return;
		}
		mCurrentPageNumber -- ;
		initData();
	}
	//下一页
	public void nextPage(View v){
		if(mCurrentPageNumber >= totalPage ){
			Toast.makeText(this, "没有下一页", Toast.LENGTH_SHORT).show();
			return;
		}
		mCurrentPageNumber ++ ;
		initData();
	}
	//跳转
	public void jump(View v){
		String strPageNumber = etPageNumber.getText().toString().trim();
		if(TextUtils.isEmpty(strPageNumber)){
			return;
		}else{
			int number = Integer.parseInt(strPageNumber);
			if(number > 0 && number <= totalPage ){
				mCurrentPageNumber = number;
				initData();
			}else{
				Toast.makeText(this, "请输入正确的页码", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
