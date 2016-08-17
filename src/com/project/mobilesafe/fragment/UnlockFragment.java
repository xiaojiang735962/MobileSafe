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

public class UnlockFragment extends Fragment{

    private View view;
    private TextView tvUnlock;
    private ListView lvUnlock;
    private List<AppInfo> appInfos;
    private AppLockDao appLockDao;
    private List<AppInfo> unlockList;
    private UnlockAdapter adapter;

    //此方法类似Activity中onCreate方法的中的setContentView()
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.item_unlock_fragment, null);
        tvUnlock = (TextView) view.findViewById(R.id.tv_unlock);
        lvUnlock = (ListView) view.findViewById(R.id.lv_unlock);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appInfos = AppInfos.getAppInfos(getActivity());
        //获取程序锁的数据库操作
        appLockDao = new AppLockDao(getActivity());
        //初始化没有加锁的应用集合
        unlockList = new ArrayList<AppInfo>();
        for (AppInfo appInfo : appInfos){
            //判断当前的应用是否在程序锁中
            if(appLockDao.find(appInfo.getApkPackageName())){

            }else{
                //如果不是，说明此应用没有加锁
                unlockList.add(appInfo);
            }
        }
        adapter = new UnlockAdapter();
        lvUnlock.setAdapter(adapter);
    }
    private class UnlockAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            tvUnlock.setText("未加锁软件(" + unlockList.size() + ")");
            return unlockList.size();
        }

        @Override
        public Object getItem(int position) {
            return unlockList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final View view;
            ViewHolder holder ;
            final AppInfo appInfo;
            if(convertView == null){
                view = View.inflate(getActivity(), R.layout.item_unlock, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvAppName = (TextView) view.findViewById(R.id.tv_appName);
                holder.ivLock = (ImageView) view.findViewById(R.id.iv_lock);
                view.setTag(holder);
            }else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            appInfo = unlockList.get(position);
            holder.ivIcon.setImageDrawable(unlockList.get(position).getIcon());
            holder.tvAppName.setText(unlockList.get(position).getApkName());
            //把程序添加到程序锁数据库里
            holder.ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //初始化一个位移动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , 1.0f
                        ,Animation.RELATIVE_TO_SELF , 0 , Animation.RELATIVE_TO_SELF , 0);
                    //设置动画时间
                    translateAnimation.setDuration(1000);
                    //开始动画
                    view.startAnimation(translateAnimation);

                    new Thread(){
                        @Override
                        public void run() {
                            SystemClock.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //添加到数据库中
                                    appLockDao.add(appInfo.getApkPackageName());
                                    //从当前页面移除对象
                                    unlockList.remove(position);
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
        ImageView ivLock;
    }
}
