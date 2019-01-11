package com.blochchain.shortvideorecord.model;

public class MyProductModel {
    private String video_id;
    private String video_title;
    private String play_amount;
    private String release_time;
    private String video_dec;
    private String video_pic;

    @Override
    public String toString() {
        return "MyProductModel{" +
                "video_id='" + video_id + '\'' +
                ", video_title='" + video_title + '\'' +
                ", play_amount='" + play_amount + '\'' +
                ", release_time='" + release_time + '\'' +
                ", video_dec='" + video_dec + '\'' +
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

    public String getPlay_amount() {
        return play_amount;
    }

    public void setPlay_amount(String play_amount) {
        this.play_amount = play_amount;
    }

    public String getRelease_time() {
        return release_time;
    }

    public void setRelease_time(String release_time) {
        this.release_time = release_time;
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
}
