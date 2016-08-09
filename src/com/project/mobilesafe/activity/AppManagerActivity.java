package com.project.mobilesafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.project.mobilesafe.R;
import com.project.mobilesafe.bean.AppInfo;
import com.project.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.ic_list)
    private ListView icList;
    @ViewInject(R.id.tv_rom)
    private TextView tvRom;
    @ViewInject(R.id.tv_sdcard)
    private TextView tvSD;
    @ViewInject(R.id.tv_appcount)
    private TextView tvAppCount;

    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initUI();
        initData();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //卸载
            case R.id.ll_uninstall:
                Intent uninstallIntent = new Intent();
                uninstallIntent.setAction("android.intent.action.DELETE");
                uninstallIntent.addCategory("android.intent.category.DEFAULT");
                uninstallIntent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                this.startActivity(uninstallIntent);
//                Intent uninstallIntent = new Intent("android.intent.action.DELETE",
//                        Uri.parse("package:" + clickAppInfo.getApkPackageName()));
//                startActivity(uninstallIntent);
                popupWindowDismiss();
                break;
            //启动
            case R.id.ll_start:
                Intent startIntent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
                this.startActivity(startIntent);
                popupWindowDismiss();
                break;
            //分享
            case R.id.ll_share:
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT","f分享");
                shareIntent.putExtra("android.intent.extra.TEXT","Hi! 推荐你使用软件：" + clickAppInfo.getApkName() +
                    "下载地址:" + "https://play.google.com/store/apps/details?id=" + clickAppInfo.getApkPackageName());
                this.startActivity(Intent.createChooser(shareIntent,"分享"));
                popupWindowDismiss();
                break;
            //详情
            case R.id.ll_detail:
                Intent detailIntent = new Intent();
                detailIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
                detailIntent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                this.startActivity(detailIntent);
                popupWindowDismiss();
                break;
        }
    }


    private class AppManagerAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            }
            AppInfo appInfo;
            if (position < userAppInfos.size() + 1) {
                //减掉多出来的条目
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = 1 + userAppInfos.size() + 1;
                appInfo = systemAppInfos.get(position - location);
            }

            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            //如果当前的position为0,表示应用程序
            if (position == 0) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("用户程序(" + userAppInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(16);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userAppInfos.size() + 1) {
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("系统程序(" + systemAppInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(16);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }
            AppInfo appInfo;
            if (position < userAppInfos.size() + 1) {
                //减掉多出来的条目
                appInfo = userAppInfos.get(position - 1);
            } else {
                int location = 1 + userAppInfos.size() + 1;
                appInfo = systemAppInfos.get(position - location);
            }

            View view = null;
            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvApkName = (TextView) view.findViewById(R.id.tv_apkname);
                holder.tvLocation = (TextView) view.findViewById(R.id.tv_location);
                holder.tvApkSize = (TextView) view.findViewById(R.id.tv_apksize);
                view.setTag(holder);
            }

            holder.ivIcon.setBackground(appInfo.getIcon());
            holder.tvApkName.setText(appInfo.getApkName());
            holder.tvApkSize.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));
            if (appInfo.isRom()) {
                holder.tvLocation.setText("手机内存");
            } else {
                holder.tvLocation.setText("外部内存");
            }
            return view;
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvApkName;
        TextView tvLocation;
        TextView tvApkSize;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AppManagerAdapter adapter = new AppManagerAdapter();
            icList.setAdapter(adapter);
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //获取所以安装在手机上的应用程序
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
                //将appInfos分为(用户程序和系统程序)
                //用户程序集合
                userAppInfos = new ArrayList<AppInfo>();
                //系统程序集合
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : appInfos) {
                    //用户程序
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        ViewUtils.inject(this);
        //获取到ROM内存的运行的剩余空间
        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        //获取到SD卡的剩余空间
        long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        //格式化获取内容的格式
        tvRom.setText("内存可用:" + Formatter.formatFileSize(this, romFreeSpace));
        tvSD.setText("SD卡可用:" + Formatter.formatFileSize(this, sdFreeSpace));

        //注册卸载的广播接收者
       /* receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);*/

        //设置icList滚动监听
        icList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            /**
             *
             * @param absListView
             * @param firstVisibleItem  第一个可见条目的位置
             * @param visibleItemCount  一页可以展示多少条目
             * @param totalVisibleCount 总共的item个数
             */
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalVisibleCount) {
                //滑动时关掉对话框
                popupWindowDismiss();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size() + 1) {
                        //系统应用程序
                        tvAppCount.setText("系统程序(" + systemAppInfos.size() + ")");
                    } else {
                        //用户应用程序
                        tvAppCount.setText("用户程序(" + userAppInfos.size() + ")");
                    }
                }
            }
        });
        icList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //获取当前点击的item对象
                Object obj = icList.getItemAtPosition(position);
                if (obj != null && obj instanceof AppInfo) {
                    View convertView = View.inflate(AppManagerActivity.this, R.layout.dialog_popup, null);

                    clickAppInfo = (AppInfo) obj;
                    //获取PopupWindow中的对象,设置点击事件，实现方法
                    LinearLayout llUninstall = (LinearLayout) convertView.findViewById(R.id.ll_uninstall);
                    LinearLayout llShare = (LinearLayout) convertView.findViewById(R.id.ll_share);
                    LinearLayout llStart = (LinearLayout) convertView.findViewById(R.id.ll_start);
                    LinearLayout llDetail = (LinearLayout) convertView.findViewById(R.id.ll_detail);
                    //设置点击事件
                    llUninstall.setOnClickListener(AppManagerActivity.this);
                    llShare.setOnClickListener(AppManagerActivity.this);
                    llStart.setOnClickListener(AppManagerActivity.this);
                    llDetail.setOnClickListener(AppManagerActivity.this);

                    popupWindowDismiss();
                    //-2表示包裹内容
                    popupWindow = new PopupWindow(convertView, -2, -2);
                    //注意：使用PopupWindow时必须设置背景,否则无法显示动画效果
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    int[] location = new int[2];
                    //获取Dialog显示在屏幕的坐标的位置
                    view.getLocationInWindow(location);
                    popupWindow.showAtLocation(adapterView, Gravity.LEFT + Gravity.TOP, 70, location[1]);
                    //给对话框设置缩放动画
                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1, 0.5f, 1, Animation.RELATIVE_TO_SELF,
                            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(500);
                    convertView.startAnimation(scaleAnimation);
                }
            }
        });


    }

    /*private class UninstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接收到卸载的广播");
        }
    }*/

    private void popupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
