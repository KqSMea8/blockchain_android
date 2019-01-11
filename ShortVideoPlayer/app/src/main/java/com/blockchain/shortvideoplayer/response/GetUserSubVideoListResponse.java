package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.UserSubVideoBean;

import java.util.List;

public class GetUserSubVideoListResponse extends BaseResponse{
    private List<UserSubVideoBean> userSubVideoList;

    @Override
    public String toString() {
        return "GetUserSubVideoListResponse{" +
                "userSubVideoList=" + userSubVideoList +
                '}';
    }

    public List<UserSubVideoBean> getUserSubVideoList() {
        return userSubVideoList;
    }

    public void setUserSubVideoList(List<UserSubVideoBean> userSubVideoList) {
        this.userSubVideoList = userSubVideoList;
    }
}
