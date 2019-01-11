package com.blochchain.shortvideorecord.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blochchain.shortvideorecord.activity.FansPortraitActivity;
import com.blochchain.shortvideorecord.activity.LoginActivity;
import com.blochchain.shortvideorecord.activity.MessageActivity;
import com.blochchain.shortvideorecord.activity.PlayDataActivity;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.response.GetFirstInfoResponse;
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


/**
 * @作者: 刘敏
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 详情页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class TabMainPager extends ViewTabBasePager implements View.OnClickListener{
    private static final String TAG = TabMainPager.class.getSimpleName();
    private RelativeLayout rl_fans_total;  //粉丝数
    private TextView tv_fans_total;
    private RelativeLayout rl_fans_yesterday; //昨日新增
    private TextView tv_fans_yesterday;
    private RelativeLayout rl_play_amout;  //播放量
    private TextView tv_play_amount;
    private RelativeLayout rl_play_yesterday;  //昨日播放
    private TextView tv_play_yesterday;
    private TextView tv_name;
    private ImageView iv_head;
    private ImageView iv_message;

    private static final int GETMAININFOSUCCESS = 0;
    private static final int GETMAININFOFAILED = 1;
    private NetUtils netUtils;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private String self_media_id;
    private MyTabHandler mHandle = new MyTabHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETMAININFOSUCCESS:  //获取主页信息成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "成功：" + str);
                    GetFirstInfoResponse getFirstInfoResponse = gson.fromJson(str,GetFirstInfoResponse.class);
                    if(getFirstInfoResponse != null){
                        if(getFirstInfoResponse.getCode() == 0){
                            if(!TextUtils.isEmpty(getFirstInfoResponse.getFans_amount())){
                                tv_fans_total.setText(getFirstInfoResponse.getFans_amount());
                            }
                            if(!TextUtils.isEmpty(getFirstInfoResponse.getFans_yesterday())){
                                tv_fans_yesterday.setText(getFirstInfoResponse.getFans_yesterday());
                            }
                            if(!TextUtils.isEmpty(getFirstInfoResponse.getPlay_amount())){
                                tv_play_amount.setText(getFirstInfoResponse.getPlay_amount());
                            }
                            if(!TextUtils.isEmpty(getFirstInfoResponse.getPlay_yesterday())){
                                tv_play_yesterday.setText(getFirstInfoResponse.getPlay_yesterday());
                            }
                            if(!TextUtils.isEmpty(getFirstInfoResponse.getHead_pic())){
                                Picasso.with(mContext).load(getFirstInfoResponse.getHead_pic()).into(iv_head);
                            }
                            if(!TextUtils.isEmpty(getFirstInfoResponse.getNickname())){
                                tv_name.setText(getFirstInfoResponse.getNickname());
                            }
                        }
                    }
                    break;
                case GETMAININFOFAILED: //获取主页信息失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    break;
            }
        }
    };
    public TabMainPager(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.pager_main, null);
        iv_head = view.findViewById(R.id.iv_head);
        tv_name = view.findViewById(R.id.tv_name);
        rl_fans_total = view.findViewById(R.id.rl_fans_total);
        tv_fans_total = view.findViewById(R.id.tv_fans_total);
        rl_fans_yesterday = view.findViewById(R.id.rl_fans_yesterday);
        tv_fans_yesterday = view.findViewById(R.id.tv_fans_yesterday);
        rl_play_amout = view.findViewById(R.id.rl_play_amout);
        tv_play_amount = view.findViewById(R.id.tv_play_amount);
        rl_play_yesterday = view.findViewById(R.id.rl_play_yesterday);
        tv_play_yesterday = view.findViewById(R.id.tv_play_yesterday);
        iv_message = view.findViewById(R.id.iv_message);

        rl_fans_total.setOnClickListener(this);
        rl_fans_yesterday.setOnClickListener(this);
        rl_play_amout.setOnClickListener(this);
        rl_play_yesterday.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        return view;
    }

    @Override
    public void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        self_media_id = SharedPrefrenceUtils.getString(mContext,"self_media_id");
        getMainInfo();
    }

    private void getMainInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(self_media_id)){
            reqBody.put("self_media_id",self_media_id);
        }
        netUtils.postDataAsynToNet(Constants.GetFirstInfUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETMAININFOSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETMAININFOFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.rl_fans_total:  //粉丝数
                intent = new Intent(mContext, FansPortraitActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.rl_fans_yesterday: //昨日新增
                intent = new Intent(mContext, FansPortraitActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.rl_play_amout:  //播放量
                intent = new Intent(mContext, PlayDataActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.rl_play_yesterday:  //昨日播放
                intent = new Intent(mContext, PlayDataActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.iv_message: //留言
                intent = new Intent(mContext, MessageActivity.class);
                UIUtils.startActivity(intent);
                break;
        }
    }
}