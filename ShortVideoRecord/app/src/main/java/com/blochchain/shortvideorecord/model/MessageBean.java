package com.blochchain.shortvideorecord.model;

public class MessageBean {
    private String msg_time; //时间
    private String msg_content;  //留言内容
    private int msg_type;  //留言类型，0：用户给自媒体留言，1：自媒体给用户留言，2：系统给用户留言，3：系统给自媒体留言。

    @Override
    public String toString() {
        return "MessageBean{" +
                "msg_time='" + msg_time + '\'' +
                ", msg_content='" + msg_content + '\'' +
                ", msg_type=" + msg_type +
                '}';
    }

    public String getMsg_time() {
        return msg_time;
    }

    public void setMsg_time(String msg_time) {
        this.msg_time = msg_time;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }
}
