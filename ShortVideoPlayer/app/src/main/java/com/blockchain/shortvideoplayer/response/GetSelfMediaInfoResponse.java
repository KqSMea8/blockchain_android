package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.VideoBean;

import java.util.List;

public class GetSelfMediaInfoResponse extends BaseResponse {
    private String self_media_id;  //自媒体id
    private String nickname;  //昵称
    private String head_pic;  //头像
    private String self_intr;  //自我介绍
    private String fans_amount;  //粉丝
    private String attention_amount; //收藏数，用户关注数
    private String praised_amount;  //点赞数
    private String last_active_time; //最后发布视频时间
    private List<VideoBean> data;

    @Override
    public String toString() {
        return "GetSelfMediaInfoResponse{" +
                "self_media_id='" + self_media_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", head_pic='" + head_pic + '\'' +
                ", self_intr='" + self_intr + '\'' +
                ", fans_amount='" + fans_amount + '\'' +
                ", attention_amount='" + attention_amount + '\'' +
                ", praised_amount='" + praised_amount + '\'' +
                ", last_active_time='" + last_active_time + '\'' +
                ", data=" + data +
                '}';
    }

    public String getSelf_media_id() {
        return self_media_id;
    }

    public void setSelf_media_id(String self_media_id) {
        this.self_media_id = self_media_id;
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

    public String getSelf_intr() {
        return self_intr;
    }

    public void setSelf_intr(String self_intr) {
        this.self_intr = self_intr;
    }

    public String getFans_amount() {
        return fans_amount;
    }

    public void setFans_amount(String fans_amount) {
        this.fans_amount = fans_amount;
    }

    public String getAttention_amount() {
        return attention_amount;
    }

    public void setAttention_amount(String attention_amount) {
        this.attention_amount = attention_amount;
    }

    public String getPraised_amount() {
        return praised_amount;
    }

    public void setPraised_amount(String praised_amount) {
        this.praised_amount = praised_amount;
    }

    public String getLast_active_time() {
        return last_active_time;
    }

    public void setLast_active_time(String last_active_time) {
        this.last_active_time = last_active_time;
    }

    public List<VideoBean> getData() {
        return data;
    }

    public void setData(List<VideoBean> data) {
        this.data = data;
    }
}
