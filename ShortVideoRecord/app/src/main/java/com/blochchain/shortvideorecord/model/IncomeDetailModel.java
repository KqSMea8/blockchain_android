package com.blochchain.shortvideorecord.model;

public class IncomeDetailModel {
    private int bay_source;  //0=购买vip，1=购买视频，2=打赏
    private String bay_time;
    private String bay_money;
    private String video_id;  //视频id，如果是vip则为0
    private String video_title;
    private String usrs_id;
    private String nickname;

    @Override
    public String toString() {
        return "IncomeDetailModel{" +
                "bay_source=" + bay_source +
                ", bay_time='" + bay_time + '\'' +
                ", bay_money='" + bay_money + '\'' +
                ", video_id='" + video_id + '\'' +
                ", video_title='" + video_title + '\'' +
                ", usrs_id='" + usrs_id + '\'' +
                ", nickname='" + nickname + '\'' +
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

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getUsrs_id() {
        return usrs_id;
    }

    public void setUsrs_id(String usrs_id) {
        this.usrs_id = usrs_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
