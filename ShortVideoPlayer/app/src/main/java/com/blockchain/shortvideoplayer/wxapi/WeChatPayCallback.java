package com.blockchain.shortvideoplayer.wxapi;

public interface WeChatPayCallback {
    void onWeChatPaySuccess();
    void onWeChatPayFailure();
    void onWeChatPayCancel();
}
