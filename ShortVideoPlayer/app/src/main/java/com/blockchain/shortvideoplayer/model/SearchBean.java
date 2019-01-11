package com.blockchain.shortvideoplayer.model;

public class SearchBean {
    private String search_name;
    private String search_cnt;
    private int id;

    @Override
    public String toString() {
        return "SearchBean{" +
                "search_name='" + search_name + '\'' +
                ", search_cnt='" + search_cnt + '\'' +
                ", id=" + id +
                '}';
    }

    public String getSearch_name() {
        return search_name;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }

    public String getSearch_cnt() {
        return search_cnt;
    }

    public void setSearch_cnt(String search_cnt) {
        this.search_cnt = search_cnt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
