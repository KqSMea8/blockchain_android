package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.UsrBuyRecordAdapter;
import com.blockchain.shortvideoplayer.adapter.VideoRecommendAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.UsrBuyRecordBean;
import com.blockchain.shortvideoplayer.response.GetUsrBuyRecordResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class AccoountManagerActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = AccoountManagerActivity.class.getSimpleName();
    private ImageView iv_back;
    private ListView lv_account;
    private List<UsrBuyRecordBean> usrBuyRecordBeanList;
    private UsrBuyRecordAdapter usrBuyRecordAdapter;
    //接口请求菊花
    private static final int GETLISTSUCCESS = 0;  //获取列表成功
    private static final int GETLISTFAILED = 1;   //获取列表失败
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private String user_id;
    private MyHandler mHandller = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETLISTSUCCESS:  //获取推荐列表成功
                    loadingDialog.dismiss();
                    String str1 = (String) msg.obj;
                    LogUtils.e(TAG + "获取列表成功：" + str1);
                    GetUsrBuyRecordResponse getUsrBuyRecordResponse = gson.fromJson(str1,GetUsrBuyRecordResponse.class);
                    if(getUsrBuyRecordResponse != null){
                        if(getUsrBuyRecordResponse.getCode() == 0){
                            usrBuyRecordBeanList = getUsrBuyRecordResponse.getPay_list();
                            usrBuyRecordAdapter = new UsrBuyRecordAdapter(AccoountManagerActivity.this,usrBuyRecordBeanList);
                            lv_account.setAdapter(usrBuyRecordAdapter);
                        }else {
                            UIUtils.showToastCenter(AccoountManagerActivity.this,getUsrBuyRecordResponse.getMsg());
                        }
                    }
                    break;
                case GETLISTFAILED: //获取列表失败
                    loadingDialog.dismiss();
                    IOException e4 = (IOException) msg.obj;
                    LogUtils.e(TAG + "获取列表失败：" + e4.getMessage());
                    UIUtils.showToastCenter(AccoountManagerActivity.this,e4.getMessage());
                    break;
            }
        }
    };
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_accoount_manager, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        lv_account = findViewById(R.id.lv_account);

        iv_back.setOnClickListener(this);
        initData();
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandller != null){
            mHandller.removeCallbacksAndMessages(null);
        }
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        user_id = SharedPrefrenceUtils.getString(this,"usrs_id");

//        usrBuyRecordBeanList = new ArrayList<>();
//        usrBuyRecordBeanList.add(new UsrBuyRecordBean());
//        usrBuyRecordBeanList.add(new UsrBuyRecordBean());
//        usrBuyRecordBeanList.add(new UsrBuyRecordBean());
//        usrBuyRecordBeanList.add(new UsrBuyRecordBean());
//        usrBuyRecordBeanList.add(new UsrBuyRecordBean());
//        usrBuyRecordAdapter = new UsrBuyRecordAdapter(this,usrBuyRecordBeanList);
//        lv_account.setAdapter(usrBuyRecordAdapter);

        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
        loadingDialog.show();
        netUtils.postDataAsynToNet(Constants.GetUsrbuyRecordUrl,reqBody, new NetUtils.MyNetCall() {

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }

    }
}
