package com.blockchain.shortvideoplayer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.ClassFirstModel;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.response.GetClassFirstListResponse;
import com.blockchain.shortvideoplayer.response.GetCodeResponse;
import com.blockchain.shortvideoplayer.response.LoginResponse;
import com.blockchain.shortvideoplayer.tabpager.ControlTabFragment;
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
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_close;
    private EditText et_phone;
    private EditText et_verification;
    private TextView tv_get_code;
    private TextView tv_login;
    private CheckBox cb_select;
    private TextView tv_user_protocol;
    private String phone;
    private String verification;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private int time = 60;
    private NetUtils netUtils;
    private String TAG = LoginActivity.class.getSimpleName();
    private static final int GETCODESUCCESS = 0;
    private static final int GETCODEFAILED = 1;
    private static final int LOGINSUCCESS = 2;
    private static final int LOGINFAILED = 3;
    private static final int GETALLCLASSSUCESS = 16; //获取所有类目成功
    private static final int GETALLCLASSFAILED = 17;  //获取所有类目失败
    private int auth_code_id;
    private boolean fromMine;  //是否从个人中心跳转过来
    private ControlTabFragment ctf;
    private int count;
    private List<ClassFirstModel> classFirstModelList;
    private List<ClassModel> classModelList;
    private MyHandler mHandle = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETCODESUCCESS:  //获取验证码成功
                    loadingDialog.dismiss();
                    String str = (String) msg.obj;
                    LogUtils.e(TAG + "成功：" + str);
                    GetCodeResponse getCodeResponse = gson.fromJson(str, GetCodeResponse.class);
                    if(getCodeResponse != null){
                        if(getCodeResponse.getCode() == 0){
                            auth_code_id = getCodeResponse.getAuth_code_id();
                        }else {
                            UIUtils.showToastCenter(LoginActivity.this,getCodeResponse.getMsg());
                        }
                    }
                    break;
                case GETCODEFAILED: //获取验证码失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
//                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    UIUtils.showToastCenter(LoginActivity.this,e.getMessage());
                    break;
                case LOGINSUCCESS: //登录成功
                    tv_login.setEnabled(true);
                    loadingDialog.dismiss();
                    String string = (String) msg.obj;
                    LogUtils.e(TAG + "成功：" + string);
                    LoginResponse loginResponse = gson.fromJson(string,LoginResponse.class);
                    if(loginResponse != null){
                        if(loginResponse.getCode() == 0){
                            SharedPrefrenceUtils.setString(LoginActivity.this,"phone",phone);
                            SharedPrefrenceUtils.setString(LoginActivity.this,"usrs_id",loginResponse.getUsrs_id());
                            SharedPrefrenceUtils.setString(LoginActivity.this,"recommend_class",gson.toJson(loginResponse.getRecommend_class()));
                            SharedPrefrenceUtils.setInt(LoginActivity.this,"is_first",loginResponse.getIs_first());
                            setResult(200);
                            finish();
                        }else {
                            UIUtils.showToastCenter(LoginActivity.this,loginResponse.getMsg());
                        }
                    }
                    break;
                case LOGINFAILED: //登录失败
                    IOException e1 = (IOException) msg.obj;
                    tv_login.setEnabled(true);
                    loadingDialog.dismiss();
