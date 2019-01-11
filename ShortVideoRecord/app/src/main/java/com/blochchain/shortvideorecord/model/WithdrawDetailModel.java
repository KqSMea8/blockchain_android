package com.blochchain.shortvideorecord.model;

public class WithdrawDetailModel {
    private String bank_card;  //银行卡
    private String withdraw_money;  //提现金额
    private String apply_time;  //申请时间
    private int state;  //状态：0=转账中,1=提现成功，-1=提现失败
    private String withdraw_record_id;

    @Override
    public String toString() {
        return "WithdrawDetailModel{" +
                "bank_card='" + bank_card + '\'' +
                ", withdraw_money='" + withdraw_money + '\'' +
                ", apply_time='" + apply_time + '\'' +
                ", state=" + state +
                ", withdraw_record_id='" + withdraw_record_id + '\'' +
                '}';
    }

    public String getBank_card() {
        return bank_card;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }

    public String getWithdraw_money() {
        return withdraw_money;
    }

    public void setWithdraw_money(String withdraw_money) {
        this.withdraw_money = withdraw_money;
    }

    public String getApply_time() {
        return apply_time;
    }

    public void setApply_time(String apply_time) {
        this.apply_time = apply_time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getWithdraw_record_id() {
        return withdraw_record_id;
    }

    public void setWithdraw_record_id(String withdraw_record_id) {
        this.withdraw_record_id = withdraw_record_id;
    }
}
