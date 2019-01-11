package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.VideoBean;

public class GetVideoInfoResponse extends BaseResponse {
    private VideoBean data;

    @Override
    public String toString() {
        return "GetVideoInfoResponse{" +
                "data=" + data +
                '}';
    }

    public VideoBean getData() {
        return data;
    }

    public void setData(VideoBean data) {
        this.data = data;
    }
}
