package com.blochchain.shortvideorecord.response;

import com.blochchain.shortvideorecord.model.PlayDataModel;

import java.util.List;

public class GetPlayDataResponse extends BaseResponse {
    private String play_amount;  //播放量
    private String play_yesterday;  //昨日播放量
    private String play_source_play_ratio;  //播放来源比例，播放
    private String play_source_search_ratio;  //播放来源比例，搜索
    private String play_source_class_ratio;  //播放来源比例，类目推荐
    private List<PlayDataModel> video_list;   //播放视频列表

    @Override
    public String toString() {
        return "GetPlayDataResponse{" +
                "play_amount='" + play_amount + '\'' +
                ", play_yesterday='" + play_yesterday + '\'' +
                ", play_source_play_ratio='" + play_source_play_ratio + '\'' +
                ", play_source_search_ratio='" + play_source_search_ratio + '\'' +
                ", play_source_class_ratio='" + play_source_class_ratio + '\'' +
                ", video_list=" + video_list +
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

    public String getPlay_source_play_ratio() {
        return play_source_play_ratio;
    }

    public void setPlay_source_play_ratio(String play_source_play_ratio) {
        this.play_source_play_ratio = play_source_play_ratio;
    }

    public String getPlay_source_search_ratio() {
        return play_source_search_ratio;
    }

    public void setPlay_source_search_ratio(String play_source_search_ratio) {
        this.play_source_search_ratio = play_source_search_ratio;
    }

    public String getPlay_source_class_ratio() {
        return play_source_class_ratio;
    }

    public void setPlay_source_class_ratio(String play_source_class_ratio) {
        this.play_source_class_ratio = play_source_class_ratio;
    }

    public List<PlayDataModel> getVideo_list() {
        return video_list;
    }

    public void setVideo_list(List<PlayDataModel> video_list) {
        this.video_list = video_list;
    }
}
