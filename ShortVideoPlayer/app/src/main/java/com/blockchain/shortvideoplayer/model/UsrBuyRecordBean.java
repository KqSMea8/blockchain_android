package com.blockchain.shortvideoplayer.model;

public class UsrBuyRecordBean {
    private int bay_source;  //来源，0=购买vip，1=购买视频，2=打赏
    private String bay_time;  //购买时间
    private String bay_money;  //金额

    @Override
    public String toString() {
        return "UsrBuyRecordBean{" +
                "bay_source='" + bay_source + '\'' +
                ", bay_time='" + bay_time + '\'' +
                ", bay_money='" + bay_money + '\'' +
                '}';
    }

    public int getBay_source() {
        return bay_source;
    }

    public void setBay_source(int bay_source) {
        this.bay_source = bay_source;
    }

    public String getBay_time() {
        return bay_time;
    }

    public void setBay_time(String bay_time) {
        this.bay_time = bay_time;
    }

    public String getBay_money() {
        return bay_money;
    }

    public void setBay_money(String bay_money) {
        this.bay_money = bay_money;
    }
}
