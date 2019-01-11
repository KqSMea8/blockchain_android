package com.blochchain.shortvideorecord.model;

public class ClassItemModel {
    private String first_class_id;  //一级目录
    private String first_class_name;
    private String second_class_id;  //二级目录
    private String second_class_name;

    @Override
    public String toString() {
        return "ClassItemModel{" +
                "first_class_id='" + first_class_id + '\'' +
                ", first_class_name='" + first_class_name + '\'' +
                ", second_class_id='" + second_class_id + '\'' +
                ", second_class_name='" + second_class_name + '\'' +
                '}';
    }

    public String getFirst_class_id() {
        return first_class_id;
    }

    public void setFirst_class_id(String first_class_id) {
        this.first_class_id = first_class_id;
    }

    public String getFirst_class_name() {
        return first_class_name;
    }

    public void setFirst_class_name(String first_class_name) {
        this.first_class_name = first_class_name;
    }

    public String getSecond_class_id() {
        return second_class_id;
    }

    public void setSecond_class_id(String second_class_id) {
        this.second_class_id = second_class_id;
    }

    public String getSecond_class_name() {
        return second_class_name;
    }

    public void setSecond_class_name(String second_class_name) {
        this.second_class_name = second_class_name;
    }
}
