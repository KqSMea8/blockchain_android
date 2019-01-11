package com.blockchain.shortvideoplayer.response;

public class UploadImageResponse extends BaseResponse {
    private String pic_path;

    @Override
    public String toString() {
        return "UploadImageResponse{" +
                "pic_path='" + pic_path + '\'' +
                '}';
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }
}
