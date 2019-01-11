package com.blochchain.shortvideorecord.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.blochchain.shortvideorecord.adapter.WithdrawDetailAdapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.model.WithdrawDetailModel;
import com.blochchain.shortvideorecord.response.GetIncomeDetailResponse;
import com.blochchain.shortvideorecord.response.GetWithdrawListResponse;
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

public class WithdrawDetailActivity extends BaseActivity {
    private static final String TAG = WithdrawDetailActivity.class.getSimpleName();
    private ImageView iv_back;
    private ListView lv_withdraw_detail;
    private List<WithdrawDetailModel> withdrawDetailModelList;
    private WithdrawDetailAdapter withdrawDetailAdapter;
    private static final int GETWITHDRAWINFOSUCCESS = 0; //获取提现明细
    private static final int GETWITHDRAWINFOFAILED = 1;
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
                case GETWITHDRAWINFOSUCCESS:  //获取提现明细成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取提现明细成功：" + str);
                    GetWithdrawListResponse getWithdrawListResponse = gson.fromJson(str, GetWithdrawListResponse.class);
                    if (getWithdrawListResponse != null) {
                        if (getWithdrawListResponse.getCode() == 0) {
                            withdrawDetailModelList = getWithdrawListResponse.getWithdraw_list();
                            if (withdrawDetailModelList != null) {
                                withdrawDetailAdapter = new WithdrawDetailAdapter(WithdrawDetailActivity.this, withdrawDetailModelList);
                                lv_withdraw_detail.setAdapter(withdrawDetailAdapter);
                            }
                        } else {
                            UIUtils.showToastCenter(WithdrawDetailActivity.this, getWithdrawListResponse.getMsg());
                        }
                    }
                    break;
                case GETWITHDRAWINFOFAILED: //获取提现明细失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取提现明细失败：" + e.getMessage());
                    break;
            }
        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_withdraw_detail, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        lv_withdraw_detail = findViewById(R.id.lv_withdraw_detail);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
        return rootView;
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");

//        withdrawDetailModelList = new ArrayList<>();
//        withdrawDetailModelList.add(new WithdrawDetailModel());
//        withdrawDetailModelList.add(new WithdrawDetailModel());
//        withdrawDetailModelList.add(new WithdrawDetailModel());
//        withdrawDetailModelList.add(new WithdrawDetailModel());
//        withdrawDetailModelList.add(new WithdrawDetailModel());
//        withdrawDetailModelList.add(new WithdrawDetailModel());
//        withdrawDetailAdapter = new WithdrawDetailAdapter(this,withdrawDetailModelList);
//        lv_withdraw_detail.setAdapter(withdrawDetailAdapter);

        lv_withdraw_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(WithdrawDetailActivity.this, WithdrawDetailResultActivity.class);
                intent.putExtra("withdraw_record_id",withdrawDetailModelList.get(i).getWithdraw_record_id());
                UIUtils.startActivity(intent);
            }
        });

        getWithdrawList();
    }

    private void getWithdrawList() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        LogUtils.e(TAG + "-self_media_id:" + self_media_id);
        netUtils.postDataAsynToNet(Constants.GetWithdrawListUrl, reqBody, new NetUtils.MyNetCall() {
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
