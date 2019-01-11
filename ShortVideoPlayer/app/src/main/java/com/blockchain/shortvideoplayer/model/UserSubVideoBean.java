package com.blockchain.shortvideoplayer.model;

public class UserSubVideoBean {
    private String video_id;
    private String video_title;
    private String video_dec;
    private String play_amount;
    private String praised_amount;
    private String nickname;
    private String v_id;
    private String v_url;
    private String release_time;
    private String self_media_id;
    private String head_pic;
    private String video_pic;

    @Override
    public String toString() {
        return "UserSubVideoBean{" +
                "video_id='" + video_id + '\'' +
                ", video_title='" + video_title + '\'' +
                ", video_dec='" + video_dec + '\'' +
                ", play_amount='" + play_amount + '\'' +
                ", praised_amount='" + praised_amount + '\'' +
                ", nickname='" + nickname + '\'' +
                ", v_id='" + v_id + '\'' +
                ", v_url='" + v_url + '\'' +
                ", release_time='" + release_time + '\'' +
                ", self_media_id='" + self_media_id + '\'' +
                ", head_pic='" + head_pic + '\'' +
                ", video_pic='" + video_pic + '\'' +
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

    public String getPlay_amount() {
        return play_amount;
    }

    public void setPlay_amount(String play_amount) {
        this.play_amount = play_amount;
    }

    public String getPraised_amount() {
        return praised_amount;
    }

    public void setPraised_amount(String praised_amount) {
        this.praised_amount = praised_amount;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getRelease_time() {
        return release_time;
    }

    public void setRelease_time(String release_time) {
        this.release_time = release_time;
    }

    public String getSelf_media_id() {
        return self_media_id;
    }

    public void setSelf_media_id(String self_media_id) {
        this.self_media_id = self_media_id;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getVideo_pic() {
        return video_pic;
    }

    public void setVideo_pic(String video_pic) {
        this.video_pic = video_pic;
    }
}
