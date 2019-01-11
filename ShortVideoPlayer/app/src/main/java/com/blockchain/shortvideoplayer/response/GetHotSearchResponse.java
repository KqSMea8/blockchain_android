package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.SearchBean;

import java.util.List;

public class GetHotSearchResponse extends BaseResponse {
    private List<SearchBean> hot_search_list;

    @Override
    public String toString() {
        return "GetHotSearchResponse{" +
                "hot_search_list=" + hot_search_list +
                '}';
    }

    public List<SearchBean> getHot_search_list() {
        return hot_search_list;
    }

    public void setHot_search_list(List<SearchBean> hot_search_list) {
        this.hot_search_list = hot_search_list;
    }
}
