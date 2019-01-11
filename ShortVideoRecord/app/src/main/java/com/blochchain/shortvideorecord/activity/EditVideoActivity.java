package com.blochchain.shortvideorecord.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.response.BaseResponse;
import com.blochchain.shortvideorecord.response.GetVideoInfoResponse;
import com.blochchain.shortvideorecord.response.GetVideoListResponse;
import com.blochchain.shortvideorecord.tabpager.ViewTabBasePager;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class EditVideoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = EditVideoActivity.class.getSimpleName();
    private ImageView iv_back;
    private ImageView iv_pic;
    private TextView tv_play_amount;  //播放量
    private TextView tv_income;  //收益
    private TextView tv_collection;  //收藏
    private TextView tv_delete;  //删除
    private TextView tv_update;  //更新
    private EditText et_describe;
    private String describe;
    private String video_id;
    private static final int GETVIDEOINFOSUCCESS = 0; //获取作品详情
    private static final int GETVIDEOINFOFAILED = 1;
    private static final int EDITVIDEOINFOSUCCESS = 2; //编辑作品
    private static final int EDITVIDEOINFOFAILED = 3;
    private static final int DELVIDEOINFOSUCCESS = 4; //删除作品
    private static final int DELVIDEOINFOFAILED = 5;
    private NetUtils netUtils;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private String self_media_id;
    private MyHandler mHandle = new MyHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETVIDEOINFOSUCCESS:  //获取作品详情成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取作品详情成功：" + str);
                    GetVideoInfoResponse getVideoInfoResponse = gson.fromJson(str,GetVideoInfoResponse.class);
                    if(getVideoInfoResponse != null){
                        if(getVideoInfoResponse.getCode() == 0){
                            setVideoMessage(getVideoInfoResponse);
                        }else {
                            UIUtils.showToastCenter(EditVideoActivity.this,getVideoInfoResponse.getMsg());
                        }
                    }
                    break;
                case GETVIDEOINFOFAILED: //获取作品详情失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取作品详情失败：" + e.getMessage());
                    break;
                case EDITVIDEOINFOSUCCESS:  //编辑作品详情成功
                    String string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "编辑作品详情成功：" + string);
                    BaseResponse baseResponse = gson.fromJson(string,BaseResponse.class);
                    if(baseResponse != null){
                        if(baseResponse.getCode() == 0){
                             setResult(200);
                             finish();
                        }else {
                            UIUtils.showToastCenter(EditVideoActivity.this,baseResponse.getMsg());
                        }
                    }
                    break;
                case EDITVIDEOINFOFAILED: //编辑作品详情失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "编辑作品详情失败：" + e1.getMessage());
                    break;
                case DELVIDEOINFOSUCCESS:  //删除作品详情成功
                    String string1 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "删除作品详情成功：" + string1);
                    BaseResponse baseResponse1 = gson.fromJson(string1,BaseResponse.class);
                    if(baseResponse1 != null){
                        if(baseResponse1.getCode() == 0){
                            setResult(200);
                            finish();
                        }else {
                            UIUtils.showToastCenter(EditVideoActivity.this,baseResponse1.getMsg());
                        }
                    }
                    break;
                case DELVIDEOINFOFAILED: //编辑作品详情失败
                    IOException e2 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "删除作品详情失败：" + e2.getMessage());
                    break;
            }
        }
    };

    private void setVideoMessage(GetVideoInfoResponse getVideoInfoResponse) {
        if(!TextUtils.isEmpty(getVideoInfoResponse.getAttention_amount())){
            tv_collection.setText(getVideoInfoResponse.getAttention_amount());
        }
        if(!TextUtils.isEmpty(getVideoInfoResponse.getIncome_amount())){
            tv_income.setText(getVideoInfoResponse.getIncome_amount());
        }
        if(!TextUtils.isEmpty(getVideoInfoResponse.getPlay_amount())){
            tv_play_amount.setText(getVideoInfoResponse.getPlay_amount());
        }
        if(!TextUtils.isEmpty(getVideoInfoResponse.getVideo_dec())){
            et_describe.setText(getVideoInfoResponse.getVideo_dec());
        }
        if(!TextUtils.isEmpty(getVideoInfoResponse.getVideo_pic())){
            Picasso.with(this).load(getVideoInfoResponse.getVideo_pic()).into(iv_pic);
        }

    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_edit_video, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        iv_pic = findViewById(R.id.iv_pic);
        tv_play_amount = findViewById(R.id.tv_play_amount);
        tv_income = findViewById(R.id.tv_income);
        tv_collection = findViewById(R.id.tv_collection);
        tv_delete = findViewById(R.id.tv_delete);
        tv_update = findViewById(R.id.tv_update);
        et_describe = findViewById(R.id.et_describe);

        iv_back.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        tv_update.setOnClickListener(this);

        initData();
        return rootView;
    }

    private void initData() {
        Intent intent = getIntent();
        video_id = intent.getStringExtra("video_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");

        getVideoInfo();
    }

    private void getVideoInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        if (!TextUtils.isEmpty(video_id)) {
            reqBody.put("video_id", video_id);
        }
        LogUtils.e(TAG+",vide详情参数："+reqBody.toString());
        netUtils.postDataAsynToNet(Constants.GetVideoInfoUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETVIDEOINFOSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETVIDEOINFOFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_delete:
                delVideoInfo();
                break;
            case R.id.tv_update:
                describe = et_describe.getText().toString();
                if (TextUtils.isEmpty(describe)) {
                    UIUtils.showToastCenter(EditVideoActivity.this, "请输入简介");
                    return;
                }
                editVideoInfo();
                break;
        }
    }

    private void delVideoInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        if (!TextUtils.isEmpty(video_id)) {
            reqBody.put("video_id", video_id);
        }
        netUtils.postDataAsynToNet(Constants.DelVideoInfoUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = DELVIDEOINFOSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = DELVIDEOINFOFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }

    private void editVideoInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        if (!TextUtils.isEmpty(video_id)) {
            reqBody.put("video_id", video_id);
        }
        reqBody.put("video_dec", describe);
        netUtils.postDataAsynToNet(Constants.EditVideoInfoUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = DELVIDEOINFOSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = DELVIDEOINFOFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }
}
