package com.blockchain.shortvideoplayer.response;

public class GetCodeResponse extends BaseResponse {
    private int auth_code_id;

    @Override
    public String toString() {
        return "GetCodeResponse{" +
                "auth_code_id=" + auth_code_id +
                '}';
    }

    public int getAuth_code_id() {
        return auth_code_id;
    }

    public void setAuth_code_id(int auth_code_id) {
        this.auth_code_id = auth_code_id;
    }
}
