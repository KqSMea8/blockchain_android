package com.blockchain.shortvideoplayer.response;

import com.blockchain.shortvideoplayer.model.ClassFirstModel;

import java.util.List;

public class GetClassFirstListResponse extends BaseResponse {
    private List<ClassFirstModel> class_1_list;

    @Override
    public String toString() {
        return "GetClassFirstListResponse{" +
                "class_1_list=" + class_1_list +
                '}';
    }

    public List<ClassFirstModel> getClass_1_list() {
        return class_1_list;
    }

    public void setClass_1_list(List<ClassFirstModel> class_1_list) {
        this.class_1_list = class_1_list;
    }
}
