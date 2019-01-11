package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.ClassTwoAdapter;
import com.blockchain.shortvideoplayer.adapter.VideoDiscoverAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.ClassFirstModel;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetClassVideoListResponse;
import com.blockchain.shortvideoplayer.response.GetIsSubscribeClassResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.HorizontalListView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class ClassListActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = ClassListActivity.class.getSimpleName();
    private LinearLayout ll_newest;
    private TextView tv_newest;
    private ImageView iv_newest;
    private LinearLayout ll_hot;
    private TextView tv_hot;
    private ImageView iv_hot;
    private GridView gv_videos;
    private VideoDiscoverAdapter videoDiscoverAdapter;
    private TextView tv_subscribe;
    private boolean isSubscribed;
    private ImageView iv_pull_down;
    private LinearLayout ll_more;
    private RelativeLayout rl_pull_up;
    private ImageView iv_pull_up;
    private String classFirstStr;
    private GridView gv_class;
    private List<ClassModel> classModelList;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private String user_id;
    private HorizontalListView hlv_class;
    private ClassTwoAdapter hClassTwoAdapter;
    private ClassTwoAdapter gClassTwoAdapter;
    private LinearLayout ll_h_class;
    private TextView tv_describe;
    private ClassModel selectedClassModel;
    private static final int GETLISTSUCCESS = 0;
    private static final int GETLISTFAILED = 1;
    private static final int ISSUBSCRIBECLASSSUCCESS = 2;  //查看用户是否订阅该类目
    private static final int ISSUBSCRIBECLASSFAILED = 3;
    private static final int SUBSCRIBECLASSSUCCESS = 4;  //用户订阅该类目
    private static final int SUBSCRIBECLASSFAILED = 5;
    private static final int DELSUBSCRIBECLASSSUCCESS = 6;  //用户取消订阅该类目
    private static final int DELSUBSCRIBECLASSFAILED = 7;
    List<VideoBean> hotVideoBeanList;
    List<VideoBean> newVideoBeanList;
    private boolean isHot; //true 展示的是热门列表   false 展示的是最新列表
    private ImageView iv_back1;
    private ImageView iv_back2;
    private MyHandler mHandller = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case GETLISTSUCCESS:  //获取列表成功
                    String  string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取列表成功："+string);
                    GetClassVideoListResponse getClassVideoListResponse = gson.fromJson(string,GetClassVideoListResponse.class);
                    if(getClassVideoListResponse != null){
                        if(getClassVideoListResponse.getCode() == 0){
                            hotVideoBeanList = getClassVideoListResponse.getHot_video_list();
                            newVideoBeanList = getClassVideoListResponse.getVideo_list();
                            switchTitle(0);
                        }else {
                            UIUtils.showToastCenter(ClassListActivity.this,getClassVideoListResponse.getMsg());
                        }
                    }
                    break;
                case GETLISTFAILED: //获取列表失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取列表失败:"+e.getMessage());
                    UIUtils.showToastCenter(ClassListActivity.this,e.getMessage());
                    break;
                case ISSUBSCRIBECLASSSUCCESS: //查看用户是否订阅该类目成功
                    String str = (String) msg.obj;
                    LogUtils.e(TAG+"查看是否订阅:"+str);
                    GetIsSubscribeClassResponse getIsSubscribeClassResponse = gson.fromJson(str,GetIsSubscribeClassResponse.class);
                    if(getIsSubscribeClassResponse != null){
                        if(getIsSubscribeClassResponse.getCode() == 0){
                            if(getIsSubscribeClassResponse.getIs_subscribe() == 0){
                                isSubscribed = false;
                            }else {
                                isSubscribed = true;
                            }
                            changeSubscribeText();
                        }else {
                            UIUtils.showToastCenter(ClassListActivity.this,getIsSubscribeClassResponse.getMsg());
                        }
                    }
                    break;
                case ISSUBSCRIBECLASSFAILED: //查看用户是否订阅该类目失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"查看用户是否订阅失败："+e1.getMessage());
                    UIUtils.showToastCenter(ClassListActivity.this,e1.getMessage());
                    break;
                case SUBSCRIBECLASSSUCCESS: //订阅该类目返回
                    String str1 = (String) msg.obj;
                    LogUtils.e(TAG+"订阅类目返回："+str1);
                    BaseResponse baseResponse = gson.fromJson(str1,BaseResponse.class);
                    if(baseResponse.getCode() == 0){
                        isSubscribed = true;
                        changeSubscribeText();
                    }else {
                        UIUtils.showToastCenter(ClassListActivity.this,baseResponse.getMsg());
                    }
                    break;
                case SUBSCRIBECLASSFAILED: //订阅该类目返回
                    IOException e2 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"订阅失败:"+e2.getMessage());
                    UIUtils.showToastCenter(ClassListActivity.this,e2.getMessage());
                    break;
                case DELSUBSCRIBECLASSSUCCESS:  //取消订阅该类目
                    String string2 = (String) msg.obj;
                    LogUtils.e(TAG+"取消订阅："+string2);
                    BaseResponse baseResponse1 = gson.fromJson(string2,BaseResponse.class);
                    if(baseResponse1 != null){
                        if(baseResponse1.getCode() == 0){
                            isSubscribed = false;
                            changeSubscribeText();
                        }else {
                            UIUtils.showToastCenter(ClassListActivity.this,baseResponse1.getMsg());
                        }
                    }
                    break;
                case DELSUBSCRIBECLASSFAILED: //取消订阅该类目
                    IOException e3 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"取消订阅失败:"+e3.getMessage());
                    UIUtils.showToastCenter(ClassListActivity.this,e3.getMessage());
                    break;
            }
        }
    };
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_class_list, null);
        setContentView(rootView);

        iv_back1 = findViewById(R.id.iv_back1);
        iv_back2 = findViewById(R.id.iv_back2);
        ll_newest = findViewById(R.id.ll_newest);
        tv_newest = findViewById(R.id.tv_newest);
        iv_newest = findViewById(R.id.iv_newest);
        ll_hot = findViewById(R.id.ll_hot);
        tv_hot = findViewById(R.id.tv_hot);
        iv_hot = findViewById(R.id.iv_hot);
        gv_videos = findViewById(R.id.gv_videos);
        tv_subscribe = findViewById(R.id.tv_subscribe);
        iv_pull_down = findViewById(R.id.iv_pull_down);
        ll_more = findViewById(R.id.ll_more);
        rl_pull_up = findViewById(R.id.rl_pull_up);
        iv_pull_up = findViewById(R.id.iv_pull_up);
        gv_class = findViewById(R.id.gv_class);
        hlv_class = findViewById(R.id.hlv_class);
        ll_h_class = findViewById(R.id.ll_h_class);
        tv_describe = findViewById(R.id.tv_describe);

        ll_hot.setOnClickListener(this);
        ll_newest.setOnClickListener(this);
        tv_subscribe.setOnClickListener(this);
        iv_pull_down.setOnClickListener(this);
        iv_pull_up.setOnClickListener(this);
        iv_back1.setOnClickListener(this);
        iv_back2.setOnClickListener(this);
        initData();
        return rootView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandller != null){
            mHandller.removeCallbacksAndMessages(null);
        }
    }

    private void initData() {
        user_id = SharedPrefrenceUtils.getString(this,"usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);

        Intent intent = getIntent();
        classFirstStr = intent.getStringExtra("classFirstStr");
        if(!TextUtils.isEmpty(classFirstStr)){
            LogUtils.e(TAG + "-classsstr:"+classFirstStr);
            ClassFirstModel classFirstModel = gson.fromJson(classFirstStr,ClassFirstModel.class);
            classModelList = classFirstModel.getClass_2_list();
            if(classModelList != null && classModelList.size()>0){
                hClassTwoAdapter = new ClassTwoAdapter(ClassListActivity.this,classModelList);
                hlv_class.setAdapter(hClassTwoAdapter);
                gClassTwoAdapter = new ClassTwoAdapter(ClassListActivity.this,classModelList);
                gv_class.setAdapter(gClassTwoAdapter);
                selectedClassModel = classModelList.get(0);
                hClassTwoAdapter.setSelectItem(0);
                gClassTwoAdapter.setSelectItem(0);
                if(!TextUtils.isEmpty(selectedClassModel.getClass_describe())){
                    tv_describe.setText("类目简介："+selectedClassModel.getClass_describe());
                }
                getVideoList();
                if(!TextUtils.isEmpty(user_id)){
                    isSubscribeClass();
                }

                hlv_class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedClassModel = classModelList.get(i);
                        hClassTwoAdapter.setSelectItem(i);
                        gClassTwoAdapter.setSelectItem(i);
                        hClassTwoAdapter.notifyDataSetChanged();
                        gClassTwoAdapter.notifyDataSetChanged();
                        if(!TextUtils.isEmpty(selectedClassModel.getClass_describe())){
                            tv_describe.setText("类目简介："+selectedClassModel.getClass_describe());
                        }
                        getVideoList();
                        if(!TextUtils.isEmpty(user_id)){
                            isSubscribeClass();
                        }
                    }
                });
                gv_class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedClassModel = classModelList.get(i);
                        gClassTwoAdapter.setSelectItem(i);
                        hClassTwoAdapter.setSelectItem(i);
                        hClassTwoAdapter.notifyDataSetChanged();
                        gClassTwoAdapter.notifyDataSetChanged();
                        if(!TextUtils.isEmpty(selectedClassModel.getClass_describe())){
                            tv_describe.setText("类目简介："+selectedClassModel.getClass_describe());
                        }
                        getVideoList();
                        if(!TextUtils.isEmpty(user_id)){
                            isSubscribeClass();
                        }
                    }
                });
            }
        }

