package com.blochchain.shortvideorecord.response;

public class LoginResponse extends BaseResponse {
    private String self_media_id;
    private int is_first;  //是否是第一次登录，0：否，1：是

    @Override
    public String toString() {
        return "LoginResponse{" +
                "self_media_id='" + self_media_id + '\'' +
                ", is_first=" + is_first +
                '}';
    }

    public void setSelf_media_id(String self_media_id) {
        this.self_media_id = self_media_id;
    }

    public String getSelf_media_id() {
        return self_media_id;
    }

    public int getIs_first() {
        return is_first;
    }

    public void setIs_first(int is_first) {
        this.is_first = is_first;
    }
}
