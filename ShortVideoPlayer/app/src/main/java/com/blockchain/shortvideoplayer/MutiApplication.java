package com.blockchain.shortvideoplayer;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.VcPlayerLog;
import com.blockchain.shortvideoplayer.wxapi.WeChatPayCallback;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Mulberry on 2018/2/24.
 */
public class MutiApplication extends Application {
    /** 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了 */
    private static MutiApplication mInstance;
    /** 主线程ID */
    private static int mMainThreadId = -1;
    /** 主线程 */
    private static Thread mMainThread;
    /** 主线程Handler */
    private static Handler mMainThreadHandler;
    /** 主线程Looper */
    private static Looper mMainLooper;

    public WeChatPayCallback weChatPayCallback;

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


        Picasso.Builder pb = new Picasso.Builder(this);
        Picasso picasso = pb.build();
        Picasso.setSingletonInstance(picasso);

        VcPlayerLog.enableLog();
//        initLeakcanary();//初始化内存检测

        //初始化播放器
        AliVcMediaPlayer.init(getApplicationContext());

        ////设置保存密码。此密码如果更换，则之前保存的视频无法播放
        //AliyunDownloadConfig config = new AliyunDownloadConfig();
        //config.setSecretImagePath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/aliyun/encryptedApp.dat");
        ////        config.setDownloadPassword("123456789");
        ////设置保存路径。请确保有SD卡访问权限。
        //config.setDownloadDir(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test_save/");
        ////设置同时下载个数
        //config.setMaxNums(2);

        //AliyunDownloadManager.getInstance(this).setDownloadConfig(config);

//        MyExceptionHandler.create(this);

        CrashReport.initCrashReport(getApplicationContext(), "5558937de3", false);
    }

    public static MutiApplication getApplication() {
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

    private void initLeakcanary() {
        LeakCanary.install(this);
    }

}
