package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.ClassModel;

import java.util.List;

public class GetClassListResponse extends BaseResponse {
    private List<ClassModel> class_list;

    @Override
    public String toString() {
        return "GetClassListResponse{" +
                "class_list=" + class_list +
                '}';
    }

    public List<ClassModel> getClass_list() {
        return class_list;
    }

    public void setClass_list(List<ClassModel> class_list) {
        this.class_list = class_list;
    }
}
