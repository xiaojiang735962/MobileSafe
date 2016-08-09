package com.project.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.MemoryInfo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.project.mobilesafe.R;
import com.project.mobilesafe.bean.AppInfo;
import com.project.mobilesafe.bean.TaskInfo;
import com.project.mobilesafe.engine.TaskInfos;
import com.project.mobilesafe.utils.SharedPreferencesUtils;
import com.project.mobilesafe.utils.SystemInfoUtils;
import com.project.mobilesafe.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
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
public class TaskManagerActivity extends Activity {

    @ViewInject(R.id.tv_process)
    private TextView tvProcess;
    @ViewInject(R.id.tv_memory)
    private TextView tvMemory;
    @ViewInject(R.id.list_view)
    private ListView listView;
    @ViewInject(R.id.tv_taskcount)
    private TextView tvTaskCount;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;
    private TaskManagerAdapter adapter;
    private int size;
    private long availMem;
    private long totalMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        initUI();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                List<TaskInfo> totalTaskInfos = TaskInfos.getTaskInfos(TaskManagerActivity.this);
                //将totalTaskInfos分为(用户进程和系统进程)
                //用户进程集合
                userTaskInfos = new ArrayList<TaskInfo>();
                //系统进程集合
                systemTaskInfos = new ArrayList<TaskInfo>();
                for(TaskInfo taskInfo : totalTaskInfos){
                    if(taskInfo.isUserApp()){
                        userTaskInfos.add(taskInfo);
                    }else{
                        systemTaskInfos.add(taskInfo);
                    }
                }

