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
import android.widget.TextView;

import com.blochchain.shortvideorecord.adapter.WithdrawDetailAdapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.response.GetWithdrawDetailResultResponse;
import com.blochchain.shortvideorecord.response.GetWithdrawListResponse;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class WithdrawDetailResultActivity extends BaseActivity {
    private static final String TAG = WithdrawDetailResultActivity.class.getSimpleName();
    private ImageView iv_back;
    private TextView tv_money;  //提现金额
    private TextView tv_bankcard;  //银行卡信息
    private TextView tv_state;  //提现状态
    private TextView tv_submit_time;  //提交时间
    private TextView tv_handle_time;  //银行处理时间
    private static final int GETWITHDRAWINFOSUCCESS = 0; //提现结果查询
    private static final int GETWITHDRAWINFOFAILED = 1;
    private NetUtils netUtils;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private String self_media_id;
    private String withdraw_record_id;
    private MyHandler mHandle = new MyHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETWITHDRAWINFOSUCCESS:  //提现结果查询成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "提现结果查询成功：" + str);
                    GetWithdrawDetailResultResponse getWithdrawDetailResultResponse = gson.fromJson(str,GetWithdrawDetailResultResponse.class);
                    if(getWithdrawDetailResultResponse != null){
                        if(getWithdrawDetailResultResponse.getCode() == 0){
                            setMessage(getWithdrawDetailResultResponse);
                        }else {
                            UIUtils.showToastCenter(WithdrawDetailResultActivity.this,getWithdrawDetailResultResponse.getMsg());
                        }
                    }
                    break;
                case GETWITHDRAWINFOFAILED: //提现结果查询失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "提现结果查询失败：" + e.getMessage());
                    break;
            }
        }
    };

    private void setMessage(GetWithdrawDetailResultResponse getWithdrawDetailResultResponse) {
        if(!TextUtils.isEmpty(getWithdrawDetailResultResponse.getApply_time())){
            tv_submit_time.setText(getWithdrawDetailResultResponse.getApply_time());
        }
        if(!TextUtils.isEmpty(getWithdrawDetailResultResponse.getDeal_time())){
            tv_handle_time.setText(getWithdrawDetailResultResponse.getDeal_time());
        }

        String bank_card = getWithdrawDetailResultResponse.getBank_card();
        if(!TextUtils.isEmpty(bank_card)){
            if(bank_card.length()>4){
                tv_bankcard.setText(bank_card.substring(0,4) + " ****** " + bank_card.substring(bank_card.length() - 4));
            }else {
                tv_bankcard.setText(bank_card);
            }
        }
        if(!TextUtils.isEmpty(getWithdrawDetailResultResponse.getWithdraw_money())){
            tv_money.setText("¥"+getWithdrawDetailResultResponse.getWithdraw_money());
        }
        switch (getWithdrawDetailResultResponse.getState()){//0=转账中,1=提现成功，-1=提现失败
            case 0:
                tv_state.setText("转账中");
                break;
            case 1:
                tv_state.setText("提现成功");
                break;
            case 2:
                tv_state.setText("提现失败");
                break;
        }
    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_withdraw_detail_result, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        tv_money = findViewById(R.id.tv_money);
        tv_bankcard = findViewById(R.id.tv_bankcard);
        tv_state = findViewById(R.id.tv_state);
        tv_submit_time = findViewById(R.id.tv_submit_time);
        tv_handle_time = findViewById(R.id.tv_handle_time);

        initData();

        return rootView;
    }

    private void initData() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        withdraw_record_id = intent.getStringExtra("withdraw_record_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");

        getWithdrawList();
    }

    private void getWithdrawList() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        if(!TextUtils.isEmpty(withdraw_record_id)){
            reqBody.put("withdraw_record_id", withdraw_record_id);
        }
        LogUtils.e(TAG + "-获取提现结果参数:" + reqBody.toString());
        netUtils.postDataAsynToNet(Constants.GetWithdrawResultUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETWITHDRAWINFOSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETWITHDRAWINFOFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }
}
