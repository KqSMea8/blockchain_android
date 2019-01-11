package com.blockchain.shortvideoplayer.tabpager;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.Main2Activity;
import com.blockchain.shortvideoplayer.adapter.ClassRecyclerAdapter;
import com.blockchain.shortvideoplayer.adapter.VideoRecommendAdapter;
import com.blockchain.shortvideoplayer.adapter.VideoRecommendAdapter1;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.GetClassListResponse;
import com.blockchain.shortvideoplayer.response.GetRecommendVideoListResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.RetrofitUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.MyLinearLayoutManager;
import com.google.gson.Gson;

import java.util.ArrayList;
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
public class TabRecommendPager extends ViewTabBasePager implements View.OnClickListener{
    private static final String TAG = TabRecommendPager.class.getSimpleName();
    private View view;
    private Gson gson;
    private Dialog loadingDialog;
    private String user_id;
    private ControlTabFragment ctf;

    private TextView tv_hot;
    private RecyclerView rcv_class;
    private RelativeLayout rl_add;
    private RecyclerView lv_videos;

    private ClassRecyclerAdapter classRecyclerAdapter;
    private List<ClassModel> classModelList;

    private List<VideoBean> videoBeanList;
    private VideoRecommendAdapter1 videoRecommendAdapter;

    private int screenWidth;

    public TabRecommendPager(Context context) {
        super(context);
    }


    @Override
    protected View initView() {
        view = View.inflate(mContext,
                R.layout.fragment_recommend, null);
        tv_hot = view.findViewById(R.id.tv_hot);
        rcv_class = view.findViewById(R.id.rcv_class);
        rl_add = view.findViewById(R.id.rl_add);
        lv_videos = view.findViewById(R.id.lv_videos);


        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        user_id = SharedPrefrenceUtils.getString(mContext,"usrs_id");

        if(ctf == null){
            ctf = Main2Activity.getCtf();
        }

        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcv_class.setLayoutManager(linearLayoutManager);

        screenWidth = ctf.getActivity().getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）

        classModelList = new ArrayList<>();
        classRecyclerAdapter = new ClassRecyclerAdapter(mContext, classModelList);
        rcv_class.setAdapter(classRecyclerAdapter);

        MyLinearLayoutManager linearLayoutManager1 = new MyLinearLayoutManager(mContext);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        lv_videos.setLayoutManager(linearLayoutManager1);
        videoBeanList = new ArrayList<>();
        videoRecommendAdapter = new VideoRecommendAdapter1(mContext,videoBeanList,screenWidth);
        lv_videos.setAdapter(videoRecommendAdapter);

        getSubscribeList();

        getRecommendList();
        return view;
    }

    private void getRecommendList() {
        loadingDialog.show();
        Call<GetRecommendVideoListResponse> call = RetrofitUtils.getInstance().getRecommendVideoList("0");
        call.enqueue(new Callback<GetRecommendVideoListResponse>() {

            @Override
            public void onResponse(Call<GetRecommendVideoListResponse> call, Response<GetRecommendVideoListResponse> response) {
                loadingDialog.dismiss();
                LogUtils.e(TAG +"--获取推荐列表:"+ response.body());
                GetRecommendVideoListResponse getRecommendVideoListResponse = response.body();
                if(getRecommendVideoListResponse != null){
                    if(getRecommendVideoListResponse.getCode() == 0){
                        List<VideoBean> videoBeans = getRecommendVideoListResponse.getData();
                        if(videoBeans != null){
//                            videoRecommendAdapter = new VideoRecommendAdapter(mContext,videoBeanList,screenWidth);
//                            lv_videos.setAdapter(videoRecommendAdapter);
                            videoBeanList.clear();
                            videoRecommendAdapter.addDataAt(0,videoBeans);
                        }
                    }else {
                        UIUtils.showToastCenter(mContext,getRecommendVideoListResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetRecommendVideoListResponse> call, Throwable t) {
                LogUtils.e(TAG +"--获取推荐列表报错"+ t.getMessage());
            }
        });
    }

    private void getSubscribeList() {
        loadingDialog.show();
        Call<GetClassListResponse> call = RetrofitUtils.getInstance().getSubscribeList(user_id);
        call.enqueue(new Callback<GetClassListResponse>() {

            @Override
            public void onResponse(Call<GetClassListResponse> call, Response<GetClassListResponse> response) {
                loadingDialog.dismiss();
                LogUtils.e(TAG+"--订阅类目" + response.body());
                GetClassListResponse getClassListResponse = response.body();
                if(getClassListResponse != null){
                    if(getClassListResponse.getCode() == 0){
                        List<ClassModel> classModels = getClassListResponse.getClass_list();
                        if(classModels != null){
                            classModelList.clear();
                            classRecyclerAdapter.addDataAt(0,classModels);
                        }
                    }else {
                        UIUtils.showToastCenter(mContext,getClassListResponse.getMsg());
                    }
                }

            }

            @Override
            public void onFailure(Call<GetClassListResponse> call, Throwable t) {
                loadingDialog.dismiss();
                LogUtils.e(TAG + t.getMessage());
            }
        });
    }

    @Override
    public void initData() {

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }


}