package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.VideoBean;

import java.util.List;

public class GetClassVideoListResponse extends BaseResponse {
    private List<VideoBean> hot_video_list;
    private List<VideoBean> video_list;

    @Override
    public String toString() {
        return "GetClassVideoListResponse{" +
                "hot_video_list=" + hot_video_list +
                ", video_list=" + video_list +
                '}';
    }

    public List<VideoBean> getHot_video_list() {
        return hot_video_list;
    }

    public void setHot_video_list(List<VideoBean> hot_video_list) {
        this.hot_video_list = hot_video_list;
    }

    public List<VideoBean> getVideo_list() {
        return video_list;
    }

    public void setVideo_list(List<VideoBean> video_list) {
        this.video_list = video_list;
    }
}