//                    LogUtils.e(TAG + "失败：" + e1.getMessage());
                    UIUtils.showToastCenter(LoginActivity.this,e1.getMessage());
                    break;
                case GETALLCLASSSUCESS:  //获取所有类目成功
                    String  string8 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取所有类目成功："+string8);
                    GetClassFirstListResponse getClassFirstListResponse = gson.fromJson(string8,GetClassFirstListResponse.class);
                    if(getClassFirstListResponse != null){
                        if(getClassFirstListResponse.getCode() == 0){
                            classFirstModelList = getClassFirstListResponse.getClass_1_list();
                            if(classFirstModelList != null){
                                setClassMessage();
                            }
                        }else {
                            UIUtils.showToastCenter(LoginActivity.this,getClassFirstListResponse.getMsg());
                        }
                    }
                    break;
                case GETALLCLASSFAILED: //获取列表失败
                    IOException e8 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取所有类目失败："+e8.getMessage());
                    UIUtils.showToastCenter(LoginActivity.this,e8.getMessage());
                    break;
            }
        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_login, null);
        setContentView(rootView);

        iv_close = findViewById(R.id.iv_close);
        et_phone = findViewById(R.id.et_phone);
        et_verification = findViewById(R.id.et_verification);
        tv_get_code = findViewById(R.id.tv_get_code);
        tv_login = findViewById(R.id.tv_login);
        cb_select = findViewById(R.id.cb_select);
        tv_user_protocol = findViewById(R.id.tv_user_protocol);

        initData();
        return rootView;
    }

    /**
     * 设置推荐目录
     */
    private void setClassMessage() {
        classModelList = new ArrayList<>();
        for(ClassFirstModel classFirstModel : classFirstModelList){
            if(classFirstModel.getClass_2_list() != null && classFirstModel.getClass_2_list().size()>0){
                for(ClassModel classModel : classFirstModel.getClass_2_list()){
                    if(classModelList.size() < 9){
                        classModelList.add(classModel);
                    }
                }
            }
        }

        SharedPrefrenceUtils.setString(this,"classModelListStr",gson.toJson(classModelList));
    }

    private void getAllClassList() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        netUtils.postDataAsynToNet(Constants.GetAllClassUrl,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETALLCLASSSUCESS;
                message.obj = str;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETALLCLASSFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandle != null){
            mHandle.removeCallbacksAndMessages(null);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        fromMine = intent.getBooleanExtra("fromMine",false);
        if (ctf == null) {
            ctf = Main2Activity.getCtf();
        }

        iv_close.setOnClickListener(this);
        tv_get_code.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        tv_user_protocol.setOnClickListener(this);

        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(LoginActivity.this, true);

        getAllClassList();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            onKeyDownMethod(keyCode, event);
            ctf.setChecked(0);
            ctf.mCurrentIndex = 0;
            finish();
            overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                if(fromMine){
                    ctf.setChecked(0);
                    ctf.mCurrentIndex = 0;
                }
                finish();
                break;
            case R.id.tv_get_code:
                phone = et_phone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    UIUtils.showToastCenter(this, "请输入手机号码!");
                    return;
                }
                getCode();
                break;
            case R.id.tv_login:
                phone = et_phone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    UIUtils.showToastCenter(this, "请输入手机号码!");
                    return;
                }
                verification = et_verification.getText().toString();
                if (TextUtils.isEmpty(verification)) {
                    UIUtils.showToastCenter(this, "请输入验证码!");
                    return;
                }
                if (!cb_select.isChecked()) {
                    UIUtils.showToastCenter(this, "请阅读并同意《用户服务协议》!");
                    return;
                }
                login();
                break;
            case R.id.tv_user_protocol:
                break;
        }
    }

    private void login() {
        tv_login.setEnabled(false);
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("tel", phone);
        reqBody.put("auth_code_id", String.valueOf(auth_code_id));
        reqBody.put("auth_code", verification);
        reqBody.put("source", "0");
        LogUtils.e(TAG + "登录参数:" + reqBody.toString());
        netUtils.postDataAsynToNet(Constants.LoginUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = LOGINSUCCESS;
                message.obj = str;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = LOGINFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }

    private void getCode() {
        count ++;
        tv_get_code.setEnabled(false);
        if(count > 3){
            Countdowmtimer(300000);
        }else {
            Countdowmtimer(60000);
        }

        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("user_phone",phone);
        reqBody.put("msg_type","0");
        netUtils.postDataAsynToNet(Constants.GetCodeUrl,reqBody, new NetUtils.MyNetCall() {

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETCODESUCCESS;
                message.obj = str;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETCODEFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }

    /**
     * 计时器
     */
    public void Countdowmtimer(long dodate) {
        new CountDownTimer(dodate, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = time - 1;
                tv_get_code.setText(time + "s重新获取");
            }

            @Override
            // 计时结束
            public void onFinish() {
                tv_get_code.setEnabled(true);
                if(count > 3){
                    time = 300;
                }else {
                    time = 60;
                }
                tv_get_code.setText("获取验证码");
//                SpannableStringBuilder style=new SpannableStringBuilder(tv_time.getText());
//                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink_text)), 8, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                tv_time.setText(style);
            }
        }.start();
    }
}
