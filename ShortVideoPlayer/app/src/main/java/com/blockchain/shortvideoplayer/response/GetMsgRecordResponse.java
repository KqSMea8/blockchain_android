package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.MediaRecordBean;
import com.blockchain.shortvideoplayer.model.MessageBean;

import java.util.List;

public class GetMsgRecordResponse extends BaseResponse {
    private List<MessageBean> sys_record_list;
    private List<MediaRecordBean> media_record_list;

    @Override
    public String toString() {
        return "GetMsgRecordResponse{" +
                "sys_record_list=" + sys_record_list +
                ", media_record_list=" + media_record_list +
                '}';
    }

    public List<MessageBean> getSys_record_list() {
        return sys_record_list;
    }

    public void setSys_record_list(List<MessageBean> sys_record_list) {
        this.sys_record_list = sys_record_list;
    }

    public List<MediaRecordBean> getMedia_record_list() {
        return media_record_list;
    }

    public void setMedia_record_list(List<MediaRecordBean> media_record_list) {
        this.media_record_list = media_record_list;
    }
}
