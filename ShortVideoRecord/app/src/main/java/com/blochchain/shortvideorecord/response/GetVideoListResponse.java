package com.blochchain.shortvideorecord.response;

import com.blochchain.shortvideorecord.model.MyProductModel;

import java.util.List;

public class GetVideoListResponse extends BaseResponse {
    private List<MyProductModel> video_list;

    @Override
    public String toString() {
        return "GetVideoListResponse{" +
                "video_list=" + video_list +
                '}';
    }

    public List<MyProductModel> getVideo_list() {
        return video_list;
    }

    public void setVideo_list(List<MyProductModel> video_list) {
        this.video_list = video_list;
    }
}
