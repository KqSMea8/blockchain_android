package com.blochchain.shortvideorecord.model;

import java.util.List;

public class MediaRecordBean {
    private String self_media_id;  //自媒体id
    private String nickname;  //昵称
    private String head_pic;  //头像照片
    private String last_record; //最后一条留言
    private String last_time;  //最后一条留言时间
    private List<MessageBean> record_list;  //留言记录
    private String usrs_id; //用户id
    private String nickname_usr; //用户昵称
    private String head_pic_usr; //用户头像照片

    @Override
    public String toString() {
        return "MediaRecordBean{" +
                "self_media_id='" + self_media_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", head_pic='" + head_pic + '\'' +
                ", last_record='" + last_record + '\'' +
                ", last_time='" + last_time + '\'' +
                ", record_list=" + record_list +
                ", usrs_id='" + usrs_id + '\'' +
                ", nickname_usr='" + nickname_usr + '\'' +
                ", head_pic_usr='" + head_pic_usr + '\'' +
                '}';
    }

    public String getSelf_media_id() {
        return self_media_id;
    }

    public void setSelf_media_id(String self_media_id) {
        this.self_media_id = self_media_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getLast_record() {
        return last_record;
    }

    public void setLast_record(String last_record) {
        this.last_record = last_record;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public List<MessageBean> getRecord_list() {
        return record_list;
    }

    public void setRecord_list(List<MessageBean> record_list) {
        this.record_list = record_list;
    }

    public String getUsrs_id() {
        return usrs_id;
    }

    public void setUsrs_id(String usrs_id) {
        this.usrs_id = usrs_id;
    }

    public String getNickname_usr() {
        return nickname_usr;
    }

    public void setNickname_usr(String nickname_usr) {
        this.nickname_usr = nickname_usr;
    }

    public String getHead_pic_usr() {
        return head_pic_usr;
    }

    public void setHead_pic_usr(String head_pic_usr) {
        this.head_pic_usr = head_pic_usr;
    }
}
