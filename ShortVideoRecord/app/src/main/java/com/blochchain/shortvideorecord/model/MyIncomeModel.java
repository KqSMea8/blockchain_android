package com.blochchain.shortvideorecord.model;

public class MyIncomeModel {
    private String video_id;
    private String video_title;
    private String video_dec;
    private String play_income;
    private String play_amount;

    @Override
    public String toString() {
        return "MyIncomeModel{" +
                "video_id='" + video_id + '\'' +
                ", video_title='" + video_title + '\'' +
                ", video_dec='" + video_dec + '\'' +
                ", play_income='" + play_income + '\'' +
                ", play_amount='" + play_amount + '\'' +
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

    public String getPlay_income() {
        return play_income;
    }

    public void setPlay_income(String play_income) {
        this.play_income = play_income;
    }

    public String getPlay_amount() {
        return play_amount;
    }

    public void setPlay_amount(String play_amount) {
        this.play_amount = play_amount;
    }
}
