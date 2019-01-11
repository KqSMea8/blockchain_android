package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.MutiApplication;
import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.PayInfoModel;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetMsgRecordResponse;
import com.blockchain.shortvideoplayer.response.GetPayInfoResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.wxapi.WeChatPayCallback;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class BuyVIPActivity extends BaseActivity implements View.OnClickListener,WeChatPayCallback {
    private static final String TAG = BuyVIPActivity.class.getSimpleName();
    private ImageView iv_back;
    private CheckBox cb_one_month;
    private CheckBox cb_three_month;
    private CheckBox cb_half_year;
    private CheckBox cb_one_year;
    private int type;  //0 一个月，  1 三个月，   2  半年，   3 一年
    private TextView tv_submit;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private String user_id;
    private static final int GETBUYINFOSUCCESS = 0;
    private static final int GETBYINFOFAILED = 1;
    private String buy_money;
    private MyHandler mHandller = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GETBUYINFOSUCCESS:  //获取支付信息成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取支付信息成功："+str);
                    GetPayInfoResponse getPayInfoResponse = gson.fromJson(str,GetPayInfoResponse.class);
                    if(getPayInfoResponse != null ){
                        if(getPayInfoResponse.getCode() == 0){
                            PayInfoModel payInfoModel = getPayInfoResponse.getData();
                            if(payInfoModel != null){
                                weixinPay(payInfoModel);
                            }
                        }else {
                            UIUtils.showToastCenter(BuyVIPActivity.this,getPayInfoResponse.getMsg());
                        }
                    }
                    break;
                case GETBYINFOFAILED: //获取支付信息失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取支付信息失败："+e1.getMessage());
                    UIUtils.showToastCenter(BuyVIPActivity.this,e1.getMessage());
                    break;
            }
        }
    };

    private void weixinPay(PayInfoModel payInfoModel) {
        MutiApplication.getApplication().weChatPayCallback=this;
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.WeiXinAppId,false);;
        PayReq req;
        req = new PayReq();
        req.appId = payInfoModel.getAppid();//APPID
        req.partnerId = payInfoModel.getPartnerid();//    商户号
        req.prepayId = payInfoModel.getPrepayid();//  预付款ID
        req.nonceStr = payInfoModel.getNoncestr();//随机数
        req.timeStamp = payInfoModel.getTimestamp();//时间戳
        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
        req.sign = payInfoModel.getSign();//签名
        api.registerApp(Constants.WeiXinAppId);
        api.sendReq(req);
    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_buy_vip, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        cb_one_month = findViewById(R.id.cb_one_month);
        cb_three_month = findViewById(R.id.cb_three_month);
        cb_half_year = findViewById(R.id.cb_half_year);
        cb_one_year = findViewById(R.id.cb_one_year);
        tv_submit = findViewById(R.id.tv_submit);

        iv_back.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

        cb_one_month.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    type = 0;
                    cb_three_month.setChecked(false);
                    cb_half_year.setChecked(false);
                    cb_one_year.setChecked(false);
//                    buy_money = "15";
                    buy_money = "0.30";
                }
            }
        });
        cb_three_month.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    type = 1;
                    cb_one_month.setChecked(false);
                    cb_half_year.setChecked(false);
                    cb_one_year.setChecked(false);
//                    buy_money = "40";
                    buy_money = "0.02";
                }
            }
        });
        cb_half_year.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    type = 2;
                    cb_one_month.setChecked(false);
                    cb_three_month.setChecked(false);
                    cb_one_year.setChecked(false);
//                    buy_money = "70";
                    buy_money = "0.03";
                }
            }
        });
        cb_one_year.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    type = 3;
                    cb_one_month.setChecked(false);
                    cb_three_month.setChecked(false);
                    cb_half_year.setChecked(false);
//                    buy_money = "120";
                    buy_money = "0.04";
                }
            }
        });
        
        initData();
        return rootView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandller != null){
            mHandller.removeCallbacksAndMessages(null);
        }
    }

    private void initData() {
        user_id = SharedPrefrenceUtils.getString(this, "usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
    }

    private void pay(){
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
        reqBody.put("bay_money", buy_money);
//        reqBody.put("bay_money", "0.01");
        reqBody.put("bay_source", "0");
        reqBody.put("video_id", "0");
        reqBody.put("self_media_id", "0");
        netUtils.postDataAsynToNet(Constants.WeiXinPayUrl,reqBody ,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETBUYINFOSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETBYINFOFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                pay();
                break;
        }
    }

    @Override
    public void onWeChatPaySuccess() {
          setResult(200);
          finish();
    }

    @Override
    public void onWeChatPayFailure() {
        UIUtils.showToastCenter(BuyVIPActivity.this,"支付失败");
    }

    @Override
    public void onWeChatPayCancel() {
        UIUtils.showToastCenter(BuyVIPActivity.this,"支付取消");
    }
}
