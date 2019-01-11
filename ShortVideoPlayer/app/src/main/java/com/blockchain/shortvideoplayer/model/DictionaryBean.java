package com.blockchain.shortvideoplayer.model;

/**
 * Created by Administrator on 2016/12/3 0003.
 */
public class DictionaryBean {
    private int dictionary_value_key;
    private String dictionary_value_name;

    @Override
    public String toString() {
        return "DictionaryBean{" +
                "dictionary_value_key=" + dictionary_value_key +
                ", dictionary_value_name='" + dictionary_value_name + '\'' +
                '}';
    }

    public int getDictionary_value_key() {
        return dictionary_value_key;
    }

    public void setDictionary_value_key(int dictionary_value_key) {
        this.dictionary_value_key = dictionary_value_key;
    }

    public String getDictionary_value_name() {
        return dictionary_value_name;
    }

    public void setDictionary_value_name(String dictionary_value_name) {
        this.dictionary_value_name = dictionary_value_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DictionaryBean that = (DictionaryBean) o;

        return dictionary_value_key == that.dictionary_value_key;

    }

    @Override
    public int hashCode() {
        return dictionary_value_key;
    }
}
