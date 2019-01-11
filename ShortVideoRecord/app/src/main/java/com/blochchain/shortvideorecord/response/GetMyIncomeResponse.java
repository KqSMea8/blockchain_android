package com.blochchain.shortvideorecord.response;

import com.blochchain.shortvideorecord.model.MyIncomeModel;

import java.util.List;

public class GetMyIncomeResponse extends BaseResponse {
    private String yield_amount; //总收益
    private String yield_yesterday; //昨日收益
    private String yield_vip; //Vip分成，每月第一天凌晨结算上个月的
    private String yield_purchase; //用户购买，每天凌晨结算前一天的
    private String yield_reward; //用户打赏
    private List<MyIncomeModel> yield_list;

    @Override
    public String toString() {
        return "GetMyIncomeResponse{" +
                "yield_amount='" + yield_amount + '\'' +
                ", yield_yesterday='" + yield_yesterday + '\'' +
                ", yield_vip='" + yield_vip + '\'' +
                ", yield_purchase='" + yield_purchase + '\'' +
                ", yield_reward='" + yield_reward + '\'' +
                ", yield_list=" + yield_list +
                '}';
    }

    public String getYield_amount() {
        return yield_amount;
    }

    public void setYield_amount(String yield_amount) {
        this.yield_amount = yield_amount;
    }

    public String getYield_yesterday() {
        return yield_yesterday;
    }

    public void setYield_yesterday(String yield_yesterday) {
        this.yield_yesterday = yield_yesterday;
    }

    public String getYield_vip() {
        return yield_vip;
    }

    public void setYield_vip(String yield_vip) {
        this.yield_vip = yield_vip;
    }

    public String getYield_purchase() {
        return yield_purchase;
    }

    public void setYield_purchase(String yield_purchase) {
        this.yield_purchase = yield_purchase;
    }

    public String getYield_reward() {
        return yield_reward;
    }

    public void setYield_reward(String yield_reward) {
        this.yield_reward = yield_reward;
    }

    public List<MyIncomeModel> getYield_list() {
        return yield_list;
    }

    public void setYield_list(List<MyIncomeModel> yield_list) {
        this.yield_list = yield_list;
    }
}
