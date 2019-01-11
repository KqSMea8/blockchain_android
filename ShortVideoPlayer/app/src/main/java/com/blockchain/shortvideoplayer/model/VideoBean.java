package com.blockchain.shortvideoplayer.model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.util.Objects;

public class VideoBean implements Serializable{
    private String video_id;  //视频id
    private String video_title;  //视频标题
    private String video_dec;  //视频描述
    private String video_pic;  //视频封面
    private String play_amount;  //播放次数
    private String self_media_id;  //上传自媒体id
    private String nickname;  //上传自媒体昵称
    private String head_pic;  //头像
    private String v_id;  //播放参数id
    private String v_url;  //播放参数url
    private String attention_amount;  //收藏数，用户关注数
    private String praised_amount;  //点赞数

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoBean videoBean = (VideoBean) o;
        return Objects.equals(video_id, videoBean.video_id);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(video_id);
    }

    @Override
    public String toString() {
        return "VideoBean{" +
                "video_id='" + video_id + '\'' +
                ", video_title='" + video_title + '\'' +
                ", video_dec='" + video_dec + '\'' +
                ", video_pic='" + video_pic + '\'' +
                ", play_amount='" + play_amount + '\'' +
                ", self_media_id='" + self_media_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", head_pic='" + head_pic + '\'' +
                ", v_id='" + v_id + '\'' +
                ", v_url='" + v_url + '\'' +
                ", attention_amount='" + attention_amount + '\'' +
                ", praised_amount='" + praised_amount + '\'' +
                '}';
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

    public String getVideo_dec() {
        return video_dec;
    }

    public void setVideo_dec(String video_dec) {
        this.video_dec = video_dec;
    }

    public String getVideo_pic() {
        return video_pic;
    }

    public void setVideo_pic(String video_pic) {
        this.video_pic = video_pic;
    }

    public String getPlay_amount() {
        return play_amount;
    }

    public void setPlay_amount(String play_amount) {
        this.play_amount = play_amount;
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

    public String getV_id() {
        return v_id;
    }

    public void setV_id(String v_id) {
        this.v_id = v_id;
    }

    public String getV_url() {
        return v_url;
    }

    public void setV_url(String v_url) {
        this.v_url = v_url;
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
}
