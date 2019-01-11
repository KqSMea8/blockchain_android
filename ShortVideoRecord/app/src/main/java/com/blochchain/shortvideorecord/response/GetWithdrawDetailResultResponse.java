package com.blochchain.shortvideorecord.response;

public class GetWithdrawDetailResultResponse extends BaseResponse {
    private String withdraw_money;  //提现金额
    private int state;  //状态：0=转账中,1=提现成功，-1=提现失败
    private String bank_card;  //银行卡
    private String apply_time;  //申请时间
    private String deal_time;  //处理时间

    @Override
    public String toString() {
        return "GetWithdrawDetailResultResponse{" +
                "withdraw_money='" + withdraw_money + '\'' +
                ", state=" + state +
                ", bank_card='" + bank_card + '\'' +
                ", apply_time='" + apply_time + '\'' +
                ", deal_time='" + deal_time + '\'' +
                '}';
    }

    public String getWithdraw_money() {
        return withdraw_money;
    }

    public void setWithdraw_money(String withdraw_money) {
        this.withdraw_money = withdraw_money;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getBank_card() {
        return bank_card;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }

    public String getApply_time() {
        return apply_time;
    }

    public void setApply_time(String apply_time) {
        this.apply_time = apply_time;
    }

    public String getDeal_time() {
        return deal_time;
    }

    public void setDeal_time(String deal_time) {
        this.deal_time = deal_time;
    }
}
