package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.response.GetSelfMediaInfoResponse;
import com.blockchain.shortvideoplayer.response.GetSubscribSelfMediaListResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.RetrofitUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySubscribeActivity extends BaseActivity {
    private static final String TAG = MySubscribeActivity.class.getSimpleName();
    private String user_id;
    //接口请求菊花
    private Dialog loadingDialog;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_my_subscribe, null);
        setContentView(rootView);

        initData();
        return rootView;
    }

    private void initData() {
        user_id = SharedPrefrenceUtils.getString(this,"usrs_id");
        loadingDialog = DialogUtils.createLoadDialog(this, true);

        getSubscribeSelfList();
    }

    private void getSubscribeSelfList() {
        loadingDialog.show();
        Call<GetSubscribSelfMediaListResponse> call = RetrofitUtils.getInstance().getSubscribeSelfList(user_id);
        call.enqueue(new Callback<GetSubscribSelfMediaListResponse>() {
            @Override
            public void onResponse(Call<GetSubscribSelfMediaListResponse> call, Response<GetSubscribSelfMediaListResponse> response) {
                loadingDialog.dismiss();
                LogUtils.e(TAG +"--订阅自媒体列表:"+ response.body());
            }

            @Override
            public void onFailure(Call<GetSubscribSelfMediaListResponse> call, Throwable t) {
                LogUtils.e(TAG +"--订阅自媒体列表报错"+ t.getMessage());
            }
        });

    }
}
