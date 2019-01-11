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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.response.BaseResponse;
import com.blochchain.shortvideorecord.response.GetIncomeDetailResponse;
import com.blochchain.shortvideorecord.response.GetWithdrawInfoResponse;
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

public class WithdrawActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = WithdrawActivity.class.getSimpleName();
    private ImageView iv_back;
    private TextView tv_bank_no;   //银行卡号
    private LinearLayout ll_bank_name;  //银行卡名称
    private TextView tv_bank_name;
    private EditText et_money;   //提现金额
    private TextView tv_total_money;  //可提现金额
    private TextView tv_withdraw_all;  //全部提现
    private TextView tv_submit;  //确定

    private static final int GETINCOMEINFOSUCCESS = 0;
    private static final int GETINCOMEINFOFAILED = 1;
    private static final int WITHDRAWSUCCESS = 2;
    private static final int WITHDRAWFAILED = 3;
    private NetUtils netUtils;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private String self_media_id;
    private String totalMoney;  //可提现最大金额
    private String money;    //提现金额
    private MyHandler mHandle = new MyHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETINCOMEINFOSUCCESS:  //提现查询成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "提现查询成功：" + str);
                    GetWithdrawInfoResponse getWithdrawInfoResponse = gson.fromJson(str, GetWithdrawInfoResponse.class);
                    if (getWithdrawInfoResponse != null) {
                        if (getWithdrawInfoResponse.getCode() == 0) {
                            setMessage(getWithdrawInfoResponse);
                        } else {
                            UIUtils.showToastCenter(WithdrawActivity.this, getWithdrawInfoResponse.getMsg());
                        }
                    }
                    break;
                case GETINCOMEINFOFAILED: //提现查询失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "提现查询失败：" + e.getMessage());
                    break;
                case WITHDRAWSUCCESS: //提现成功
                    String string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "提现成功：" + string);
                    BaseResponse baseResponse = gson.fromJson(string, BaseResponse.class);
                    if (baseResponse != null) {
                        if (baseResponse.getCode() == 0) {
                            Intent intent = new Intent(WithdrawActivity.this, OperationSuccessActivity.class);
                            UIUtils.startActivity(intent);
                        } else {
                            UIUtils.showToastCenter(WithdrawActivity.this, baseResponse.getMsg());
                        }
                    }
                    break;
                case WITHDRAWFAILED:  //提现失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "提现失败：" + e1.getMessage());
                    break;
            }
        }
    };

    private void setMessage(GetWithdrawInfoResponse getWithdrawInfoResponse) {
        if (!TextUtils.isEmpty(getWithdrawInfoResponse.getBank_card())) {
            tv_bank_no.setText(getWithdrawInfoResponse.getBank_card());
        }
        if (!TextUtils.isEmpty(getWithdrawInfoResponse.getYield_desirable())) {
            tv_total_money.setText("¥"+getWithdrawInfoResponse.getYield_desirable());
            totalMoney = getWithdrawInfoResponse.getYield_desirable();
        }
    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_withdraw, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        tv_bank_no = findViewById(R.id.tv_bank_no);
        ll_bank_name = findViewById(R.id.ll_bank_name);
        tv_bank_name = findViewById(R.id.tv_bank_name);
        et_money = findViewById(R.id.et_money);
        tv_total_money = findViewById(R.id.tv_total_money);
        tv_withdraw_all = findViewById(R.id.tv_withdraw_all);
        tv_submit = findViewById(R.id.tv_submit);

        initData();

        return rootView;
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");

        iv_back.setOnClickListener(this);
        tv_withdraw_all.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

        getWithdrawInfo();
    }

    private void getWithdrawInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        LogUtils.e(TAG + "-self_media_id:" + self_media_id);
        netUtils.postDataAsynToNet(Constants.GetWithdrawInfoUrl, reqBody, new NetUtils.MyNetCall() {
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

    private void withdraw() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        reqBody.put("money", money);
        LogUtils.e(TAG + "提现参数:" + reqBody.toString());
        netUtils.postDataAsynToNet(Constants.WithdrawUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = WITHDRAWSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = WITHDRAWFAILED;
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
            case R.id.tv_withdraw_all: //点击全部提现
                if (!TextUtils.isEmpty(totalMoney)) {
                    et_money.setText(totalMoney);
                }
                break;
            case R.id.tv_submit:  //确定
                money = et_money.getText().toString();
                if (TextUtils.isEmpty(money)) {
                    UIUtils.showToastCenter(WithdrawActivity.this, "请输入可提现金额");
                    return;
                }
                withdraw();
//                Intent intent = new Intent(this,OperationSuccessActivity.class);
//                UIUtils.startActivity(intent);
                break;
        }

    }
}
