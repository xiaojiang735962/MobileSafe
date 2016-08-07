package com.project.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by xiaojiang on 8/6/16.
 */
public class AppInfo {
    //程序的图片
    private Drawable icon;
    //程序的名字
    private String apkName;
    //程序的大小
    private long apkSize;
    //判断是用户app还是系统app(true表示用户app,false表示系统app)
    private boolean userApp;
    //判断app放置的位置(true表示手机内存,false表示sd卡)
    private boolean isRom;
    //程序的包名
    private String apkPackageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setRom(boolean rom) {
        isRom = rom;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public void setApkPackageName(String apkPackageName) {
        this.apkPackageName = apkPackageName;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "apkName='" + apkName + '\'' +
                ", apkSize='" + apkSize + '\'' +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", apkPackageName='" + apkPackageName + '\'' +
                '}';
    }
}
