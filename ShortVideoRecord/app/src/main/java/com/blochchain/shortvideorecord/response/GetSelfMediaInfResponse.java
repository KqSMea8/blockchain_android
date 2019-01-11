package com.blochchain.shortvideorecord.response;

public class GetSelfMediaInfResponse extends BaseResponse {
    private String tel;  //手机号
    private String nickname ;  //昵称
    private String self_intr ;  //自我介绍
    private int sex ;  //性别，0=男，1=女
    private String birthday;  //生日
    private int education_id;  //学历ID 1 初中，  2 高中  ，3 大专 ，  4 本科及以上
    private String head_pic;  //头像照片
    private String attention_amount;  //关注，被收藏视频数
    private String fans_amount;  //粉丝，被被订阅
    private String praised_amount;  //被赞视频数
    private String bank_card;  //银行卡
    private String real_name;  //真实姓名
    private String id_card;  //身份证号码
    private String id_card_pic;  //身份证正面照
    private String id_card_hold_pic;  //手持身份证照
    private String class_id;  //所属类目
    private String class_name; //类目名称
    private String bank_name;  //支行名称

    @Override
    public String toString() {
        return "GetSelfMediaInfResponse{" +
                "tel='" + tel + '\'' +
                ", nickname='" + nickname + '\'' +
                ", self_intr='" + self_intr + '\'' +
                ", sex=" + sex +
                ", birthday='" + birthday + '\'' +
                ", education_id=" + education_id +
                ", head_pic='" + head_pic + '\'' +
                ", attention_amount='" + attention_amount + '\'' +
                ", fans_amount='" + fans_amount + '\'' +
                ", praised_amount='" + praised_amount + '\'' +
                ", bank_card='" + bank_card + '\'' +
                ", real_name='" + real_name + '\'' +
                ", id_card='" + id_card + '\'' +
                ", id_card_pic='" + id_card_pic + '\'' +
                ", id_card_hold_pic='" + id_card_hold_pic + '\'' +
                ", class_id='" + class_id + '\'' +
                ", class_name='" + class_name + '\'' +
                ", bank_name='" + bank_name + '\'' +
                '}';
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSelf_intr() {
        return self_intr;
    }

    public void setSelf_intr(String self_intr) {
        this.self_intr = self_intr;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getEducation_id() {
        return education_id;
    }

    public void setEducation_id(int education_id) {
        this.education_id = education_id;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getAttention_amount() {
        return attention_amount;
    }

    public void setAttention_amount(String attention_amount) {
        this.attention_amount = attention_amount;
    }

    public String getFans_amount() {
        return fans_amount;
    }

    public void setFans_amount(String fans_amount) {
        this.fans_amount = fans_amount;
    }

    public String getPraised_amount() {
        return praised_amount;
    }

    public void setPraised_amount(String praised_amount) {
        this.praised_amount = praised_amount;
    }

    public String getBank_card() {
        return bank_card;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getId_card_pic() {
        return id_card_pic;
    }

    public void setId_card_pic(String id_card_pic) {
        this.id_card_pic = id_card_pic;
    }

    public String getId_card_hold_pic() {
        return id_card_hold_pic;
    }

    public void setId_card_hold_pic(String id_card_hold_pic) {
        this.id_card_hold_pic = id_card_hold_pic;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }
}
