package com.blockchain.shortvideoplayer.response;

public class IsSubcribedResponse extends BaseResponse {
    private int is_subscribe;  //是否订阅该类目，0：否，1：订阅

    @Override
    public String toString() {
        return "IsSubcribedResponse{" +
                "is_subscribe=" + is_subscribe +
                '}';
    }

    public int getIs_subscribe() {
        return is_subscribe;
    }

    public void setIs_subscribe(int is_subscribe) {
        this.is_subscribe = is_subscribe;
    }
}
