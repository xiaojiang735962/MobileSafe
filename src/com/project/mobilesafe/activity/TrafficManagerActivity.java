package com.project.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.project.mobilesafe.R;
import com.project.mobilesafe.bean.TrafficInfo;
import com.project.mobilesafe.engine.TrafficInfos;

import java.util.List;

public class TrafficManagerActivity extends Activity {

    private ListView listView;
    private PackageManager packageManager;
    private List<TrafficInfo> trafficInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);
        initUI();
    }

    private void initUI() {
        listView = (ListView) findViewById(R.id.list_view);
        packageManager = getPackageManager();
        new Thread(){
            @Override
            public void run() {
                //获取所以安装在手机上的应用程序
                trafficInfos = TrafficInfos.getTrafficInfos(TrafficManagerActivity.this);
                handler.sendEmptyMessage(0);
            }
        }.start();

    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            TrafficAdapter adapter = new TrafficAdapter();
            listView.setAdapter(adapter);
        }
    };

    private class TrafficAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return trafficInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return trafficInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = null ;
            ViewHolder holder ;
            if(convertView == null){
                view = View.inflate(TrafficManagerActivity.this, R.layout.item_traffic_total, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvAppName = (TextView) view.findViewById(R.id.tv_appName);
                holder.tvUpdate = (TextView) view.findViewById(R.id.tv_update);
                holder.tvDownload = (TextView) view.findViewById(R.id.tv_download);
                holder.tvTotal = (TextView) view.findViewById(R.id.tv_total);
                view.setTag(holder);
            }else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            TrafficInfo trafficInfo = trafficInfos.get(position);
            holder.ivIcon.setImageDrawable(trafficInfo.getIcon());
            holder.tvAppName.setText(trafficInfo.getAppName());
            holder.tvUpdate.setText("上传  " + Formatter.formatFileSize(TrafficManagerActivity.this ,trafficInfo.getUpdate()));
            holder.tvDownload.setText("下载  " + Formatter.formatFileSize(TrafficManagerActivity.this , trafficInfo.getDownload()));
            holder.tvTotal.setText(Formatter.formatFileSize(TrafficManagerActivity.this , trafficInfo.getTotal()));
            return view;
        }
    }
    static class ViewHolder{
        ImageView ivIcon;
        TextView tvAppName;
        TextView tvUpdate;
        TextView tvDownload;
        TextView tvTotal;
    }

}
