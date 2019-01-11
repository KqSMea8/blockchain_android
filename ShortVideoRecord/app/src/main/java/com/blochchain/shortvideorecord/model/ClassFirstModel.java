package com.blochchain.shortvideorecord.model;

import java.util.List;

public class ClassFirstModel {
    private String class_id;
    private String class_name;
    private String pic;
    private List<ClassModel> class_2_list;

    @Override
    public String toString() {
        return "ClassFirstModel{" +
                "class_id='" + class_id + '\'' +
                ", class_name='" + class_name + '\'' +
                ", pic='" + pic + '\'' +
                ", class_2_list=" + class_2_list +
                '}';
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<ClassModel> getClass_2_list() {
        return class_2_list;
    }

    public void setClass_2_list(List<ClassModel> class_2_list) {
        this.class_2_list = class_2_list;
    }
}
