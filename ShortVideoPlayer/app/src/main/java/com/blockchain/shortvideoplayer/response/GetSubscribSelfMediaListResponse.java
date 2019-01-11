package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.SelfMediaBean;

import java.util.List;

public class GetSubscribSelfMediaListResponse extends BaseResponse {
    private List<SelfMediaBean> self_media_list;

    @Override
    public String toString() {
        return "GetSubscribSelfMediaListResponse{" +
                "self_media_list=" + self_media_list +
                '}';
    }

    public List<SelfMediaBean> getSelf_media_list() {
        return self_media_list;
    }

    public void setSelf_media_list(List<SelfMediaBean> self_media_list) {
        this.self_media_list = self_media_list;
    }
}
