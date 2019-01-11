package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.response.GetMsgRecordResponse;
import com.blockchain.shortvideoplayer.response.GetUserInfoResponse;
import com.blockchain.shortvideoplayer.tabpager.TabMinePager;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class MessageActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_back;
    private RelativeLayout rl_system;  //系统消息
    private TextView tv_system_time;
    private TextView tv_system_num;
    private RelativeLayout rl_self;  //自媒体留言
    private TextView tv_self_time;
    private TextView tv_self_num;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final int GETLISTSUCCESS = 0;
    private static final int GETLISTFAILED = 1;
    private static final String TAG = MessageActivity.class.getSimpleName();
    private String user_id;
    private GetMsgRecordResponse getMsgRecordResponse;

    private MyHandler mHandller = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case GETLISTSUCCESS:  //获取消息列表成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取消息列表成功："+str);
                    getMsgRecordResponse = gson.fromJson(str,GetMsgRecordResponse.class);
                    if(getMsgRecordResponse != null){
                        if(getMsgRecordResponse.getCode() == 0){
                            setMessage();
                        }else {
                            UIUtils.showToastCenter(MessageActivity.this,getMsgRecordResponse.getMsg());
                        }
                    }
                    break;
                case GETLISTFAILED: //获取消息列表失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取消息列表失败："+e.getMessage());
                    UIUtils.showToastCenter(MessageActivity.this,e.getMessage());
                    break;
            }
        }
    };

    private void setMessage() {
        if(getMsgRecordResponse.getSys_record_list() != null && getMsgRecordResponse.getSys_record_list().size()>0){
            tv_system_time.setText(getMsgRecordResponse.getSys_record_list().get(0).getMsg_time());
            tv_system_num.setText(getMsgRecordResponse.getSys_record_list().size()+"条");
        }
        if(getMsgRecordResponse.getMedia_record_list() != null && getMsgRecordResponse.getMedia_record_list().size()>0){
            tv_self_time.setText(getMsgRecordResponse.getMedia_record_list().get(0).getLast_time());
            tv_self_num.setText(getMsgRecordResponse.getMedia_record_list().size()+"条");
        }
    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_message, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        rl_system = findViewById(R.id.rl_system);
        tv_system_time = findViewById(R.id.tv_system_time);
        tv_system_num = findViewById(R.id.tv_system_num);
        rl_self = findViewById(R.id.rl_self);
        tv_self_time = findViewById(R.id.tv_self_time);
        tv_self_num = findViewById(R.id.tv_self_num);

        iv_back.setOnClickListener(this);
        rl_system.setOnClickListener(this);
        rl_self.setOnClickListener(this);

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
        user_id = SharedPrefrenceUtils.getString(this,"usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);

        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
        LogUtils.e(TAG + ",user_id:"+user_id);
        netUtils.postDataAsynToNet(Constants.GetMsgRecordUrl,reqBody ,new NetUtils.MyNetCall(){

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
            case R.id.rl_system:
                Intent intent1 = new Intent(this,MessageDetailActivity.class);
                intent1.putExtra("sysRecordListStr",gson.toJson(getMsgRecordResponse.getSys_record_list()));
                UIUtils.startActivity(intent1);
                overridePendingTransition(R.anim.animnext_in,R.anim.animnext_out);
                break;
            case R.id.rl_self:
                Intent intent = new Intent(this,MessageFansActivity.class);
                intent.putExtra("mediaRecordListStr",gson.toJson(getMsgRecordResponse.getMedia_record_list()));
                UIUtils.startActivity(intent);
                overridePendingTransition(R.anim.animnext_in,R.anim.animnext_out);
                break;
        }
    }
}