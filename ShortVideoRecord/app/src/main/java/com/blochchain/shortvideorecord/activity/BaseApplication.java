/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.blochchain.shortvideorecord.activity;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.aliyun.common.crash.CrashHandler;
import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.aliyun.downloader.DownloaderManager;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;
import java.util.Locale;

/**
 * Created by Mulberry on 2018/2/24.
 */
public class BaseApplication extends Application {
    /** 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了 */
    private static BaseApplication mInstance;
    /** 主线程ID */
    private static int mMainThreadId = -1;
    /** 主线程 */
    private static Thread mMainThread;
    /** 主线程Handler */
    private static Handler mMainThreadHandler;
    /** 主线程Looper */
    private static Looper mMainLooper;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMainThreadId = android.os.Process.myTid();
        mMainThread = Thread.currentThread();
        mMainThreadHandler = new Handler();
        mMainLooper = getMainLooper();
        mInstance = this;

        loadLibs();
        QupaiHttpFinal.getInstance().initOkHttpFinal();
        com.aliyun.vod.common.httpfinal.QupaiHttpFinal.getInstance().initOkHttpFinal();
        //Logger.setDebug(true);
        initDownLoader();
        //        localCrashHandler();
        //        new NativeCrashHandler().registerForNativeCrash(this);


        CrashReport.initCrashReport(getApplicationContext(), "39d40ade73", false);
    }

    private void loadLibs(){
        System.loadLibrary("live-openh264");
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
        System.loadLibrary("FaceAREngine");
        System.loadLibrary("AliFaceAREngine");
    }

    private void initDownLoader() {
        DownloaderManager.getInstance().init(this);
    }

    private void localCrashHandler() {
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());
    }

    public static BaseApplication getApplication() {
        return mInstance;
    }

    /** 获取主线程ID */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /** 获取主线程 */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /** 获取主线程的handler */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /** 获取主线程的looper */
    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

}
