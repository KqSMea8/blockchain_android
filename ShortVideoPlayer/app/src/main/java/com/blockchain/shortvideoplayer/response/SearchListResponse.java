package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.SelfMediaBean;
import com.blockchain.shortvideoplayer.model.VideoBean;

import java.util.List;

public class SearchListResponse extends BaseResponse {
    private List<VideoBean> video_list;
    private List<SelfMediaBean> self_media_list;

    @Override
    public String toString() {
        return "SearchListResponse{" +
                "video_list=" + video_list +
                ", self_media_list=" + self_media_list +
                '}';
    }

    public List<VideoBean> getVideo_list() {
        return video_list;
    }

    public void setVideo_list(List<VideoBean> video_list) {
        this.video_list = video_list;
    }

    public List<SelfMediaBean> getSelf_media_list() {
        return self_media_list;
    }

    public void setSelf_media_list(List<SelfMediaBean> self_media_list) {
        this.self_media_list = self_media_list;
    }
}
