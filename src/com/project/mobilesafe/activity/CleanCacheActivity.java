package com.project.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.project.mobilesafe.R;
import com.project.mobilesafe.utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CleanCacheActivity extends Activity {

    private PackageManager packageManager;
    private List<CacheInfo> cacheInfos;
    private ListView icList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        initUI();
    }

    private void initUI() {
        icList = (ListView) findViewById(R.id.ic_list);
        //获取包管理器
        packageManager = getPackageManager();
        cacheInfos = new ArrayList<CacheInfo>();
        new Thread(){
            @Override
            public void run() {
                //获取安装在手机上的所有应用
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                for (PackageInfo packageInfo : installedPackages){
                    getCacheSize(packageInfo);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            CacheAdapter adapter = new CacheAdapter();
            icList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    };

    private class CacheAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return cacheInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            View view = null;
            ViewHolder holder;
            if(convertView == null){
                view = View.inflate(CleanCacheActivity.this, R.layout.item_clean_cache, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tv_name);
                holder.tvClean = (TextView) view.findViewById(R.id.tv_clean);
                holder.ivClean = (ImageView) view.findViewById(R.id.iv_clean);
                view.setTag(holder);
            }else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.ivIcon.setImageDrawable(cacheInfos.get(position).icon);
            holder.tvName.setText(cacheInfos.get(position).appName);
            holder.tvClean.setText("缓存大小:" + Formatter.formatFileSize(CleanCacheActivity.this , cacheInfos.get(position).size));
            holder.ivClean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailIntent = new Intent();
                    detailIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    detailIntent.setData(Uri.parse("package:" + cacheInfos.get(position).packageName));
                    startActivity(detailIntent);
                }
            });
            return view;
        }
    }

    static class ViewHolder{
        ImageView ivIcon;
        TextView tvName;
        TextView tvClean;
        ImageView ivClean;
    }

    //获取缓存大小
    private void getCacheSize(PackageInfo packageInfo) {
        try {
            //Class<?> clazz = getClassLoader().loadClass("packageManager");
            //通过反射获取当前方法
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            //参数一:表示调用这个方法的类名  参数二:表示包名
            method.invoke(packageManager ,packageInfo.applicationInfo.packageName,new MyIPackageStatsObserver(packageInfo) );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub{
        private PackageInfo packageInfo;
        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取当前手机应用的缓存大小
            long cacheSize = pStats.cacheSize;
            //如果当前的缓存大小大于0，说明有缓存
            if(cacheSize > 0){
                System.out.println("当前应用名字:" + packageInfo.applicationInfo.loadLabel(packageManager) +
                        "\n缓存大小:" + Formatter.formatFileSize(CleanCacheActivity.this , cacheSize));
                CacheInfo cacheInfo = new CacheInfo();
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                cacheInfo.icon = icon;
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                cacheInfo.appName = appName;
                cacheInfo.size = cacheSize;
                String packageName = packageInfo.applicationInfo.packageName;
                cacheInfo.packageName = packageName;
                cacheInfos.add(cacheInfo);
            }
        }
    }
    static class CacheInfo{
        Drawable icon;
        String appName;
        long size;
        String packageName;
    }
    public void cleanAll(View v){
        //获取当前应用程序里面的所以方法
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods){
            //判断当前的方法名
            if(method.getName().equals("freeStorageAndNotify")){
                try {
                    method.invoke(packageManager , Integer.MAX_VALUE , new MyIPackageDataObserver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ToastUtils.showToast(CleanCacheActivity.this , "全部清除");
    }
    private class MyIPackageDataObserver extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
        }
    }
}
