package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.PayInfoModel;

public class GetPayInfoResponse extends BaseResponse {
    private PayInfoModel data;

    @Override
    public String toString() {
        return "GetPayInfoResponse{" +
                "data=" + data +
                '}';
    }

    public PayInfoModel getData() {
        return data;
    }

    public void setData(PayInfoModel data) {
        this.data = data;
    }
}
