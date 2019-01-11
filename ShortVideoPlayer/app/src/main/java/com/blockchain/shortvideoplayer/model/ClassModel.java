package com.blockchain.shortvideoplayer.model;

public class ClassModel {
    private String class_id;
    private String class_name;
    private String pic;
    private String class_describe;
    private boolean isSelected;

    @Override
    public String toString() {
        return "ClassModel{" +
                "class_id='" + class_id + '\'' +
                ", class_name='" + class_name + '\'' +
                ", pic='" + pic + '\'' +
                ", class_describe='" + class_describe + '\'' +
                ", isSelected=" + isSelected +
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

    public String getClass_describe() {
        return class_describe;
    }

    public void setClass_describe(String class_describe) {
        this.class_describe = class_describe;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
