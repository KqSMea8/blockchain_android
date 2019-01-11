package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.VideoBean;

import java.util.List;

public class GetHotVideoListResponse extends BaseResponse {
    private List<VideoBean> data;

    @Override
    public String toString() {
        return "GetHotVideoListResponse{" +
                "data=" + data +
                '}';
    }

    public List<VideoBean> getData() {
        return data;
    }

    public void setData(List<VideoBean> data) {
        this.data = data;
    }
}
