package com.blochchain.shortvideorecord.activity;

import android.app.Dialog;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blochchain.shortvideorecord.adapter.PlayDataAdapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.model.PlayDataModel;
import com.blochchain.shortvideorecord.response.GetFirstInfoResponse;
import com.blochchain.shortvideorecord.response.GetPlayDataResponse;
import com.blochchain.shortvideorecord.tabpager.ViewTabBasePager;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class PlayDataActivity extends BaseActivity {
    private static final String TAG = PlayDataActivity.class.getSimpleName();
    private TextView tv_play_amount;  //视频播放总数
    private TextView tv_play_yesterday;  //昨日播放
    private TextView tv_play_ratio;  //视频播放比例
    private TextView tv_search_ratio;  //搜索比例
    private TextView tv_class_ratio;  //类目推荐比例
    private ListView lv_play;  //播放排行
    private List<PlayDataModel> playDataModelList;
    private PlayDataAdapter playDataAdapter;
    private ImageView iv_back;

    private static final int GETDATASUCCESS = 0;
    private static final int GETDATAFAILED = 1;
    private NetUtils netUtils;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private String self_media_id;
    private MyHandler mHandle = new MyHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETDATASUCCESS:  //获取播放数据成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "成功：" + str);
                    GetPlayDataResponse getPlayDataResponse = gson.fromJson(str, GetPlayDataResponse.class);
                    if (getPlayDataResponse != null) {
                        if (getPlayDataResponse.getCode() == 0) {
                            setMessage(getPlayDataResponse);
                        } else {
                            UIUtils.showToastCenter(PlayDataActivity.this, getPlayDataResponse.getMsg());
                        }
                    }
                    break;
                case GETDATAFAILED: //获取播放数据失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    break;
            }
        }
    };

    private void setMessage(GetPlayDataResponse getPlayDataResponse) {
        if (!TextUtils.isEmpty(getPlayDataResponse.getPlay_amount())) {
            tv_play_amount.setText(getPlayDataResponse.getPlay_amount());
        }
        if (!TextUtils.isEmpty(getPlayDataResponse.getPlay_yesterday())) {
            tv_play_yesterday.setText(getPlayDataResponse.getPlay_yesterday());
        }
        String classRatioStr = getPlayDataResponse.getPlay_source_class_ratio();
        String playRatioStr = getPlayDataResponse.getPlay_source_play_ratio();
        String searchRatioStr = getPlayDataResponse.getPlay_source_search_ratio();
        DecimalFormat fnum  =   new  DecimalFormat("##0.00");
        if(!TextUtils.isEmpty(classRatioStr) && !TextUtils.isEmpty(playRatioStr) && !TextUtils.isEmpty(searchRatioStr)){
            float classs = Float.valueOf(classRatioStr);
            float play = Float.valueOf(playRatioStr);
            float serch = Float.valueOf(searchRatioStr);
            float total = classs + play + serch;
            classs = (classs / total) * 100;
            play = (play / total) * 100;
            serch = (serch / total) * 100;
            tv_class_ratio.setText(fnum.format(classs)+"%");
            tv_play_ratio.setText(fnum.format(play)+"%");
            tv_search_ratio.setText(fnum.format(serch)+"%");
        }
        playDataModelList = getPlayDataResponse.getVideo_list();
        if (playDataModelList != null) {
            playDataAdapter = new PlayDataAdapter(PlayDataActivity.this, playDataModelList);
            lv_play.setAdapter(playDataAdapter);
        }
    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_play_data, null);
        setContentView(rootView);

        tv_play_amount = findViewById(R.id.tv_play_amount);
        tv_play_yesterday = findViewById(R.id.tv_play_yesterday);
        tv_play_ratio = findViewById(R.id.tv_play_ratio);
        tv_search_ratio = findViewById(R.id.tv_search_ratio);
        tv_class_ratio = findViewById(R.id.tv_class_ratio);
        lv_play = findViewById(R.id.lv_play);
        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
        return rootView;
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");

//        playDataModelList = new ArrayList<>();
//        playDataModelList.add(new PlayDataModel());
//        playDataModelList.add(new PlayDataModel());
//        playDataModelList.add(new PlayDataModel());
//        playDataModelList.add(new PlayDataModel());
//        playDataModelList.add(new PlayDataModel());
//        playDataModelList.add(new PlayDataModel());
//        playDataAdapter = new PlayDataAdapter(this,playDataModelList);
//        lv_play.setAdapter(playDataAdapter);

        getPlayData();
    }

    private void getPlayData() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        LogUtils.e(TAG + "self_media_id:" + self_media_id);
        netUtils.postDataAsynToNet(Constants.GetPlayDataUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETDATASUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETDATAFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }
}
