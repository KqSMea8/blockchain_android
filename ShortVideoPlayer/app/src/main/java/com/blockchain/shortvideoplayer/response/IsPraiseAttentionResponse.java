package com.blockchain.shortvideoplayer.response;

public class IsPraiseAttentionResponse extends BaseResponse {
    private int is_praised;   //是否点赞 0 否，  1 是
    private int is_attention;  //是否收藏 0 否，  1 是

    @Override
    public String toString() {
        return "IsPraiseAttentionResponse{" +
                "is_praised=" + is_praised +
                ", is_attention=" + is_attention +
                '}';
    }

    public int getIs_praised() {
        return is_praised;
    }

    public void setIs_praised(int is_praised) {
        this.is_praised = is_praised;
    }

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }
}
