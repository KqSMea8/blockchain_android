package com.blochchain.shortvideorecord.response;

import com.blochchain.shortvideorecord.model.MediaRecordBean;
import com.blochchain.shortvideorecord.model.MessageBean;

import java.util.List;

public class GetMsgRecordResponse extends BaseResponse {
    private List<MessageBean> sys_record_list;
    private List<MediaRecordBean> usr_record_list;

    @Override
    public String toString() {
        return "GetMsgRecordResponse{" +
                "sys_record_list=" + sys_record_list +
                ", usr_record_list=" + usr_record_list +
                '}';
    }

    public List<MessageBean> getSys_record_list() {
        return sys_record_list;
    }

    public void setSys_record_list(List<MessageBean> sys_record_list) {
        this.sys_record_list = sys_record_list;
    }

    public List<MediaRecordBean> getUsr_record_list() {
        return usr_record_list;
    }

    public void setUsr_record_list(List<MediaRecordBean> usr_record_list) {
        this.usr_record_list = usr_record_list;
    }
}
