package com.blockchain.shortvideoplayer.model;

public class SelfMediaBean {
    private String self_media_id;  //自媒体id
    private String nickname;  //昵称
    private String head_pic;  //头像照片
    private String self_intr;  //自我介绍
    private String fans_amount;  //粉丝
    private String last_active_time;  //最后发布视频时间

    @Override
    public String toString() {
        return "SelfMediaBean{" +
                "self_media_id='" + self_media_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", head_pic='" + head_pic + '\'' +
                ", self_intr='" + self_intr + '\'' +
                ", fans_amount='" + fans_amount + '\'' +
                ", last_active_time='" + last_active_time + '\'' +
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

    public String getLast_active_time() {
        return last_active_time;
    }

    public void setLast_active_time(String last_active_time) {
        this.last_active_time = last_active_time;
    }
}
