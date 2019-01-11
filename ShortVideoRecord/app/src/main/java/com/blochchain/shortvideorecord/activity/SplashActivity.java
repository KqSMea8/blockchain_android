/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.blochchain.shortvideorecord.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;

/**
 * @author Mulberry
 */
public class SplashActivity extends BaseActivity {
    private static final int JUMP_TO_MAIN = 1;
    private static final int SPALASH_DELAY_TIME = 2000;

    private String self_media_id;
    private int type;  // 0 没有登陆， 跳转登陆，    1 已经登陆，跳转主页
    private MyHandler jumpHandler = new MyHandler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case JUMP_TO_MAIN:
                    Intent intent;
                    if(type == 1){
                        intent = new Intent(SplashActivity.this,Main2Activity.class);
                    }else {
                        intent = new Intent(SplashActivity.this,LoginActivity.class);
                        intent.putExtra("fromSplash",true);
                    }
                    UIUtils.startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View rootView = View.inflate(this, R.layout.activity_spalash, null);
        setContentView(rootView);

        self_media_id = SharedPrefrenceUtils.getString(this,"self_media_id");
        if(TextUtils.isEmpty(self_media_id)){
            type = 0;
        }else {
            type = 1;
        }
        setJumpToMain();
        return rootView;
    }

//    private void initData() {
//        AlivcLiveUserManager.getInstance().init(getApplicationContext());
//    }

    private void setJumpToMain(){
        Message msg = jumpHandler.obtainMessage();
        msg.what = JUMP_TO_MAIN;
        jumpHandler.sendMessageDelayed(msg,SPALASH_DELAY_TIME);
    }
}