                //刷新主线程UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new TaskManagerAdapter();
                        listView.setAdapter(adapter);
                    }
                });
            }
        }.start();

    }
    private class TaskManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(SharedPreferencesUtils.getBoolean(TaskManagerActivity.this , "is_show_system" , true)){
                return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
            }else {
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userTaskInfos.size() + 1) {
                return null;
            }
            TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                //减掉多出来的条目
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = 1 + userTaskInfos.size() + 1;
                taskInfo = systemTaskInfos.get(position - location);
            }

            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            //如果当前的position为0,表示用户进程
            if (position == 0) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setText("用户进程(" + userTaskInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(16);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userTaskInfos.size() + 1) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setText("系统进程(" + systemTaskInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(16);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }
            TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                //减掉多出来的条目
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                int location = 1 + userTaskInfos.size() + 1;
                taskInfo = systemTaskInfos.get(position - location);
            }

            View view = null;
            ViewHolder holder;
            if(convertView != null && convertView instanceof LinearLayout){
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            }else{
                view = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                holder = new ViewHolder();
                holder.ivProcessIcon = (ImageView) view.findViewById(R.id.iv_processIcon);
                holder.tvProcessName = (TextView) view.findViewById(R.id.tv_processName);
                holder.tvProcessSize = (TextView) view.findViewById(R.id.tv_processSize);
                holder.cbProcessStatus = (CheckBox) view.findViewById(R.id.cb_processStatus);
                view.setTag(holder);
            }
            //设置属性的值
            holder.ivProcessIcon.setImageDrawable(taskInfo.getIcon());
            holder.tvProcessName.setText(taskInfo.getAppName());
            holder.tvProcessSize.setText("内存占用" + Formatter.formatFileSize(TaskManagerActivity.this,
                    taskInfo.getMemorySize()));
            if(taskInfo.isChecked()){
                holder.cbProcessStatus.setChecked(true);
            }else{
                holder.cbProcessStatus.setChecked(false);
            }
            return view;
        }
    }
    static class ViewHolder{
        ImageView ivProcessIcon;
        TextView tvProcessName;
        TextView tvProcessSize;
        CheckBox cbProcessStatus;
    }

    /**
     * ActivityManager :活动管理器(进程管理器)
     * PackageManager :包管理器
     */
    private void initUI() {
        ViewUtils.inject(this);
        //获取手机中进程的个数
        size = SystemInfoUtils.getProcessCount(this);
        tvProcess.setText("运行中进程：" + size + "个");
        //获取剩余内存
        availMem = SystemInfoUtils.getAvailMem(this);
        //获取总共内存(只有高版本有此变量,低版本需要从porc文件中读取)
        totalMem = SystemInfoUtils.getTotalMem(this);
        //匹配低版本的totalMem
        /* try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String readLine = reader.readLine();
            StringBuffer sb = new StringBuffer();
            for(char c : readLine.toCharArray()){
                if(c >= '0' && c <= '9'){
                    sb.append(c);
                }
            }
            totalMem = Long.parseLong(sb.toString()) * 1024 ;
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        tvMemory.setText("剩余/总内存：" + Formatter.formatFileSize(TaskManagerActivity.this , availMem) + "/" +
                Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

        //设置listView滚动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            /**
             * @param absListView
             * @param firstVisibleItem  第一个可见条目的位置
             * @param visibleItemCount  一页可以展示多少条目
             * @param totalVisibleCount 总共的item个数
             */
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalVisibleCount) {
                if (userTaskInfos != null && systemTaskInfos != null) {
                    if (firstVisibleItem > userTaskInfos.size() + 1) {
                        //系统应用进程
                        tvTaskCount.setText("系统进程(" + systemTaskInfos.size() + ")");
                    } else {
                        //用户应用进程
                        tvTaskCount.setText("用户进程(" + userTaskInfos.size() + ")");
                    }
                }
            }
        });
        //设置listView的条目点击事件,实现CheckBox的勾选
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到当前点击的listView对象
                Object object = listView.getItemAtPosition(position);
                if(object != null && object instanceof TaskInfo){
                    TaskInfo taskInfo = (TaskInfo) object;
                    ViewHolder holder = (ViewHolder) view.getTag();
                    //判断当前的item是否被勾选
                    if(taskInfo.isChecked()){
                        taskInfo.setChecked(false);
                        holder.cbProcessStatus.setChecked(false);
                    }else{
                        taskInfo.setChecked(true);
                        holder.cbProcessStatus.setChecked(true);
                    }
                }
            }
        });
    }
    //全选
    public void selectAll(View v){
        for (TaskInfo taskInfo : userTaskInfos){
            //判断当前的用户进程是不是自己的进程,如果是就跳过
            if(taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(true);
        }
        for (TaskInfo taskInfo : systemTaskInfos){
            taskInfo.setChecked(true);
        }
        //数据改变时,一定要刷新界面
        adapter.notifyDataSetChanged();
    }
    //反选
    public void reverseAll(View v){
        for (TaskInfo taskInfo : userTaskInfos){
            //判断当前的用户进程是不是自己的进程,如果是就跳过
            if(taskInfo.getPackageName().equals(getPackageName())){
                continue;
            }
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        for (TaskInfo taskInfo : systemTaskInfos){
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        //数据改变时,一定要刷新界面
        adapter.notifyDataSetChanged();
    }
    //清理
    public void killProcess(View v){
        List<TaskInfo> killLists = new ArrayList<TaskInfo>();
        //获取进程管理器(用来杀死进程)
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //清理进程的个数
        int totalCount = 0;
        //清理进程的大小
        int totalMemory = 0;
        //当集合迭代时,不能改变集合大小,否则会报异常
        for (TaskInfo taskInfo : userTaskInfos){
            if(taskInfo.isChecked()){
                killLists.add(taskInfo);
                totalCount ++ ;
                totalMemory += taskInfo.getMemorySize();
            }
        }
        for (TaskInfo taskInfo : systemTaskInfos){
            if(taskInfo.isChecked()){
                killLists.add(taskInfo);
                totalCount ++ ;
                totalMemory += taskInfo.getMemorySize();
            }
        }
        for (TaskInfo taskInfo : killLists){
            //判断是否是用户进程
            if(taskInfo.isUserApp()){
                userTaskInfos.remove(taskInfo);
                //杀死进程(参数表示进程包名)
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }else{
                systemTaskInfos.remove(taskInfo);
                //杀死进程(参数表示进程包名)
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }
        ToastUtils.showToast(TaskManagerActivity.this , "共清理" + totalCount + "个进程,释放" +
            Formatter.formatFileSize(TaskManagerActivity.this , totalMemory) + "内存");

        //杀死被选中的进程后，还剩多少进程
        size -= totalCount;
        tvProcess.setText("运行中进程：" + size + "个");
        //杀死被选中的进程后，还剩多少内存
        tvMemory.setText("剩余/总内存：" + Formatter.formatFileSize(TaskManagerActivity.this , availMem + totalMemory) + "/" +
                Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
        //刷新界面
        adapter.notifyDataSetChanged();
    }
    //设置
    public void openSetting(View v){
        Intent intent = new Intent(TaskManagerActivity.this , TaskManagerSettingActivity.class);
        startActivity(intent);
    }
}
