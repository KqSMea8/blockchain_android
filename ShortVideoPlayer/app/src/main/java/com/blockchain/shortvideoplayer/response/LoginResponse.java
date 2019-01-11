package com.blockchain.shortvideoplayer.response;

import java.util.List;

public class LoginResponse extends BaseResponse {
    private String usrs_id;
    private List<Integer> recommend_class;  //推荐类目
    private int is_first;   //是否是第一次登录，0：否，1：是

    @Override
    public String toString() {
        return "LoginResponse{" +
                "usrs_id='" + usrs_id + '\'' +
                ", recommend_class=" + recommend_class +
                ", is_first=" + is_first +
                '}';
    }

    public String getUsrs_id() {
        return usrs_id;
    }

    public void setUsrs_id(String usrs_id) {
        this.usrs_id = usrs_id;
    }

    public List<Integer> getRecommend_class() {
        return recommend_class;
    }

    public void setRecommend_class(List<Integer> recommend_class) {
        this.recommend_class = recommend_class;
    }

    public int getIs_first() {
        return is_first;
    }

    public void setIs_first(int is_first) {
        this.is_first = is_first;
    }
}
