package com.blochchain.shortvideorecord.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blochchain.shortvideorecord.adapter.IncomeDetailAdapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.model.IncomeDetailModel;
import com.blochchain.shortvideorecord.response.GetIncomeDetailResponse;
import com.blochchain.shortvideorecord.response.GetMyIncomeResponse;
import com.blochchain.shortvideorecord.tabpager.ViewTabBasePager;
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

public class IncomeDetailActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = IncomeDetailActivity.class.getSimpleName();
    private ImageView iv_back;
    private TextView tv_withdraw_detail;  //提现明细
    private TextView tv_unbalance_income;  //未结算收益
    private TextView tv_balance;  //提现
    private TextView tv_total_income;  //总收益
    private TextView tv_income_balance;  //已结算收益
    private ListView lv_income_detail;  //收益明细

    private List<IncomeDetailModel> incomeDetailModelList;
    private IncomeDetailAdapter incomeDetailAdapter;

    private static final int GETINCOMEINFOSUCCESS = 0;
    private static final int GETINCOMEINFOFAILED = 1;
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
                case GETINCOMEINFOSUCCESS:  //获取作品成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取收益明细成功：" + str);
                    GetIncomeDetailResponse getIncomeDetailResponse = gson.fromJson(str,GetIncomeDetailResponse.class);
                    if(getIncomeDetailResponse != null){
                        if(getIncomeDetailResponse.getCode() == 0){
                            setMessage(getIncomeDetailResponse);
                        }else {
                            UIUtils.showToastCenter(IncomeDetailActivity.this,getIncomeDetailResponse.getMsg());
                        }
                    }
                    break;
                case GETINCOMEINFOFAILED: //获取作品列表失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取收益明细失败：" + e.getMessage());
                    break;
            }
        }
    };

    private void setMessage(GetIncomeDetailResponse getIncomeDetailResponse) {
        double total;
        if(!TextUtils.isEmpty(getIncomeDetailResponse.getYield_amount())){
            tv_income_balance.setText("¥"+getIncomeDetailResponse.getYield_amount());
        }
        if(!TextUtils.isEmpty(getIncomeDetailResponse.getYield_unsettled())){
            tv_unbalance_income.setText("¥"+getIncomeDetailResponse.getYield_unsettled());
        }
        total = Double.valueOf(getIncomeDetailResponse.getYield_amount()) + Double.valueOf(getIncomeDetailResponse.getYield_unsettled());
        tv_total_income.setText("¥"+String.valueOf(total));

        incomeDetailModelList = getIncomeDetailResponse.getIncome_list();
        if(incomeDetailModelList != null){
            incomeDetailAdapter = new IncomeDetailAdapter(this,incomeDetailModelList);
            lv_income_detail.setAdapter(incomeDetailAdapter);
        }
    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_income_detail, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        tv_withdraw_detail = findViewById(R.id.tv_withdraw_detail);
        tv_unbalance_income = findViewById(R.id.tv_unbalance_income);
        tv_balance = findViewById(R.id.tv_balance);
        tv_total_income = findViewById(R.id.tv_total_income);
        tv_income_balance = findViewById(R.id.tv_income_balance);
        lv_income_detail = findViewById(R.id.lv_income_detail);
        
        initData();
        return rootView;
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        self_media_id = SharedPrefrenceUtils.getString(this,"self_media_id");

        tv_balance.setOnClickListener(this);
        tv_withdraw_detail.setOnClickListener(this);
        iv_back.setOnClickListener(this);

//        incomeDetailModelList = new ArrayList<>();
//        incomeDetailModelList.add(new IncomeDetailModel());
//        incomeDetailModelList.add(new IncomeDetailModel());
//        incomeDetailModelList.add(new IncomeDetailModel());
//        incomeDetailModelList.add(new IncomeDetailModel());
//        incomeDetailModelList.add(new IncomeDetailModel());
//        incomeDetailModelList.add(new IncomeDetailModel());
//        incomeDetailModelList.add(new IncomeDetailModel());
//        incomeDetailAdapter = new IncomeDetailAdapter(this,incomeDetailModelList);
//        lv_income_detail.setAdapter(incomeDetailAdapter);

       getIncomeInfo();
    }

    private void getIncomeInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(self_media_id)){
            reqBody.put("self_media_id",self_media_id);
        }
        LogUtils.e(TAG + "-self_media_id:"+self_media_id);
        netUtils.postDataAsynToNet(Constants.GetMyIncomeInfoUrl, reqBody, new NetUtils.MyNetCall() {
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
            case R.id.tv_balance: //提现
                Intent intent = new Intent(this,WithdrawActivity.class);
                UIUtils.startActivity(intent);
                break;
            case R.id.tv_withdraw_detail:  //提现明细
                Intent intent1 = new Intent(this,WithdrawDetailActivity.class);
                UIUtils.startActivity(intent1);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
