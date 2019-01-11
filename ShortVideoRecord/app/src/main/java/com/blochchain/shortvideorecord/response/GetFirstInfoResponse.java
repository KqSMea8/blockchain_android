package com.blochchain.shortvideorecord.response;

public class GetFirstInfoResponse extends BaseResponse {
    private String play_amount;  //播放量
    private String play_yesterday;  //昨日播放量
    private String fans_amount;  //粉丝，被被订阅
    private String fans_yesterday;  //昨日粉丝，被被订阅
    private String nickname;  //名称
    private String head_pic;  //头像

    @Override
    public String toString() {
        return "GetFirstInfoResponse{" +
                "play_amount='" + play_amount + '\'' +
                ", play_yesterday='" + play_yesterday + '\'' +
                ", fans_amount='" + fans_amount + '\'' +
                ", fans_yesterday='" + fans_yesterday + '\'' +
                ", nickname='" + nickname + '\'' +
                ", head_pic='" + head_pic + '\'' +
                '}';
    }

    public String getPlay_amount() {
        return play_amount;
    }

    public void setPlay_amount(String play_amount) {
        this.play_amount = play_amount;
    }

    public String getPlay_yesterday() {
        return play_yesterday;
    }

    public void setPlay_yesterday(String play_yesterday) {
        this.play_yesterday = play_yesterday;
    }

    public String getFans_amount() {
        return fans_amount;
    }

    public void setFans_amount(String fans_amount) {
        this.fans_amount = fans_amount;
    }

    public String getFans_yesterday() {
        return fans_yesterday;
    }

    public void setFans_yesterday(String fans_yesterday) {
        this.fans_yesterday = fans_yesterday;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }
}
