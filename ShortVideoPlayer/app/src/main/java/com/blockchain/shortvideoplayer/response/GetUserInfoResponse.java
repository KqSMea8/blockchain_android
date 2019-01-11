package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.VideoBean;

import java.util.List;

public class GetUserInfoResponse extends BaseResponse {
    private String usrs_id;
    private String nickname;  //昵称
    private int education_id; //学历
    private String self_intr;  //自我介绍
    private String head_pic;  //头像
    private int is_vip;  //是否是vip，0=否，1=是
    private String fans_amount;  //订阅自媒体数，订阅了也就是自媒体的粉丝
    private int sex;  //性别
    private String birthday;  //生日
    private List<VideoBean> buy_video_list;  //已购买列表
    private List<VideoBean> play_video_list;  //观看记录列表
    private List<VideoBean> attention_video_list;  //收藏视频列表

    @Override
    public String toString() {
        return "GetUserInfoResponse{" +
                "usrs_id='" + usrs_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", education_id=" + education_id +
                ", self_intr='" + self_intr + '\'' +
                ", head_pic='" + head_pic + '\'' +
                ", is_vip=" + is_vip +
                ", fans_amount='" + fans_amount + '\'' +
                ", sex=" + sex +
                ", birthday='" + birthday + '\'' +
                ", buy_video_list=" + buy_video_list +
                ", play_video_list=" + play_video_list +
                ", attention_video_list=" + attention_video_list +
                '}';
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

    public int getEducation_id() {
        return education_id;
    }

    public void setEducation_id(int education_id) {
        this.education_id = education_id;
    }

    public String getSelf_intr() {
        return self_intr;
    }

    public void setSelf_intr(String self_intr) {
        this.self_intr = self_intr;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public String getFans_amount() {
        return fans_amount;
    }

    public void setFans_amount(String fans_amount) {
        this.fans_amount = fans_amount;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public List<VideoBean> getBuy_video_list() {
        return buy_video_list;
    }

    public void setBuy_video_list(List<VideoBean> buy_video_list) {
        this.buy_video_list = buy_video_list;
    }

    public List<VideoBean> getPlay_video_list() {
        return play_video_list;
    }

    public void setPlay_video_list(List<VideoBean> play_video_list) {
        this.play_video_list = play_video_list;
    }

    public List<VideoBean> getAttention_video_list() {
        return attention_video_list;
    }

    public void setAttention_video_list(List<VideoBean> attention_video_list) {
        this.attention_video_list = attention_video_list;
    }
}
