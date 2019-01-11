package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.ClassSubscribeAdapter;
import com.blockchain.shortvideoplayer.adapter.ClassTwoAdapter;
import com.blockchain.shortvideoplayer.adapter.SelfMediaSearchAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.model.SelfMediaBean;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetClassListResponse;
import com.blockchain.shortvideoplayer.response.GetSubscribSelfMediaListResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class SubscribeActivity extends BaseActivity {
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private static final String TAG = SubscribeActivity.class.getSimpleName();
    private static final int GETSUBSCRIBESUCCESS = 0;  //获取订阅类目成功
    private static final int GETSUBSCRIBEFAILED = 1;   //获取订阅类目失败
    private static final int DELSUBSCRIBECLASSSUCCESS = 2;  //用户取消订阅该类目
    private static final int DELSUBSCRIBECLASSFAILED = 3;
    private String usr_id;
    private GridView gv_class;
    private List<ClassModel> classModelList;
    private ClassSubscribeAdapter classSubscribeAdapter;
    private boolean isEdite;  //是否编辑
    private TextView tv_edite;
    private String selectClassIds = "";
    private ImageView iv_back;

    private MyHandler mHandller = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case GETSUBSCRIBESUCCESS:  //获取订阅类目成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"成功："+str);
                    GetClassListResponse getClassListResponse = gson.fromJson(str,GetClassListResponse.class);
                    if(getClassListResponse != null){
                        if(getClassListResponse.getCode() == 0){
                            classModelList = getClassListResponse.getClass_list();
                            if(isEdite){
                                classSubscribeAdapter = new ClassSubscribeAdapter(SubscribeActivity.this,classModelList,true);
                                gv_class.setAdapter(classSubscribeAdapter);
                            }else {
                                classSubscribeAdapter = new ClassSubscribeAdapter(SubscribeActivity.this,classModelList,false);
                                gv_class.setAdapter(classSubscribeAdapter);
                            }
                        }else {
                            UIUtils.showToastCenter(SubscribeActivity.this,getClassListResponse.getMsg());
                        }
                    }
                    break;
                case GETSUBSCRIBEFAILED: //获取订阅类目失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"失败："+e.getMessage());
                    UIUtils.showToastCenter(SubscribeActivity.this,e.getMessage());
                    break;
                case DELSUBSCRIBECLASSSUCCESS:  //取消订阅该类目
                    String string2 = (String) msg.obj;
                    LogUtils.e(TAG+"取消订阅："+string2);
                    BaseResponse baseResponse1 = gson.fromJson(string2,BaseResponse.class);
                    if(baseResponse1 != null){
                        if(baseResponse1.getCode() == 0){
                            getSubscribList();
                        }else {
                            UIUtils.showToastCenter(SubscribeActivity.this,baseResponse1.getMsg());
                        }
                    }
                    break;
                case DELSUBSCRIBECLASSFAILED: //取消订阅该类目
                    IOException e3 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"取消订阅失败:"+e3.getMessage());
                    UIUtils.showToastCenter(SubscribeActivity.this,e3.getMessage());
                    break;
            }
        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_subscribe, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        gv_class = findViewById(R.id.gv_class);
        tv_edite = findViewById(R.id.tv_edite);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gv_class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClassModel classModel = classModelList.get(i);
                if(classModel.isSelected()){
                    classModel.setSelected(false);
                }else {
                    classModel.setSelected(true);
                }
                classSubscribeAdapter.notifyDataSetChanged();
            }
        });

        tv_edite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEdite){
                    isEdite = true;
                    tv_edite.setText("取消关注");
                    if(classModelList != null){
                        classSubscribeAdapter = new ClassSubscribeAdapter(SubscribeActivity.this,classModelList,true);
                        gv_class.setAdapter(classSubscribeAdapter);
                    }
                }else {
                    selectClassIds = "";
                    if(classModelList != null && classModelList.size()>0){
                        for(ClassModel classModel : classModelList){
                            if(classModel.isSelected()){
                                selectClassIds = selectClassIds + ","+classModel.getClass_id();
                            }
                        }
                    }
                    if(selectClassIds.length()>0){
                        selectClassIds = selectClassIds.substring(1);
                        delSubscribClass();
                    }

                }
            }
        });
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
        usr_id = SharedPrefrenceUtils.getString(this,"usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);

        getSubscribList();
    }

    private void delSubscribClass(){
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("class_id_list",selectClassIds);
        reqBody.put("usrs_id",usr_id);
        LogUtils.e(TAG+"订阅参数："+reqBody.toString());
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
    }

    private void getSubscribList() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", usr_id);
        netUtils.postDataAsynToNet(Constants.GetSubscribeListUrl,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETSUBSCRIBESUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETSUBSCRIBEFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }
}
