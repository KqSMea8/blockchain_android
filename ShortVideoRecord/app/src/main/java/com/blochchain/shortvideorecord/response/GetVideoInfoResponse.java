package com.blochchain.shortvideorecord.response;

public class GetVideoInfoResponse extends BaseResponse {
    private String video_id;
    private String video_title;
    private String video_dec;
    private String play_amount;  //播放次数
    private String attention_amount;  //收藏数，用户关注数
    private String income_amount;  //该视频收入，查询 购买记录表（tab_bay_record）的金额（bay_money）汇总，
    private String video_pic;  //封面图

    @Override
    public String toString() {
        return "GetVideoInfoResponse{" +
                "video_id='" + video_id + '\'' +
                ", video_title='" + video_title + '\'' +
                ", video_dec='" + video_dec + '\'' +
                ", play_amount='" + play_amount + '\'' +
                ", attention_amount='" + attention_amount + '\'' +
                ", income_amount='" + income_amount + '\'' +
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

    public String getAttention_amount() {
        return attention_amount;
    }

    public void setAttention_amount(String attention_amount) {
        this.attention_amount = attention_amount;
    }

    public String getIncome_amount() {
        return income_amount;
    }

    public void setIncome_amount(String income_amount) {
        this.income_amount = income_amount;
    }

    public String getVideo_pic() {
        return video_pic;
    }

    public void setVideo_pic(String video_pic) {
        this.video_pic = video_pic;
    }
}
