package com.blochchain.shortvideorecord.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blochchain.shortvideorecord.activity.BaseActivity;
import com.blochchain.shortvideorecord.activity.HelpCenterActivity;
import com.blochchain.shortvideorecord.activity.LoginActivity;
import com.blochchain.shortvideorecord.activity.Main2Activity;
import com.blochchain.shortvideorecord.activity.MediaAuthenticationActivity;
import com.blochchain.shortvideorecord.activity.MessageActivity;
import com.blochchain.shortvideorecord.activity.MessageDetailActivity;
import com.blochchain.shortvideorecord.activity.MyInfromationActivity;
import com.blochchain.shortvideorecord.activity.SystemSettingActivity;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.response.BaseResponse;
import com.blochchain.shortvideorecord.response.GetMsgRecordResponse;
import com.blochchain.shortvideorecord.response.GetSelfMediaInfResponse;
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
public class TabMinePager extends ViewTabBasePager implements View.OnClickListener{
    private static final String TAG = TabMinePager.class.getSimpleName();
    private ImageView iv_back;
    private LinearLayout ll_message;  //消息
    private ImageView iv_head;  //头像
    private LinearLayout ll_setting;  //设置
    private TextView tv_nickname;  //昵称
    private ImageView iv_sex;  //性别
    private TextView tv_level; //级别
    private TextView tv_describe; //自我介绍
    private TextView tv_fans; //粉丝
    private TextView tv_collection; //收藏
    private TextView tv_praise; //被赞
    private LinearLayout ll_media_authentication;  //媒体认证
    private LinearLayout ll_help_center;  //帮助中心

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private String self_media_id;
    private GetSelfMediaInfResponse getSelfMediaInfResponse;

    private static final int GETINFROSUCCESS = 0;  //获取自媒体个人信息
    private static final int GETINFOFAILED = 1;
    private MyTabHandler mHandller = new MyTabHandler(TabMinePager.this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GETINFROSUCCESS:  //发送消息成功
                    String  string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取自媒体个人信息成功："+string);
                    getSelfMediaInfResponse = gson.fromJson(string,GetSelfMediaInfResponse.class);
                    if(getSelfMediaInfResponse != null){
                        if(getSelfMediaInfResponse.getCode() == 0){
                            setMessage();
                        }else {
                            UIUtils.showToastCenter(mContext,getSelfMediaInfResponse.getMsg());
                        }
                    }
                    break;
                case GETINFOFAILED: //发送失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取自媒体个人信息失败:"+e.getMessage());
                    UIUtils.showToastCenter(mContext,e.getMessage());
                    break;
            }
        }
    };

    private void setMessage() {
        if(!TextUtils.isEmpty(getSelfMediaInfResponse.getAttention_amount())){
            tv_collection.setText(getSelfMediaInfResponse.getAttention_amount());
        }
        if(!TextUtils.isEmpty(getSelfMediaInfResponse.getFans_amount())){
            tv_fans.setText(getSelfMediaInfResponse.getFans_amount());
        }
        if(!TextUtils.isEmpty(getSelfMediaInfResponse.getHead_pic())){
            Picasso.with(mContext).load(getSelfMediaInfResponse.getHead_pic()).into(iv_head);
        }
        if(!TextUtils.isEmpty(getSelfMediaInfResponse.getNickname())){
            tv_nickname.setText(getSelfMediaInfResponse.getNickname());
        }
        if(!TextUtils.isEmpty(getSelfMediaInfResponse.getPraised_amount())){
            tv_praise.setText(getSelfMediaInfResponse.getPraised_amount());
        }
        if(!TextUtils.isEmpty(getSelfMediaInfResponse.getSelf_intr())){
            tv_describe.setText(getSelfMediaInfResponse.getSelf_intr());
        }
        if(getSelfMediaInfResponse.getSex() == 0){
            iv_sex.setImageResource(R.mipmap.male);
        }else {
            iv_sex.setImageResource(R.mipmap.female);
        }
    }

    public TabMinePager(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.pager_mine, null);

        iv_back = view.findViewById(R.id.iv_back);
        ll_message = view.findViewById(R.id.ll_message);
        iv_head = view.findViewById(R.id.iv_head);
        ll_setting = view.findViewById(R.id.ll_setting);
        tv_nickname = view.findViewById(R.id.tv_nickname);
        iv_sex = view.findViewById(R.id.iv_sex);
        tv_level = view.findViewById(R.id.tv_level);
        tv_describe = view.findViewById(R.id.tv_describe);
        tv_fans = view.findViewById(R.id.tv_fans);
        tv_collection = view.findViewById(R.id.tv_collection);
        tv_praise = view.findViewById(R.id.tv_praise);
        ll_media_authentication = view.findViewById(R.id.ll_media_authentication);
        ll_help_center = view.findViewById(R.id.ll_help_center);

        iv_back.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_media_authentication.setOnClickListener(this);
        ll_help_center.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        tv_nickname.setOnClickListener(this);
        tv_describe.setOnClickListener(this);
        return view;
    }

    @Override
    public void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        self_media_id = SharedPrefrenceUtils.getString(mContext, "self_media_id");

        if(TextUtils.isEmpty(self_media_id)){
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra("fromMine",true);
            UIUtils.startActivityForResult(intent,Main2Activity.TOLOGIN);
        }else {
            getSelfMediaInf();
        }
    }

    private void getSelfMediaInf(){
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("self_media_id", self_media_id);
        netUtils.postDataAsynToNet(Constants.GetSelfMediaInfUrl,reqBody ,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETINFROSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETINFOFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.iv_back:
                break;
            case R.id.ll_message: //消息
                intent = new Intent(mContext, MessageActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.ll_setting: //设置
                intent = new Intent(mContext, SystemSettingActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.ll_media_authentication:  //媒体认证
                intent = new Intent(mContext, MediaAuthenticationActivity.class);
                intent.putExtra("real_name",getSelfMediaInfResponse.getReal_name());
                intent.putExtra("id_card",getSelfMediaInfResponse.getId_card());
                intent.putExtra("id_card_pic",getSelfMediaInfResponse.getId_card_pic());
                intent.putExtra("id_card_hold_pic",getSelfMediaInfResponse.getId_card_hold_pic());
                UIUtils.startActivityForResult(intent, Main2Activity.REFRESHMINE);
                break;
            case R.id.ll_help_center: //帮助中心
                intent = new Intent(mContext, HelpCenterActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.iv_head:
            case R.id.tv_nickname:
            case R.id.tv_describe:
                intent = new Intent(mContext, MyInfromationActivity.class);
                intent.putExtra("nick_name",getSelfMediaInfResponse.getNickname());
                intent.putExtra("head_pic",getSelfMediaInfResponse.getHead_pic());
                intent.putExtra("self_intr",getSelfMediaInfResponse.getSelf_intr());
                intent.putExtra("bank_card",getSelfMediaInfResponse.getBank_card());
                intent.putExtra("class_id",getSelfMediaInfResponse.getClass_id());
                intent.putExtra("class_name",getSelfMediaInfResponse.getClass_name());
                intent.putExtra("bank_no",getSelfMediaInfResponse.getBank_card());
                intent.putExtra("bank_name",getSelfMediaInfResponse.getBank_name());
                UIUtils.startActivityForResult(intent, Main2Activity.REFRESHMINE);
                break;
        }
    }
}