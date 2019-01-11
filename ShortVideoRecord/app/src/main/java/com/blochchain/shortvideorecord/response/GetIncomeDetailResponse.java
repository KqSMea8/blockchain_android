package com.blochchain.shortvideorecord.response;

import com.blochchain.shortvideorecord.model.IncomeDetailModel;

import java.util.List;

public class GetIncomeDetailResponse extends BaseResponse {
    private String yield_unsettled;  //未结算收入
    private String yield_amount;  //已经结算收益
    private List<IncomeDetailModel> income_list; //收入列表，统计tab_bay_record表

    @Override
    public String toString() {
        return "GetIncomeDetailResponse{" +
                "yield_unsettled='" + yield_unsettled + '\'' +
                ", yield_amount='" + yield_amount + '\'' +
                ", income_list=" + income_list +
                '}';
    }

    public String getYield_unsettled() {
        return yield_unsettled;
    }

    public void setYield_unsettled(String yield_unsettled) {
        this.yield_unsettled = yield_unsettled;
    }

    public String getYield_amount() {
        return yield_amount;
    }

    public void setYield_amount(String yield_amount) {
        this.yield_amount = yield_amount;
    }

    public List<IncomeDetailModel> getIncome_list() {
        return income_list;
    }

    public void setIncome_list(List<IncomeDetailModel> income_list) {
        this.income_list = income_list;
    }
}
