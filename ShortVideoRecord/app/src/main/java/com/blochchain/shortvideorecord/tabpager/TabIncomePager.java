package com.blochchain.shortvideorecord.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.blochchain.shortvideorecord.activity.IncomeDetailActivity;
import com.blochchain.shortvideorecord.adapter.MyIncomeAdapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.model.MyIncomeModel;
import com.blochchain.shortvideorecord.response.GetMyIncomeResponse;
import com.blochchain.shortvideorecord.response.GetVideoListResponse;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class TabIncomePager extends ViewTabBasePager implements View.OnClickListener{
    private static final String TAG = TabIncomePager.class.getSimpleName();
    private TextView tv_income_detail;  //收益详情
    private TextView tv_income_amount;  //总收益
    private TextView tv_income_yesterday;  //昨日收益
    private TextView tv_vip_income;  //vip分成
    private TextView tv_user_buy;  //用户购买
    private TextView tv_user_reward;  //用户打赏
    private ListView lv_income;  //收益排行

    private List<MyIncomeModel> myIncomeModelList;
    private MyIncomeAdapter myIncomeAdapter;

    private static final int GETINCOMEINFOSUCCESS = 0;
    private static final int GETINCOMEINFOFAILED = 1;
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
                case GETINCOMEINFOSUCCESS:  //获取作品成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取收益成功：" + str);
                    GetMyIncomeResponse getMyIncomeResponse = gson.fromJson(str,GetMyIncomeResponse.class);
                    if(getMyIncomeResponse != null){
                        if(getMyIncomeResponse.getCode() == 0){
                            setMessage(getMyIncomeResponse);
                        }else {
                            UIUtils.showToastCenter(mContext,getMyIncomeResponse.getMsg());
                        }
                    }
                    break;
                case GETINCOMEINFOFAILED: //获取作品列表失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取收益失败：" + e.getMessage());
                    break;
            }
        }
    };

    private void setMessage(GetMyIncomeResponse getMyIncomeResponse) {
        if(!TextUtils.isEmpty(getMyIncomeResponse.getYield_amount())){
            tv_income_amount.setText("¥"+getMyIncomeResponse.getYield_amount());
        }
        if(!TextUtils.isEmpty(getMyIncomeResponse.getYield_yesterday())){
            tv_income_yesterday.setText("¥"+getMyIncomeResponse.getYield_yesterday());
        }
        if(!TextUtils.isEmpty(getMyIncomeResponse.getYield_purchase())){
            tv_user_buy.setText("¥"+getMyIncomeResponse.getYield_purchase());
        }
        if(!TextUtils.isEmpty(getMyIncomeResponse.getYield_reward())){
            tv_user_reward.setText("¥"+getMyIncomeResponse.getYield_reward());
        }
        if(!TextUtils.isEmpty(getMyIncomeResponse.getYield_vip())){
            tv_vip_income.setText("¥"+getMyIncomeResponse.getYield_vip());
        }
        myIncomeModelList = getMyIncomeResponse.getYield_list();
        if(myIncomeModelList != null){
            myIncomeAdapter = new MyIncomeAdapter(mContext,myIncomeModelList);
            lv_income.setAdapter(myIncomeAdapter);
        }
    }

    public TabIncomePager(Context context) {
        super(context);
    }


    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.pager_income, null);
        tv_income_detail = view.findViewById(R.id.tv_income_detail);
        tv_income_amount = view.findViewById(R.id.tv_income_amount);
        tv_income_yesterday = view.findViewById(R.id.tv_income_yesterday);
        tv_vip_income = view.findViewById(R.id.tv_vip_income);
        tv_user_buy = view.findViewById(R.id.tv_user_buy);
        tv_user_reward = view.findViewById(R.id.tv_user_reward);
        lv_income = view.findViewById(R.id.lv_income);
        return view;
    }

    @Override
    public void initData() {
//        myIncomeModelList = new ArrayList<>();
//        myIncomeModelList.add(new MyIncomeModel());
//        myIncomeModelList.add(new MyIncomeModel());
//        myIncomeModelList.add(new MyIncomeModel());
//        myIncomeModelList.add(new MyIncomeModel());
//        myIncomeModelList.add(new MyIncomeModel());
//        myIncomeModelList.add(new MyIncomeModel());
//        myIncomeAdapter = new MyIncomeAdapter(mContext,myIncomeModelList);
//        lv_income.setAdapter(myIncomeAdapter);

        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        self_media_id = SharedPrefrenceUtils.getString(mContext,"self_media_id");

        tv_income_detail.setOnClickListener(this);

        getIncomeInfo();
    }

    private void getIncomeInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(self_media_id)){
            reqBody.put("self_media_id",self_media_id);
        }
        LogUtils.e(TAG + ",self_media_id:"+self_media_id);
        netUtils.postDataAsynToNet(Constants.GetMyIncomeUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETINCOMEINFOSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETINCOMEINFOFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_income_detail:
                Intent intent = new Intent(mContext, IncomeDetailActivity.class);
                UIUtils.startActivity(intent);
                break;
        }
    }
}