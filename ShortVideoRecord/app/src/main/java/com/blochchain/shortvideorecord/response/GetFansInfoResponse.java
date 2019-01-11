package com.blochchain.shortvideorecord.response;

public class GetFansInfoResponse extends BaseResponse {
    private String fans_man_ratio; //男粉丝比例
    private String fans_girl_ratio; //女粉丝比例
    private String fans_source_play_ratio; //粉丝来源比例，播放
    private String fans_source_search_ratio; //粉丝来源比例，搜索
    private String fans_source_class_ratio; //粉丝来源比例，类目推荐

    @Override
    public String toString() {
        return "GetFansInfoResponse{" +
                "fans_man_ratio='" + fans_man_ratio + '\'' +
                ", fans_girl_ratio='" + fans_girl_ratio + '\'' +
                ", fans_source_play_ratio='" + fans_source_play_ratio + '\'' +
                ", fans_source_search_ratio='" + fans_source_search_ratio + '\'' +
                ", fans_source_class_ratio='" + fans_source_class_ratio + '\'' +
                '}';
    }

    public String getFans_man_ratio() {
        return fans_man_ratio;
    }

    public void setFans_man_ratio(String fans_man_ratio) {
        this.fans_man_ratio = fans_man_ratio;
    }

    public String getFans_girl_ratio() {
        return fans_girl_ratio;
    }

    public void setFans_girl_ratio(String fans_girl_ratio) {
        this.fans_girl_ratio = fans_girl_ratio;
    }

    public String getFans_source_play_ratio() {
        return fans_source_play_ratio;
    }

    public void setFans_source_play_ratio(String fans_source_play_ratio) {
        this.fans_source_play_ratio = fans_source_play_ratio;
    }

    public String getFans_source_search_ratio() {
        return fans_source_search_ratio;
    }

    public void setFans_source_search_ratio(String fans_source_search_ratio) {
        this.fans_source_search_ratio = fans_source_search_ratio;
    }

    public String getFans_source_class_ratio() {
        return fans_source_class_ratio;
    }

    public void setFans_source_class_ratio(String fans_source_class_ratio) {
        this.fans_source_class_ratio = fans_source_class_ratio;
    }
}
