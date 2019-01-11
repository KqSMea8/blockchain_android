package com.blochchain.shortvideorecord.tabpager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.blochchain.shortvideorecord.activity.EditVideoActivity;
import com.blochchain.shortvideorecord.activity.Main2Activity;
import com.blochchain.shortvideorecord.adapter.MyProductAdapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.model.MyProductModel;
import com.blochchain.shortvideorecord.response.GetFirstInfoResponse;
import com.blochchain.shortvideorecord.response.GetVideoListResponse;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blochchain.shortvideorecord.utils.Utils;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;


/**
 * @作者: 刘敏
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 详情页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class TabProductPager extends ViewTabBasePager {
    private static final String TAG = TabProductPager.class.getSimpleName();
    private GridView lv_products;
    private List<MyProductModel> myProductModelList;
    private MyProductAdapter myProductAdapter;
    private static final int GETVIDEOLISTSUCCESS = 0;
    private static final int GETVIDEOLISTFAILED = 1;
    private NetUtils netUtils;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private String self_media_id;
    private String video_id;
    private MyTabHandler mHandle = new MyTabHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETVIDEOLISTSUCCESS:  //获取作品成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "成功：" + str);
                    GetVideoListResponse getVideoListResponse = gson.fromJson(str,GetVideoListResponse.class);
                    if(getVideoListResponse != null){
                        if(getVideoListResponse.getCode() == 0){
                            setMessage(getVideoListResponse);
                        }else {
                            UIUtils.showToastCenter(mContext,getVideoListResponse.getMsg());
                        }
                    }
                    break;
                case GETVIDEOLISTFAILED: //获取作品列表失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    break;
            }
        }
    };

    private void setMessage(GetVideoListResponse getVideoListResponse) {
        myProductModelList = getVideoListResponse.getVideo_list();
        if(myProductModelList != null){
            myProductAdapter = new MyProductAdapter(mContext,myProductModelList);
            lv_products.setAdapter(myProductAdapter);
        }
    }

    public TabProductPager(Context context) {
        super(context);
    }

    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.pager_product, null);

        lv_products = view.findViewById(R.id.lv_products);
        return view;
    }

    @Override
    public void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(mContext, true);
        self_media_id = SharedPrefrenceUtils.getString(mContext,"self_media_id");

        getVideoList();

        lv_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                video_id = myProductModelList.get(i).getVideo_id();
                Intent intent = new Intent(mContext, EditVideoActivity.class);
                if(!TextUtils.isEmpty(video_id)){
                    intent.putExtra("video_id",video_id);
                }
                UIUtils.startActivityForResult(intent,Main2Activity.EDITVIDEO);
            }
        });

    }

    private void getVideoList() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(self_media_id)){
            reqBody.put("self_media_id",self_media_id);
        }
        netUtils.postDataAsynToNet(Constants.GetVideoListUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETVIDEOLISTSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETVIDEOLISTFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }


}