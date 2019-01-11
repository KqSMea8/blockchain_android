package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.VideoSearchAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetSelfMediaInfoResponse;
import com.blockchain.shortvideoplayer.response.IsSubcribedResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.NoScrollGridView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class SelfMediaDetailActivity extends BaseActivity implements View.OnClickListener {
    private NoScrollGridView gv_newest;
    private List<VideoBean> videoBeanList;
    private VideoSearchAdapter videoSearchAdapter;
    private ImageView iv_back;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final int GETINFOSUCCESS = 0;
    private static final int GETINFOFAILED = 1;
    private static final int SUBSCRIBESUCCESS = 2;
    private static final int SUBSCRIBEFAILED = 3;
    private static final int ISSUBSCRIBESUCCESS = 4;
    private static final int ISSUBSCRIBEFAILED = 5;
    private static final String TAG = SelfMediaDetailActivity.class.getSimpleName();
    private String user_id;
    private String self_media_id;
    private GetSelfMediaInfoResponse getSelfMediaInfoResponse;
    private ImageView iv_head;
    private LinearLayout ll_send_message; //发消息
    private LinearLayout ll_subscribe; //订阅
    private TextView tv_nickname;  //昵称
    private ImageView iv_sex;  //性别
    private TextView tv_describe; //简介
    private TextView tv_fans;  //粉丝
    private TextView tv_attention; //收藏
    private TextView tv_praise;  //点赞
    private boolean isSubscribed;  //是否订阅
    private TextView tv_subscribe;
    private int fromSource;

    private MyHandler mHandller = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case GETINFOSUCCESS:  //获取详情成功
                    String string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取详情成功："+string);
                    getSelfMediaInfoResponse = gson.fromJson(string,GetSelfMediaInfoResponse.class);
                    if(getSelfMediaInfoResponse != null){
                        if(getSelfMediaInfoResponse.getCode() == 0){
                            setMessage();
                        }else {
                            UIUtils.showToastCenter(SelfMediaDetailActivity.this,getSelfMediaInfoResponse.getMsg());
                        }
                    }
                    break;
                case GETINFOFAILED: //获取详情失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取详情失败："+e.getMessage());
                    UIUtils.showToastCenter(SelfMediaDetailActivity.this,e.getMessage());
                    break;
                case SUBSCRIBESUCCESS: //订阅自媒体成功
                    String string1 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"订阅自媒体成功："+string1);
                    BaseResponse baseResponse = gson.fromJson(string1,BaseResponse.class);
                    if(baseResponse.getCode() == 0){
                        UIUtils.showToastCenter(SelfMediaDetailActivity.this,"订阅成功");
                        isSubscribSelfMedia();
                    }else {
                        UIUtils.showToastCenter(SelfMediaDetailActivity.this,baseResponse.getMsg());
                    }
                    break;
                case SUBSCRIBEFAILED: //订阅自媒体失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"订阅自媒体失败："+e1.getMessage());
                    UIUtils.showToastCenter(SelfMediaDetailActivity.this,e1.getMessage());
                    break;
                case ISSUBSCRIBESUCCESS: //是否订阅自媒体成功
                    String string2 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"是否订阅自媒体成功："+string2);
                    IsSubcribedResponse isSubcribedResponse = gson.fromJson(string2,IsSubcribedResponse.class);
                    if(isSubcribedResponse != null){
                        if(isSubcribedResponse.getCode() == 0){
                            if(isSubcribedResponse.getIs_subscribe() == 0){
                                isSubscribed = false;
                                tv_subscribe.setText("订阅");
                            }else {
                                isSubscribed = true;
                                tv_subscribe.setText("已订阅");
                            }
                        }else {
                            UIUtils.showToastCenter(SelfMediaDetailActivity.this,isSubcribedResponse.getMsg());
                        }
                    }
                    break;
                case ISSUBSCRIBEFAILED: //是否订阅自媒体失败
                    IOException e2 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"是否订阅自媒体失败："+e2.getMessage());
                    UIUtils.showToastCenter(SelfMediaDetailActivity.this,e2.getMessage());
                    break;
            }
        }
    };

    private void setMessage() {
        if(!TextUtils.isEmpty(getSelfMediaInfoResponse.getHead_pic())){
            Picasso.with(this).load(getSelfMediaInfoResponse.getHead_pic()).into(iv_head);
        }
        if(!TextUtils.isEmpty(getSelfMediaInfoResponse.getNickname())){
            tv_nickname.setText(getSelfMediaInfoResponse.getNickname());
        }
        if(!TextUtils.isEmpty(getSelfMediaInfoResponse.getSelf_intr())){
            tv_describe.setText(getSelfMediaInfoResponse.getSelf_intr());
        }
        if(!TextUtils.isEmpty(getSelfMediaInfoResponse.getFans_amount())){
            tv_fans.setText(getSelfMediaInfoResponse.getFans_amount());
        }
        if(!TextUtils.isEmpty(getSelfMediaInfoResponse.getAttention_amount())){
            tv_attention.setText(getSelfMediaInfoResponse.getAttention_amount());
        }
        if(!TextUtils.isEmpty(getSelfMediaInfoResponse.getPraised_amount())){
            tv_praise.setText(getSelfMediaInfoResponse.getPraised_amount());
        }
        videoBeanList = getSelfMediaInfoResponse.getData();
        if(videoBeanList != null){
            videoSearchAdapter = new VideoSearchAdapter(this,videoBeanList);
            gv_newest.setAdapter(videoSearchAdapter);
        }
    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_self_media_detail, null);
        setContentView(rootView);

        gv_newest = findViewById(R.id.gv_newest);
        iv_back = findViewById(R.id.iv_back);
        iv_head = findViewById(R.id.iv_head);
        ll_send_message = findViewById(R.id.ll_send_message);
        ll_subscribe = findViewById(R.id.ll_subscribe);
        tv_nickname = findViewById(R.id.tv_nickname);
        iv_sex = findViewById(R.id.iv_sex);
        tv_describe = findViewById(R.id.tv_describe);
        tv_fans = findViewById(R.id.tv_fans);
        tv_attention = findViewById(R.id.tv_attention);
        tv_praise = findViewById(R.id.tv_praise);
        tv_subscribe = findViewById(R.id.tv_subscribe);

        iv_back.setOnClickListener(this);
        ll_send_message.setOnClickListener(this);
        ll_subscribe.setOnClickListener(this);

        gv_newest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VideoBean videoBean = videoBeanList.get(i);
                Intent intent = new Intent(SelfMediaDetailActivity.this,VideoPlayActivity.class);
                intent.putExtra("video_id",videoBean.getVideo_id());
                intent.putExtra("videoListStr",gson.toJson(videoBeanList));
                intent.putExtra("postion",i);
                UIUtils.startActivity(intent);
            }
        });
        initData();
        return rootView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandller != null){

        }
    }

    private void initData() {
        Intent intent = getIntent();
        self_media_id = intent.getStringExtra("self_media_id");
        fromSource = intent.getIntExtra("fromSource",0);

        user_id = SharedPrefrenceUtils.getString(this,"usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);

        isSubscribSelfMedia();
        getSelfMediaInfo();
    }

    private void getSelfMediaInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(user_id)){
            reqBody.put("usrs_id", user_id);
        }
        reqBody.put("self_media_id",self_media_id);
        LogUtils.e(TAG+"自媒体详情参数："+reqBody.toString());
        netUtils.postDataAsynToNet(Constants.SelfMedioInfoUrl ,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETINFOSUCCESS;
                message.obj = string;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETINFOFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    private void isSubscribSelfMedia() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(user_id)){
            reqBody.put("usrs_id", user_id);
        }
        reqBody.put("self_media_id",self_media_id);
        LogUtils.e(TAG+"是否订阅自媒体参数："+reqBody.toString());
        netUtils.postDataAsynToNet(Constants.IsSubscribeSelfmediaUrl ,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = ISSUBSCRIBESUCCESS;
                message.obj = string;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = ISSUBSCRIBEFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    /**
     * 自媒体订阅
     */
    private void subscribSelfMediaInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(user_id)){
            reqBody.put("usrs_id", user_id);
        }
        reqBody.put("self_media_id",self_media_id);
        reqBody.put("fans_source",String.valueOf(fromSource));
        LogUtils.e(TAG+"自媒体订阅参数："+reqBody.toString());
        netUtils.postDataAsynToNet(Constants.SubscribeSelfMediaUrl ,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = SUBSCRIBESUCCESS;
                message.obj = string;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = SUBSCRIBEFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_send_message: //发消息
                if(getSelfMediaInfoResponse == null){
                    UIUtils.showToastCenter(SelfMediaDetailActivity.this,"获取自媒体数据失败");
                    return;
                }
                Intent intent1 = new Intent(SelfMediaDetailActivity.this,MessageDetailActivity.class);
                intent1.putExtra("self_media_id",getSelfMediaInfoResponse.getSelf_media_id());
                UIUtils.startActivity(intent1);
                break;
            case R.id.ll_subscribe:  //订阅
                if(isSubscribed){
                    UIUtils.showToastCenter(SelfMediaDetailActivity.this,"已经订阅");
                    return;
                }
                subscribSelfMediaInfo();
                break;
        }
    }
}
