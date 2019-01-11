package com.blochchain.shortvideorecord.model;

public class PlayDataModel {
    private String video_id;
    private String video_title;
    private String play_amount;
    private String video_income;

    @Override
    public String toString() {
        return "PlayDataModel{" +
                "video_id='" + video_id + '\'' +
                ", video_title='" + video_title + '\'' +
                ", play_amount='" + play_amount + '\'' +
                ", video_income='" + video_income + '\'' +
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

    public String getVideo_income() {
        return video_income;
    }

    public void setVideo_income(String video_income) {
        this.video_income = video_income;
    }
}
