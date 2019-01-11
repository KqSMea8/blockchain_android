package com.blockchain.shortvideoplayer.tabpager;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.Main2Activity;
import com.blockchain.shortvideoplayer.activity.MySubscribeActivity;
import com.blockchain.shortvideoplayer.activity.RecommendSearchActivity;
import com.blockchain.shortvideoplayer.activity.SelfMediaDetailActivity;
import com.blockchain.shortvideoplayer.adapter.SelfMediaSearchAdapter;
import com.blockchain.shortvideoplayer.adapter.UserSubVideoAdapter;
import com.blockchain.shortvideoplayer.fragment.SubscribeFragment;
import com.blockchain.shortvideoplayer.model.SelfMediaBean;
import com.blockchain.shortvideoplayer.model.UserSubVideoBean;
import com.blockchain.shortvideoplayer.response.GetSubscribSelfMediaListResponse;
import com.blockchain.shortvideoplayer.response.GetUserSubVideoListResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.RetrofitUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @作者: 刘敏
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 详情页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class TabSubscribePager extends ViewTabBasePager implements View.OnClickListener{
    private SubscribeFragment.OnFragmentInteractionListener mListener;

    private ListView lv_self_media;

    //接口请求菊花
    private Dialog loadingDialog;

    private static final String TAG = TabSubscribePager.class.getSimpleName();
    private String user_id;
    private int pageNo;
    private int pageSize;
    private List<UserSubVideoBean> userSubVideoBeanList;
    private UserSubVideoAdapter userSubVideoAdapter;
    private TextView my_subscribe;
    private int screenWidth;
    private ControlTabFragment ctf;

    public TabSubscribePager(Context context) {
        super(context);
    }


    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.fragment_subscribe, null);
        lv_self_media = view.findViewById(R.id.lv_self_media);
        my_subscribe = view.findViewById(R.id.my_subscribe);

        my_subscribe.setOnClickListener(this);
        return view;
    }

    @Override
    public void initData() {
        if(ctf == null){
            ctf = Main2Activity.getCtf();
        }
        user_id = SharedPrefrenceUtils.getString(mContext,"usrs_id");
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        pageNo = 1;
        pageSize = 10;

        screenWidth = ctf.getActivity().getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
        if(!TextUtils.isEmpty(user_id)){
            getVideoList();
        }

        lv_self_media.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserSubVideoBean userSubVideoBean = userSubVideoBeanList.get(i);
                Intent intent = new Intent(mContext, SelfMediaDetailActivity.class);
                intent.putExtra("self_media_id",userSubVideoBean.getSelf_media_id());
                UIUtils.startActivity(intent);
            }
        });
    }

    private void getVideoList() {
        loadingDialog.show();
        Call<GetUserSubVideoListResponse> call = RetrofitUtils.getInstance().getUserSubVideoList(user_id,pageNo,pageSize);
        call.enqueue(new Callback<GetUserSubVideoListResponse>() {
            @Override
            public void onResponse(Call<GetUserSubVideoListResponse> call, Response<GetUserSubVideoListResponse> response) {
                loadingDialog.dismiss();
                LogUtils.e(TAG +"--获取用户订阅视频列表:"+ response.body());
                GetUserSubVideoListResponse getUserSubVideoListResponse = response.body();
                if(getUserSubVideoListResponse != null){
                    if(getUserSubVideoListResponse.getCode() == 0){
                        userSubVideoBeanList = getUserSubVideoListResponse.getUserSubVideoList();
                        userSubVideoAdapter = new UserSubVideoAdapter(mContext,userSubVideoBeanList,screenWidth);
                        lv_self_media.setAdapter(userSubVideoAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserSubVideoListResponse> call, Throwable t) {
                LogUtils.e(TAG +"--获取用户订阅视频列表报错"+ t.getMessage());
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.my_subscribe:
                Intent intent = new Intent(mContext, MySubscribeActivity.class);
                UIUtils.startActivity(intent);
                break;
        }

    }

}