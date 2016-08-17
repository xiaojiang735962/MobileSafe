package com.project.mobilesafe.fragment;/*
       Created by xiaojiang on 8/11/16.
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.project.mobilesafe.R;
import com.project.mobilesafe.bean.AppInfo;
import com.project.mobilesafe.db.dao.AppLockDao;
import com.project.mobilesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class LockFragment extends Fragment{

    private TextView tvLock;
    private ListView lvLock;
    private List<AppInfo> appInfos;
    private AppLockDao appLockDao;
    private ArrayList<AppInfo> lockList;
    private LockAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_lock_fragment, null);
        tvLock = (TextView) view.findViewById(R.id.tv_lock);
        lvLock = (ListView) view.findViewById(R.id.lv_lock);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appInfos = AppInfos.getAppInfos(getActivity());
        //获取程序锁的数据库操作
        appLockDao = new AppLockDao(getActivity());
        //初始化加锁的应用集合
        lockList = new ArrayList<AppInfo>();
        for (AppInfo appInfo : appInfos){
            if(appLockDao.find(appInfo.getApkPackageName())){
                //说明此应用已加锁
                lockList.add(appInfo);
            }
        }

        adapter = new LockAdapter();
        lvLock.setAdapter(adapter);
    }
    private class LockAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            tvLock.setText("已加锁软件(" + lockList.size() + ")");
            return lockList.size();
        }

        @Override
        public Object getItem(int position) {
            return lockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final View view ;
            ViewHolder holder ;
            if(convertView == null){
                view = View.inflate(getActivity(), R.layout.item_lock, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvAppName = (TextView) view.findViewById(R.id.tv_appName);
                holder.ivUnlock = (ImageView) view.findViewById(R.id.iv_unlock);
                view.setTag(holder);
            }else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            final AppInfo appInfo = lockList.get(position);
            holder.ivIcon.setImageDrawable(appInfo.getIcon());
            holder.tvAppName.setText(appInfo.getApkName());
            holder.ivUnlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF , 0 ,
                            Animation.RELATIVE_TO_SELF , -1.0f , Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , 0);
                    translateAnimation.setDuration(1000);
                    view.startAnimation(translateAnimation);

                    new Thread(){
                        @Override
                        public void run() {
                            SystemClock.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //移除数据库中的数据
                                    appLockDao.delete(appInfo.getApkPackageName());
                                    //从当前页面移除对象
                                    lockList.remove(position);
                                    //刷新界面
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();
                }
            });
            return view;
        }
    }
    static class ViewHolder{
        ImageView ivIcon;
        TextView tvAppName;
        ImageView ivUnlock;
    }
}
