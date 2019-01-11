package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.UsrBuyRecordBean;

import java.util.List;

public class GetUsrBuyRecordResponse extends BaseResponse {
    private List<UsrBuyRecordBean> pay_list;

    @Override
    public String toString() {
        return "GetUsrBuyRecordResponse{" +
                "pay_list=" + pay_list +
                '}';
    }

    public List<UsrBuyRecordBean> getPay_list() {
        return pay_list;
    }

    public void setPay_list(List<UsrBuyRecordBean> pay_list) {
        this.pay_list = pay_list;
    }
}