//        videoBeanList = new ArrayList<>();
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoDiscoverAdapter = new VideoDiscoverAdapter(this,videoBeanList);
//        gv_videos.setAdapter(videoDiscoverAdapter);
        gv_videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ClassListActivity.this, VideoPlayActivity1.class);
                VideoBean videoBean = null;
                if(isHot){
                    videoBean = hotVideoBeanList.get(i);
                    intent.putExtra("videoListStr",gson.toJson(hotVideoBeanList));
                }else{
                    videoBean = newVideoBeanList.get(i);
                    intent.putExtra("videoListStr",gson.toJson(newVideoBeanList));
                }
                if(videoBean != null){
                    intent.putExtra("video_id",videoBean.getVideo_id());
                    intent.putExtra("postion",i);
                    intent.putExtra("fromSource",2);
                    UIUtils.startActivity(intent);
                    overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                }
            }
        });
    }

    private void switchTitle(int i) {
        switch (i){
            case 0:
                isHot = true;
                tv_hot.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                tv_newest.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                iv_hot.setImageResource(R.mipmap.bottom_blue);
                iv_newest.setImageResource(R.mipmap.bottom_white);
                if(hotVideoBeanList != null){
                    videoDiscoverAdapter = new VideoDiscoverAdapter(this,hotVideoBeanList);
                    gv_videos.setAdapter(videoDiscoverAdapter);
                }
                break;
            case 1:
                isHot = false;
                tv_hot.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                tv_newest.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                iv_hot.setImageResource(R.mipmap.bottom_white);
                iv_newest.setImageResource(R.mipmap.bottom_blue);
                if(newVideoBeanList != null){
                    videoDiscoverAdapter = new VideoDiscoverAdapter(this,newVideoBeanList);
                    gv_videos.setAdapter(videoDiscoverAdapter);
                }
                break;
        }
    }

    private void getVideoList() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("class_id",selectedClassModel.getClass_id());
        netUtils.postDataAsynToNet(Constants.GetClassVideoListUrl,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETLISTSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETLISTFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    /**
     * 查看该用户是否订阅该类目
     */
    private void isSubscribeClass() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("class_id",selectedClassModel.getClass_id());
        reqBody.put("usrs_id",user_id);
        netUtils.postDataAsynToNet(Constants.IsSubscribeClassUrl,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = ISSUBSCRIBECLASSSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = ISSUBSCRIBECLASSFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_hot:
                switchTitle(0);
                break;
            case R.id.ll_newest:
                switchTitle(1);
                break;
            case R.id.tv_subscribe:
                changeSubscribe();
                break;
            case R.id.iv_pull_down:
                iv_pull_down.setVisibility(View.GONE);
                ll_more.setVisibility(View.VISIBLE);
                rl_pull_up.setVisibility(View.VISIBLE);
                ll_h_class.setVisibility(View.GONE);
                break;
            case R.id.iv_pull_up:
                iv_pull_down.setVisibility(View.VISIBLE);
                ll_more.setVisibility(View.GONE);
                rl_pull_up.setVisibility(View.GONE);
                ll_h_class.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_back1:
            case R.id.iv_back2:
                finish();
                break;
        }

    }

    private void changeSubscribe() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("class_id_list",selectedClassModel.getClass_id());
        reqBody.put("usrs_id",user_id);
        LogUtils.e(TAG+"订阅参数："+reqBody.toString());
        if(isSubscribed){  //已经订阅了，取消订阅
            netUtils.postDataAsynToNet(Constants.DelSubscribeClassUrl,reqBody,new NetUtils.MyNetCall(){
                @Override
                public void success(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    Message message = new Message();
                    message.what = DELSUBSCRIBECLASSSUCCESS;
                    message.obj = str;
                    mHandller.sendMessage(message);
                }

                @Override
                public void failed(Call call, IOException e) {
                    Message message = new Message();
                    message.what = DELSUBSCRIBECLASSFAILED;
                    message.obj = e;
                    mHandller.sendMessage(message);
                }
            });
        }else {  //没有订阅， 订阅
            netUtils.postDataAsynToNet(Constants.SubscribeClassUrl,reqBody,new NetUtils.MyNetCall(){

                @Override
                public void success(Call call, Response response) throws IOException {
                    String str = response.body().string();
                    Message message = new Message();
                    message.what = SUBSCRIBECLASSSUCCESS;
                    message.obj = str;
                    mHandller.sendMessage(message);
                }

                @Override
                public void failed(Call call, IOException e) {
                    Message message = new Message();
                    message.what = SUBSCRIBECLASSFAILED;
                    message.obj = e;
                    mHandller.sendMessage(message);
                }
            });
        }
    }

    private void changeSubscribeText() {
        if(isSubscribed){
            tv_subscribe.setBackgroundResource(R.drawable.gray_circle_shape);
            tv_subscribe.setText("取消订阅");
        }else {
            tv_subscribe.setBackgroundResource(R.drawable.blue_circle_shape);
            tv_subscribe.setText("订阅");

        }
    }
}
