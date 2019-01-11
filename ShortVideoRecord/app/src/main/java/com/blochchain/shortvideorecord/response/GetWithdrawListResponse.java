package com.blochchain.shortvideorecord.response;

import com.blochchain.shortvideorecord.model.WithdrawDetailModel;

import java.util.List;

public class GetWithdrawListResponse extends BaseResponse {
    private List<WithdrawDetailModel> withdraw_list;

    @Override
    public String toString() {
        return "GetWithdrawListResponse{" +
                "withdraw_list=" + withdraw_list +
                '}';
    }

    public List<WithdrawDetailModel> getWithdraw_list() {
        return withdraw_list;
    }

    public void setWithdraw_list(List<WithdrawDetailModel> withdraw_list) {
        this.withdraw_list = withdraw_list;
    }
}
