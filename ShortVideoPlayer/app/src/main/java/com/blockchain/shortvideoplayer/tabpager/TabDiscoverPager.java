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
import android.widget.GridView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.ClassListActivity;
import com.blockchain.shortvideoplayer.activity.RecommendSearchActivity;
import com.blockchain.shortvideoplayer.adapter.ClassFirstAdapter;
import com.blockchain.shortvideoplayer.adapter.SearchAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.ClassFirstModel;
import com.blockchain.shortvideoplayer.model.SearchBean;
import com.blockchain.shortvideoplayer.response.GetClassFirstListResponse;
import com.blockchain.shortvideoplayer.response.GetHotSearchResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.RetrofitUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @作者: 刘敏
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 详情页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class TabDiscoverPager extends ViewTabBasePager implements View.OnClickListener{
    private EditText et_search_self; //自媒体
    private EditText et_search_video; //视频
    private GridView gv_discover;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final int GETLISTSUCCESS = 0;
    private static final int GETLISTFAILED = 1;
    private static final String TAG = TabDiscoverPager.class.getSimpleName();
    private String user_id;
    private List<ClassFirstModel> classModelList;
    private ClassFirstAdapter classAdapter;
    private String searchStr;
    private TextView tv_search;
    private List<SearchBean> searchBeanList;
    private SearchAdapter searchAdapter;


    public TabDiscoverPager(Context context) {
        super(context);
    }


    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.fragment_discover, null);
        et_search_self = view.findViewById(R.id.et_search_self);
        et_search_video = view.findViewById(R.id.et_search_video);
        gv_discover = view.findViewById(R.id.gv_discover);
        tv_search = view.findViewById(R.id.tv_search);

        tv_search.setOnClickListener(this);
        et_search_self.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    UIUtils.hideInputWindow((Activity) mContext);
//                    search();
                    searchStr = et_search_self.getText().toString();
                    if(!TextUtils.isEmpty(searchStr)){
                        et_search_self.setText("");
                        Intent intent = new Intent(mContext,RecommendSearchActivity.class);
                        intent.putExtra("searchStr",searchStr);
                        UIUtils.startActivity(intent);
                    }
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void initData() {
        user_id = SharedPrefrenceUtils.getString(mContext,"usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);

        getHotSearch();

//        DialogUtils.showRecommendAttentionDialog(mContext,classModelList,this);


//        getAllClassList();
        gv_discover.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               SearchBean searchBean = searchBeanList.get(i);
                Intent intent = new Intent(mContext,RecommendSearchActivity.class);
                if(!TextUtils.isEmpty(searchStr)){
                    intent.putExtra("searchStr",searchBean.getSearch_name());
                }
                UIUtils.startActivity(intent);
            }
        });
    }

    private void getHotSearch() {
        loadingDialog.show();
        Call<GetHotSearchResponse> call = RetrofitUtils.getInstance().getHotSearch();
        call.enqueue(new Callback<GetHotSearchResponse>() {
            @Override
            public void onResponse(Call<GetHotSearchResponse> call, Response<GetHotSearchResponse> response) {
                loadingDialog.dismiss();
                LogUtils.e(TAG +"--获取热门搜索:"+ response.body());
                GetHotSearchResponse getHotSearchResponse = response.body();
                if(getHotSearchResponse != null){
                    searchBeanList = getHotSearchResponse.getHot_search_list();
                    if(searchBeanList != null){
                        searchAdapter = new SearchAdapter(mContext,searchBeanList);
                        gv_discover.setAdapter(searchAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetHotSearchResponse> call, Throwable t) {
                LogUtils.e(TAG +"--获取热门搜索报错"+ t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_search:
                searchStr = et_search_self.getText().toString();
//                search();
                Intent intent = new Intent(mContext,RecommendSearchActivity.class);
                if(!TextUtils.isEmpty(searchStr)){
                    intent.putExtra("searchStr",searchStr);
                }
                UIUtils.startActivity(intent);
                break;
        }
    }

    private void search() {
        searchStr = et_search_self.getText().toString();
        if(!TextUtils.isEmpty(searchStr)){
            List<ClassFirstModel> classModelList1 = new ArrayList<>();
            if(classModelList != null && classModelList.size()>0){
                for(ClassFirstModel classModel: classModelList){
                    String str = classModel.getClass_name();
                    if(!TextUtils.isEmpty(str)){
                        if(str.contains(searchStr)){
                            classModelList1.add(classModel);
                        }
                    }
                }
            }
            classAdapter = new ClassFirstAdapter(mContext,classModelList1);
            gv_discover.setAdapter(classAdapter);
        }else {
            classAdapter = new ClassFirstAdapter(mContext,classModelList);
            gv_discover.setAdapter(classAdapter);
        }
    }
}