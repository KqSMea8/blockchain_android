package com.blockchain.shortvideoplayer.tabpager;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.AccoountManagerActivity;
import com.blockchain.shortvideoplayer.activity.BuyVIPActivity;
import com.blockchain.shortvideoplayer.activity.HelpCenterActivity;
import com.blockchain.shortvideoplayer.activity.LoginActivity;
import com.blockchain.shortvideoplayer.activity.Main2Activity;
import com.blockchain.shortvideoplayer.activity.MessageActivity;
import com.blockchain.shortvideoplayer.activity.MyInfromationActivity;
import com.blockchain.shortvideoplayer.activity.SubscribeActivity;
import com.blockchain.shortvideoplayer.activity.SystemSettingActivity;
import com.blockchain.shortvideoplayer.activity.VideoPlayActivity;
import com.blockchain.shortvideoplayer.adapter.VideoSearchAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetUserInfoResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.RetrofitUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.NoScrollGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

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
    private ImageView iv_back;
    private ImageView iv_message; //消息
    private ImageView iv_head; //头像
    private LinearLayout ll_setting; //设置
    private TextView tv_nickname;  //昵称
    private ImageView iv_sex;  //性别
    private TextView tv_level; //等级
    private TextView tv_describe; //自我介绍
    private TextView tv_follow;  //关注
    private LinearLayout ll_subscribe;  //订阅类目
    private LinearLayout ll_account;  //账号管理
    private LinearLayout ll_help;  //帮助中心
    private LinearLayout ll_bought_title; //已购买标签
    private TextView tv_bought;
    private ImageView iv_bought;
    private LinearLayout ll_record_title; //观看记录标签
    private TextView tv_record;
    private ImageView iv_record;
    private LinearLayout ll_collection_title; //收藏标签
    private TextView tv_collection;
    private ImageView iv_collection;
    private NoScrollGridView gv_video;
    private VideoSearchAdapter videoSearchAdapter;
    private List<VideoBean> buy_video_list;  //已购买
    private List<VideoBean> play_video_list;  //已购买
    private List<VideoBean> attention_video_list;  //已购买
    private ScrollView sv_total;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final String TAG = TabMinePager.class.getSimpleName();
    private String user_id;
    private GetUserInfoResponse getUserInfoResponse;
    private int type;
    private List<ClassModel> classModelList;
    private AlertDialog recommendDialog;  //推荐类目对话框
    private String selectClassIds = "";
    private TextView tv_norecord;

    private void setUserMessage() {
        LogUtils.e(TAG+"education_id:"+getUserInfoResponse.getEducation_id());
        if(!TextUtils.isEmpty(getUserInfoResponse.getFans_amount())){
            tv_follow.setText(getUserInfoResponse.getFans_amount());
        }
        if(getUserInfoResponse.getIs_vip()==1){
            tv_level.setText("VIP1");
        }else {
            tv_level.setText("会员中心");
        }
        if(!TextUtils.isEmpty(getUserInfoResponse.getNickname())){
            tv_nickname.setText(getUserInfoResponse.getNickname());
        }
        if(!TextUtils.isEmpty(getUserInfoResponse.getHead_pic())){
            SharedPrefrenceUtils.setString(mContext,"head_pic",getUserInfoResponse.getHead_pic());
            Picasso.with(mContext).load(getUserInfoResponse.getHead_pic()).into(iv_head);
        }
        if(!TextUtils.isEmpty(getUserInfoResponse.getSelf_intr())){
            tv_describe.setText(getUserInfoResponse.getSelf_intr());
        }
        if(getUserInfoResponse.getSex() == 0){
            iv_sex.setImageResource(R.mipmap.male);
        }else {
            iv_sex.setImageResource(R.mipmap.female);
        }
        buy_video_list = getUserInfoResponse.getBuy_video_list();
        play_video_list = getUserInfoResponse.getPlay_video_list();
        attention_video_list = getUserInfoResponse.getAttention_video_list();

        if(buy_video_list == null){
            buy_video_list = new ArrayList<>();
        }
        changeTitle(0);
        videoSearchAdapter = new VideoSearchAdapter(mContext,buy_video_list);
        gv_video.setAdapter(videoSearchAdapter);
//        gv_video.setParentScrollView(sv_total);

        int is_first = SharedPrefrenceUtils.getInt(mContext,"is_first",0);
        if(is_first == 1){
            String classModelListStr = SharedPrefrenceUtils.getString(mContext,"classModelListStr");
            if(!TextUtils.isEmpty(classModelListStr)){
                classModelList = gson.fromJson(classModelListStr,new TypeToken<List<ClassModel>>(){}.getType());
                if(classModelList != null && classModelList.size()>0){
                    recommendDialog = DialogUtils.showRecommendAttentionDialog(mContext,classModelList,this);
                }
            }
        }
    }

    public TabMinePager(Context context) {
        super(context);
    }


    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.fragment_mine, null);
        iv_back = view.findViewById(R.id.iv_back);
        iv_message = view.findViewById(R.id.iv_message);
        iv_head = view.findViewById(R.id.iv_head);
        ll_setting = view.findViewById(R.id.ll_setting);
        tv_nickname = view.findViewById(R.id.tv_nickname);
        iv_sex = view.findViewById(R.id.iv_sex);
        tv_level = view.findViewById(R.id.tv_level);
        tv_describe = view.findViewById(R.id.tv_describe);
        tv_follow = view.findViewById(R.id.tv_follow);
        ll_subscribe = view.findViewById(R.id.ll_subscribe);
        ll_account = view.findViewById(R.id.ll_account);
        ll_help = view.findViewById(R.id.ll_help);
        ll_bought_title = view.findViewById(R.id.ll_bought_title);
        tv_bought = view.findViewById(R.id.tv_bought);
        iv_bought = view.findViewById(R.id.iv_bought);
        ll_record_title = view.findViewById(R.id.ll_record_title);
        tv_record = view.findViewById(R.id.tv_record);
        iv_record = view.findViewById(R.id.iv_record);
        ll_collection_title = view.findViewById(R.id.ll_collection_title);
        tv_collection = view.findViewById(R.id.tv_collection);
        iv_collection = view.findViewById(R.id.iv_collection);
        gv_video = view.findViewById(R.id.gv_video);
        sv_total = view.findViewById(R.id.sv_total);
        tv_norecord = view.findViewById(R.id.tv_norecord);

        iv_back.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_subscribe.setOnClickListener(this);
        ll_account.setOnClickListener(this);
        ll_help.setOnClickListener(this);
        ll_bought_title.setOnClickListener(this);
        ll_record_title.setOnClickListener(this);
        ll_collection_title.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        tv_nickname.setOnClickListener(this);
        tv_describe.setOnClickListener(this);
        iv_sex.setOnClickListener(this);
        tv_level.setOnClickListener(this);

        gv_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VideoBean videoBean = null;
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                switch (type){
                    case 0:
                        videoBean = buy_video_list.get(i);
                        intent.putExtra("videoListStr",gson.toJson(buy_video_list));
                        break;
                    case 1:
                        videoBean = play_video_list.get(i);
                        intent.putExtra("videoListStr",gson.toJson(play_video_list));
                        break;
                    case 2:
                        videoBean = attention_video_list.get(i);
                        intent.putExtra("videoListStr",gson.toJson(attention_video_list));
                        break;
                }
                if(videoBean != null){
                    intent.putExtra("video_id",videoBean.getVideo_id());
                    intent.putExtra("postion",i);
                    UIUtils.startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void initData() {
        user_id = SharedPrefrenceUtils.getString(mContext,"usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);

        if(!TextUtils.isEmpty(user_id)){
            getUserInfo();
        }else {
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra("fromMine",true);
            UIUtils.startActivityForResult(intent,Main2Activity.REFRESHMINE);
        }
    }

    private void getUserInfo() {
        Call<GetUserInfoResponse> call = RetrofitUtils.getInstance().getUserInfo(user_id);
        call.enqueue(new Callback<GetUserInfoResponse>() {
            @Override
            public void onResponse(Call<GetUserInfoResponse> call, retrofit2.Response<GetUserInfoResponse> response) {
                LogUtils.e(TAG +"--获取用户信息:"+ response.body());
                getUserInfoResponse = response.body();
                if(getUserInfoResponse != null){
                    if(getUserInfoResponse.getCode() == 0){
                        setUserMessage();
                    }else {
                        UIUtils.showToastCenter(mContext,getUserInfoResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserInfoResponse> call, Throwable t) {
                LogUtils.e(TAG +"--获取用户信息报错"+ t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:  //返回
                break;
            case R.id.iv_message:  //消息
                intent = new Intent(mContext, MessageActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.ll_setting: //设置
                intent = new Intent(mContext, SystemSettingActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.ll_subscribe: //订阅类目
                intent = new Intent(mContext, SubscribeActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.ll_account: //账号管理
                intent = new Intent(mContext, AccoountManagerActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.ll_help: //帮助中心
                intent = new Intent(mContext, HelpCenterActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.ll_bought_title: //已购买
                changeTitle(0);
                setVideoMessage(0);
                type = 0;
                break;
            case R.id.ll_record_title: //观看记录
                changeTitle(1);
                setVideoMessage(1);
                type = 1;
                break;
            case R.id.ll_collection_title: //
                changeTitle(2);
                setVideoMessage(2);
                type = 2;
                break;
            case R.id.iv_head:
            case R.id.tv_nickname:
            case R.id.iv_sex:
            case R.id.tv_describe:
                intent = new Intent(mContext, MyInfromationActivity.class);
                intent.putExtra("nickname",getUserInfoResponse.getNickname());
                intent.putExtra("describe",getUserInfoResponse.getSelf_intr());
                intent.putExtra("headpic",getUserInfoResponse.getHead_pic());
                intent.putExtra("sex",getUserInfoResponse.getSex());
                intent.putExtra("birthday",getUserInfoResponse.getBirthday());
                intent.putExtra("education_id",getUserInfoResponse.getEducation_id());
                UIUtils.startActivityForResult(intent, Main2Activity.REFRESHMINE);
                break;
            case R.id.tv_level:  //vip
                if(getUserInfoResponse.getIs_vip()==0){
                    intent = new Intent(mContext, BuyVIPActivity.class);
                    UIUtils.startActivity(intent);
                }
                break;
            case R.id.btn_pass:
                recommendDialog.dismiss();
                SharedPrefrenceUtils.setInt(mContext,"is_first",0);
                break;
            case R.id.btn_submit:
                selectClassIds = "";
                if(classModelList != null && classModelList.size()>0){
                    for(ClassModel classModel : classModelList){
                        if(classModel.isSelected()){
                            selectClassIds = selectClassIds + ","+classModel.getClass_id();
                        }
                    }
                }
                if(selectClassIds.length()>0){
                    selectClassIds = selectClassIds.substring(1);
                    subscribClass();
                }else {
                    recommendDialog.dismiss();
                }
                break;
        }
    }

    private void subscribClass(){
        Call<BaseResponse> call = RetrofitUtils.getInstance().subscribeClass(user_id,selectClassIds);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                LogUtils.e(TAG +"--订阅该类目:"+ response.body());
                BaseResponse baseResponse = response.body();
                if(baseResponse != null){
                    if(baseResponse.getCode() == 0){
                        recommendDialog.dismiss();
                        SharedPrefrenceUtils.setInt(mContext,"is_first",0);
                    }else {
                        UIUtils.showToastCenter(mContext,baseResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                LogUtils.e(TAG +"--订阅该类目报错"+ t.getMessage());
            }
        });
    }
    private void setVideoMessage(int i) {
        switch (i){
            case 0:
                if(buy_video_list == null){
                    buy_video_list = new ArrayList<>();
                }
                if(buy_video_list.size()>0){
                    videoSearchAdapter = new VideoSearchAdapter(mContext,buy_video_list);
                    gv_video.setAdapter(videoSearchAdapter);
//                gv_video.setParentScrollView(sv_total);
                    gv_video.setVisibility(View.VISIBLE);
                    tv_norecord.setVisibility(View.GONE);
                }else {
                    gv_video.setVisibility(View.GONE);
                    tv_norecord.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if(play_video_list == null){
                    play_video_list = new ArrayList<>();
                }
                if(play_video_list.size()>0){
                    videoSearchAdapter = new VideoSearchAdapter(mContext,play_video_list);
                    gv_video.setAdapter(videoSearchAdapter);
//                gv_video.setParentScrollView(sv_total);
                    gv_video.setVisibility(View.VISIBLE);
                    tv_norecord.setVisibility(View.GONE);
                }else {
                    gv_video.setVisibility(View.GONE);
                    tv_norecord.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if(attention_video_list == null){
                    attention_video_list = new ArrayList<>();
                }
                if(attention_video_list.size()>0){
                    videoSearchAdapter = new VideoSearchAdapter(mContext,attention_video_list);
                    gv_video.setAdapter(videoSearchAdapter);
//                gv_video.setParentScrollView(sv_total);
                    gv_video.setVisibility(View.VISIBLE);
                    tv_norecord.setVisibility(View.GONE);
                }else {
                    gv_video.setVisibility(View.GONE);
                    tv_norecord.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void changeTitle(int i) {
//        TextPaint paint = tv_bought.getPaint();
//        TextPaint paint1 = tv_record.getPaint();
//        TextPaint paint2 = tv_collection.getPaint();
            switch (i) {
                case 0:
                    tv_bought.setTextColor(UIUtils.getColor(R.color.background_blue));
                    tv_record.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                    tv_collection.setTextColor(UIUtils.getColor(R.color.text_color_gray));
//                    paint.setFakeBoldText(true);
//                    paint1.setFakeBoldText(false);
//                    paint2.setFakeBoldText(false);
                    iv_bought.setImageResource(R.mipmap.bottom_blue);
                    iv_record.setImageResource(R.mipmap.bottom_white);
                    iv_collection.setImageResource(R.mipmap.bottom_white);
                    break;
                case 1:
                    tv_bought.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                    tv_record.setTextColor(UIUtils.getColor(R.color.background_blue));
                    tv_collection.setTextColor(UIUtils.getColor(R.color.text_color_gray));
//                    paint.setFakeBoldText(false);
//                    paint1.setFakeBoldText(true);
//                    paint2.setFakeBoldText(false);
                    iv_bought.setImageResource(R.mipmap.bottom_white);
                    iv_record.setImageResource(R.mipmap.bottom_blue);
                    iv_collection.setImageResource(R.mipmap.bottom_white);
                    break;
                case 2:
                    tv_bought.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                    tv_record.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                    tv_collection.setTextColor(UIUtils.getColor(R.color.background_blue));
//                    paint.setFakeBoldText(false);
//                    paint1.setFakeBoldText(false);
//                    paint2.setFakeBoldText(true);
                    iv_bought.setImageResource(R.mipmap.bottom_white);
                    iv_record.setImageResource(R.mipmap.bottom_white);
                    iv_collection.setImageResource(R.mipmap.bottom_blue);
                    break;
            }
        }
    }