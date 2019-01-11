package com.blochchain.shortvideorecord.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blochchain.shortvideorecord.adapter.Messagedapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.model.MediaRecordBean;
import com.blochchain.shortvideorecord.model.MessageBean;
import com.blochchain.shortvideorecord.response.BaseResponse;
import com.blochchain.shortvideorecord.response.GetMsgRecordResponse;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
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
    private String usrs_id;
    private List<MediaRecordBean> mediaRecordBeanList;
    private GetMsgRecordResponse getMsgRecordResponse;
    private List<MessageBean> sys_record_list;
    private String sysRecordListStr;
    private LinearLayout ll_send_message;
    private MyHandler mHandller = new MyHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENDMSGSUCCESS:  //发送消息成功
                    String string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "发送消息成功：" + string);
                    BaseResponse baseResponse = gson.fromJson(string, BaseResponse.class);
                    if (baseResponse != null) {
                        if (baseResponse.getCode() == 0) {
                            et_content.setText("");
                            getMessageDetail();
                        } else {
                            UIUtils.showToastCenter(MessageDetailActivity.this, baseResponse.getMsg());
                        }
                    }
                    break;
                case SENDMSTFAILED: //发送失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "发送消息失败:" + e.getMessage());
                    UIUtils.showToastCenter(MessageDetailActivity.this, e.getMessage());
                    break;
                case GETLISTSUCCESS:  //获取消息列表成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取消息列表成功：" + str);
                    getMsgRecordResponse = gson.fromJson(str, GetMsgRecordResponse.class);
                    if (getMsgRecordResponse != null) {
                        if (getMsgRecordResponse.getCode() == 0) {
                            mediaRecordBeanList = getMsgRecordResponse.getUsr_record_list();
                            if (mediaRecordBeanList != null) {
                                setMessage();
                            }
                        } else {
                            UIUtils.showToastCenter(MessageDetailActivity.this, getMsgRecordResponse.getMsg());
                        }
                    }
                    break;
                case GETLISTFAILED: //获取消息列表失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取消息列表失败：" + e1.getMessage());
                    UIUtils.showToastCenter(MessageDetailActivity.this, e1.getMessage());
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

    private void initData() {
//        myHeadPic = SharedPrefrenceUtils.getString(this, "head_pic");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");

        Intent intent = getIntent();
        sysRecordListStr = intent.getStringExtra("sysRecordListStr");
        usrs_id = intent.getStringExtra("usrs_id");
        if (!TextUtils.isEmpty(usrs_id)) {
            ll_send_message.setVisibility(View.VISIBLE);
            getMessageDetail();
        } else {
            ll_send_message.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(sysRecordListStr)) {
                sys_record_list = gson.fromJson(sysRecordListStr, new TypeToken<List<MessageBean>>() {
                }.getType());
                if (sys_record_list != null) {
                    messagedapter = new Messagedapter(this, sys_record_list, otherHeadPic, myHeadPic);
                    lv_message.setAdapter(messagedapter);
                }
            }
        }
    }

    private void setMessage() {

        if (!TextUtils.isEmpty(self_media_id)) {
            for (MediaRecordBean mediaRecordBean1 : mediaRecordBeanList) {
                if (mediaRecordBean1.getSelf_media_id().equals(self_media_id)) {
                    mediaRecordBean = mediaRecordBean1;
                }
            }
            if (mediaRecordBean != null) {
                if (!TextUtils.isEmpty(mediaRecordBean.getNickname())) {
                    tv_tilte.setText(mediaRecordBean.getNickname());
                    otherHeadPic = mediaRecordBean.getHead_pic_usr();
                    myHeadPic = mediaRecordBean.getHead_pic();
                }
                messageBeanList = mediaRecordBean.getRecord_list();
            }
        } else {
            messageBeanList = getMsgRecordResponse.getSys_record_list();
        }
        if (messageBeanList != null) {
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
                if (TextUtils.isEmpty(content)) {
                    UIUtils.showToastCenter(MessageDetailActivity.this, "请先输入内容");
                    return;
                }
                sendMessage();
                break;
        }
    }

    private void getMessageDetail() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("self_media_id", self_media_id);
        netUtils.postDataAsynToNet(Constants.GetMsgRecordUrl, reqBody, new NetUtils.MyNetCall() {

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
        reqBody.put("self_media_id", self_media_id);
        reqBody.put("msg_content", content);
        reqBody.put("msg_type", "1");
        reqBody.put("usrs_id", usrs_id);
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
