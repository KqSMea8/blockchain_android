package com.blockchain.shortvideoplayer.wxapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.blockchain.shortvideoplayer.MutiApplication;
import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private static final String TAG = WXPayEntryActivity.class.getSimpleName();
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_entry);

        api = WXAPIFactory.createWXAPI(this, Constants.WeiXinAppId);//这里填入自己的微信APPID
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.e(TAG + ",baseReq:"+baseReq.getType());

    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.e("onPayFinish, errCode = " + baseResp.errCode);
        WeChatPayCallback weChatPayCallback= MutiApplication.getApplication().weChatPayCallback;

//        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//
//            int errCord = baseResp.errCode;
//
//            if (errCord == 0) {
//                UIUtils.showToastCenter(this,"支付成功");
//            } else {
//                UIUtils.showToastCenter(this,"支付失败");
//            }
        if(ConstantsAPI.COMMAND_PAY_BY_WX==baseResp.getType()){
            if(weChatPayCallback!=null){
                switch (baseResp.errCode){
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        weChatPayCallback.onWeChatPayCancel();
                        break;
                    case BaseResp.ErrCode.ERR_COMM:
                        weChatPayCallback.onWeChatPayFailure();
                        break;
                    case BaseResp.ErrCode.ERR_OK:
                        weChatPayCallback.onWeChatPaySuccess();
                        break;
                }
            }


            //这里接收到了返回的状态码可以进行相应的操作，如果不想在这个页面操作可以把状态码存在本地然后finish掉这个页面，这样就回到了你调起支付的那个页面

            //获取到你刚刚存到本地的状态码进行相应的操作就可以了
            finish();
        }

    }

    @Override

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }
}
