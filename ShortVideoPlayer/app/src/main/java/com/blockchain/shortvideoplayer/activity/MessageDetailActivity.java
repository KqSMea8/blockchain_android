package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.Messagedapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.MediaRecordBean;
import com.blockchain.shortvideoplayer.model.MessageBean;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetMsgRecordResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class MessageDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MessageDetailActivity.class.getSimpleName();
    private String mediaRecordBeanStr;
    private MediaRecordBean mediaRecordBean;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private String user_id;
    private ListView lv_message;
    private ImageView iv_back;
    private TextView tv_tilte;
    private List<MessageBean> messageBeanList;
    private Messagedapter messagedapter;
    private String myHeadPic;
    private String otherHeadPic;
    private TextView tv_submit;
    private EditText et_content;
    private String content;
    private static final int SENDMSGSUCCESS = 0;
    private static final int SENDMSTFAILED = 1;
    private static final int GETLISTSUCCESS = 2;
    private static final int GETLISTFAILED = 3;
    private String self_media_id;
    private List<MediaRecordBean> mediaRecordBeanList;
    private GetMsgRecordResponse getMsgRecordResponse;
    private List<MessageBean> sys_record_list;
    private String sysRecordListStr;
    private LinearLayout ll_send_message;
    private InputMethodManager imm;
    private MyHandler mHandller = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SENDMSGSUCCESS:  //发送消息成功
                    String  string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"发送消息成功："+string);
                    BaseResponse baseResponse = gson.fromJson(string,BaseResponse.class);
                    if(baseResponse != null){
                        if(baseResponse.getCode() ==0){
                            et_content.setText("");
                            imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0); //强制隐藏键盘
                            getMessageDetail();
                        }else {
                            UIUtils.showToastCenter(MessageDetailActivity.this,baseResponse.getMsg());
                        }
                    }
                    break;
                case SENDMSTFAILED: //发送失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"发送消息失败:"+e.getMessage());
                    UIUtils.showToastCenter(MessageDetailActivity.this,e.getMessage());
                    break;
                case GETLISTSUCCESS:  //获取消息列表成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取消息列表成功："+str);
                    getMsgRecordResponse = gson.fromJson(str,GetMsgRecordResponse.class);
                    if(getMsgRecordResponse != null){
                        if(getMsgRecordResponse.getCode() == 0){
                            mediaRecordBeanList = getMsgRecordResponse.getMedia_record_list();
                            if(mediaRecordBeanList != null){
                                setMessage();
                            }
                        }else {
                            UIUtils.showToastCenter(MessageDetailActivity.this,getMsgRecordResponse.getMsg());
                        }
                    }
                    break;
                case GETLISTFAILED: //获取消息列表失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取消息列表失败："+e1.getMessage());
                    UIUtils.showToastCenter(MessageDetailActivity.this,e1.getMessage());
                    break;
            }
        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_message_detail, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        tv_tilte = findViewById(R.id.tv_tilte);
        lv_message = findViewById(R.id.lv_message);
        et_content = findViewById(R.id.et_content);
        tv_submit = findViewById(R.id.tv_submit);
        ll_send_message = findViewById(R.id.ll_send_message);

        iv_back.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
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
        user_id = SharedPrefrenceUtils.getString(this, "usrs_id");
//        myHeadPic = SharedPrefrenceUtils.getString(this, "head_pic");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_content,InputMethodManager.SHOW_FORCED);
        Intent intent = getIntent();
        self_media_id = intent.getStringExtra("self_media_id");
        sysRecordListStr = intent.getStringExtra("sysRecordListStr");
        if(!TextUtils.isEmpty(self_media_id)){
            ll_send_message.setVisibility(View.VISIBLE);
            getMessageDetail();
        }else {
            ll_send_message.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(sysRecordListStr)){
                sys_record_list = gson.fromJson(sysRecordListStr,new TypeToken<List<MessageBean>>(){}.getType());
                if(sys_record_list != null){
                    messagedapter = new Messagedapter(this, sys_record_list, otherHeadPic, myHeadPic);
                    lv_message.setAdapter(messagedapter);
                }
            }
        }
    }

    private void setMessage() {

        if(!TextUtils.isEmpty(self_media_id)){
            for(MediaRecordBean mediaRecordBean1: mediaRecordBeanList){
                if(mediaRecordBean1.getSelf_media_id().equals(self_media_id)){
                    mediaRecordBean = mediaRecordBean1;
                }
            }
            if(mediaRecordBean != null){
                if (!TextUtils.isEmpty(mediaRecordBean.getNickname())) {
                    tv_tilte.setText(mediaRecordBean.getNickname());
                    otherHeadPic = mediaRecordBean.getHead_pic();
                    myHeadPic = mediaRecordBean.getHead_pic_usr();
                }
                messageBeanList = mediaRecordBean.getRecord_list();
            }
        }else {
            messageBeanList = getMsgRecordResponse.getSys_record_list();
        }
        if(messageBeanList != null){
            messagedapter = new Messagedapter(this, messageBeanList, otherHeadPic, myHeadPic);
            lv_message.setAdapter(messagedapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                content = et_content.getText().toString();
                if(TextUtils.isEmpty(content)){
                    UIUtils.showToastCenter(MessageDetailActivity.this,"请先输入内容");
                    return;
                }
                sendMessage();
                break;
        }
    }

    private void getMessageDetail(){
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
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

    private void sendMessage() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
        reqBody.put("msg_content", content);
        reqBody.put("msg_type", "0");
        if(!TextUtils.isEmpty(self_media_id)){
            reqBody.put("self_media_id", self_media_id);
        }else {
            reqBody.put("self_media_id", mediaRecordBean.getSelf_media_id());
        }
        netUtils.postDataAsynToNet(Constants.SendMegUrl, reqBody, new NetUtils.MyNetCall() {

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = SENDMSGSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = SENDMSTFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }
}
