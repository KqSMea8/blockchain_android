package com.blochchain.shortvideorecord.model;

public class FansPlaceModel {
    private String place;
    private String ratio;

    public FansPlaceModel(String place, String ratio) {
        this.place = place;
        this.ratio = ratio;
    }

    @Override
    public String toString() {
        return "FansPlaceModel{" +
                "place='" + place + '\'' +
                ", ratio='" + ratio + '\'' +
                '}';
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }
}
