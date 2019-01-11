package com.blochchain.shortvideorecord.response;

public class GetWithdrawInfoResponse extends BaseResponse {
    private String bank_card; //银行卡
    private String yield_desirable; //可提现金额 总收益（yield_amount）- 已提现（yield_cleared）

    @Override
    public String toString() {
        return "GetWithdrawInfoResponse{" +
                "bank_card='" + bank_card + '\'' +
                ", yield_desirable='" + yield_desirable + '\'' +
                '}';
    }

    public String getBank_card() {
        return bank_card;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }

    public String getYield_desirable() {
        return yield_desirable;
    }

    public void setYield_desirable(String yield_desirable) {
        this.yield_desirable = yield_desirable;
    }
}
