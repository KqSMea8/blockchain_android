package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.SearchCommenAdapter;
import com.blockchain.shortvideoplayer.adapter.SelfMediaSearchAdapter;
import com.blockchain.shortvideoplayer.adapter.VideoRecommendAdapter1;
import com.blockchain.shortvideoplayer.adapter.VideoSearchAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.SearchBean;
import com.blockchain.shortvideoplayer.model.SelfMediaBean;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.SearchListResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.RetrofitUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.MyLinearLayoutManager;
import com.blockchain.shortvideoplayer.widget.NoScollListView;
import com.blockchain.shortvideoplayer.widget.NoScrollGridView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import retrofit2.Callback;

public class RecommendSearchActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_search_self;
    private EditText et_search_video;
    private TextView tv_search;

    private LinearLayout ll_video_title;  //视频选项卡总布局
    private TextView tv_video;  //视频标题
    private ImageView iv_video;  //视频下划线
    private LinearLayout ll_self_title; //自媒体选项卡总布局
    private TextView tv_self;   //自媒体标题
    private ImageView iv_self;  //自媒体下划线
    private ListView lv_self_media;  //自媒体列表
    private RecyclerView gv_video;   //视频列表
    private LinearLayout ll_all_list;  //所有自媒体和视频列表的总布局

    private SearchCommenAdapter searchCommenAdapter;
    private List<String> stringList;
    private VideoSearchAdapter videoSearchAdapter;
    private List<VideoBean> videoBeanList;
    private SelfMediaSearchAdapter selfMediaSearchAdapter;
    private List<SelfMediaBean> selfMediaBeanList;
    private VideoRecommendAdapter1 videoRecommendAdapter;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final int GETLISTSUCCESS = 0;
    private static final int GETLISTFAILED = 1;
    private static final int HOTSEARCHSUCCESS = 2;
    private static final int HOTSEARCHFAILED = 3;
    private static final String TAG = RecommendSearchActivity.class.getSimpleName();
    private String user_id;
    private String search_str;
    private int search_type = 1; //搜索类型，1：视频，2：自媒体
    private List<SearchBean> searchBeanList;
    private int screenWidth;


    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_recommend_search, null);
        setContentView(rootView);

        et_search_self = findViewById(R.id.et_search_self);
        et_search_video = findViewById(R.id.et_search_video);
        tv_search = findViewById(R.id.tv_search);

        ll_video_title = findViewById(R.id.ll_video_title);
        tv_video = findViewById(R.id.tv_video);
        iv_video = findViewById(R.id.iv_video);
        ll_self_title = findViewById(R.id.ll_self_title);
        tv_self = findViewById(R.id.tv_self);
        iv_self = findViewById(R.id.iv_self);
        lv_self_media = findViewById(R.id.lv_self_media);
        gv_video = findViewById(R.id.gv_video);
        ll_all_list = findViewById(R.id.ll_all_list);

        ll_video_title.setOnClickListener(this);
        ll_self_title.setOnClickListener(this);
        tv_search.setOnClickListener(this);
//        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if(i == EditorInfo.IME_ACTION_SEARCH){
//                    UIUtils.hideInputWindow(RecommendSearchActivity.this);
//                    getVideoList();
//                }
//                return false;
//            }
//        });
        initData();
        return rootView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        user_id = SharedPrefrenceUtils.getString(this,"usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        Intent intent = getIntent();
        search_str = intent.getStringExtra("searchStr");

        screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）

        MyLinearLayoutManager linearLayoutManager1 = new MyLinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        gv_video.setLayoutManager(linearLayoutManager1);
        videoBeanList = new ArrayList<>();
        videoRecommendAdapter = new VideoRecommendAdapter1(this,videoBeanList,screenWidth);
        gv_video.setAdapter(videoRecommendAdapter);
        getVideoList();

//        gv_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                VideoBean videoBean = videoBeanList.get(i);
//                Intent intent = new Intent(RecommendSearchActivity.this, VideoPlayActivity.class);
//                intent.putExtra("video_id",videoBean.getVideo_id());
//                intent.putExtra("videoListStr",gson.toJson(videoBeanList));
//                intent.putExtra("postion",i);
//                UIUtils.startActivity(intent);
//
//                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
//            }
//        });

        lv_self_media.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SelfMediaBean selfMediaBean = selfMediaBeanList.get(i);
                Intent intent = new Intent(RecommendSearchActivity.this, SelfMediaDetailActivity.class);
                intent.putExtra("self_media_id",selfMediaBean.getSelf_media_id());
                intent.putExtra("fromSource",1);
                UIUtils.startActivity(intent);
                overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
            }
        });
    }

    private void getVideoList() {
        search_str = et_search_video.getText().toString();
        loadingDialog.show();
        retrofit2.Call<SearchListResponse> call = RetrofitUtils.getInstance().searchVideoList(user_id,search_str,search_type);
        call.enqueue(new Callback<SearchListResponse>() {

            @Override
            public void onResponse(retrofit2.Call<SearchListResponse> call, retrofit2.Response<SearchListResponse> response) {
                loadingDialog.dismiss();
                LogUtils.e(TAG + "--搜索视频:" + response.body());
                SearchListResponse searchListResponse = response.body();
                if (searchListResponse != null) {
                    if (searchListResponse.getCode() == 0) {
                        List<VideoBean> videoBeans= searchListResponse.getVideo_list();
                        if (videoBeans != null) {
//                            videoSearchAdapter = new VideoSearchAdapter(RecommendSearchActivity.this, videoBeanList);
//                            gv_video.setAdapter(videoSearchAdapter);
                            videoBeanList.clear();
                            videoRecommendAdapter.addDataAt(0,videoBeans);
                        }
                        selfMediaBeanList = searchListResponse.getSelf_media_list();
                        if (selfMediaBeanList != null) {
                                selfMediaSearchAdapter = new SelfMediaSearchAdapter(RecommendSearchActivity.this, selfMediaBeanList);
                                lv_self_media.setAdapter(selfMediaSearchAdapter);
                        } else {
                            UIUtils.showToastCenter(RecommendSearchActivity.this, searchListResponse.getMsg());
                        }
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SearchListResponse> call, Throwable t) {
                LogUtils.e(TAG +"--搜索视频报错"+ t.getMessage());
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_video_title:
                lv_self_media.setVisibility(View.GONE);
                gv_video.setVisibility(View.VISIBLE);
                search_type = 1;
                changeTitle(0);
                getVideoList();
                break;
            case R.id.ll_self_title:
                lv_self_media.setVisibility(View.VISIBLE);
                gv_video.setVisibility(View.GONE);
                search_type = 2;
                changeTitle(1);
                getVideoList();
                break;
            case R.id.tv_search:  //搜索
                ll_all_list.setVisibility(View.VISIBLE);
                getVideoList();
                break;
        }

    }


    private void changeTitle(int i) {
        switch (i) {
            case 0:
                tv_video.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                tv_self.setTextColor(UIUtils.getColor(R.color.text_color_deep_gray));
                iv_video.setVisibility(View.VISIBLE);
                iv_self.setVisibility(View.INVISIBLE);
                break;
            case 1:
                tv_video.setTextColor(UIUtils.getColor(R.color.text_color_deep_gray));
                tv_self.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                iv_video.setVisibility(View.INVISIBLE);
                iv_self.setVisibility(View.VISIBLE);
                break;
        }
    }
}
