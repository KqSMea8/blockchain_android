package com.blochchain.shortvideorecord.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.response.GetCodeResponse;
import com.blochchain.shortvideorecord.response.LoginResponse;
import com.blochchain.shortvideorecord.tabpager.ControlTabFragment;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;

import java.io.IOException;
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
    private int auth_code_id;
    private boolean fromMine;  //是否从个人中心跳转过来
    private boolean fromSplash; //从引导页跳过来
    private ControlTabFragment ctf;
    private TextView tv_login_text;
    private boolean isChecked;
    private int count;
    private MyHandler mHandle = new MyHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETCODESUCCESS:  //获取验证码成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "成功：" + str);
                    GetCodeResponse getCodeResponse = gson.fromJson(str, GetCodeResponse.class);
                    auth_code_id = getCodeResponse.getAuth_code_id();
                    break;
                case GETCODEFAILED: //获取验证码失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    break;
                case LOGINSUCCESS: //登录成功
                    String string = (String) msg.obj;
                    tv_login.setEnabled(true);
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "成功：" + string);
                    LoginResponse loginResponse = gson.fromJson(string, LoginResponse.class);
                    if (loginResponse != null) {
                        if (loginResponse.getCode() == 0) {
                            SharedPrefrenceUtils.setString(LoginActivity.this, "phone", phone);
                            SharedPrefrenceUtils.setString(LoginActivity.this, "self_media_id", loginResponse.getSelf_media_id());
                            SharedPrefrenceUtils.setInt(LoginActivity.this, "is_first", loginResponse.getIs_first());

                            if (loginResponse.getIs_first() == 1) {
                                Intent intent = new Intent(LoginActivity.this, MyInfromationActivity.class);
                                intent.putExtra("fromLogin", fromSplash);
                                UIUtils.startActivity(intent);
                            } else {
                                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                                UIUtils.startActivity(intent);
                                setResult(200, intent);
                            }
                            finish();
                        } else {
                            UIUtils.showToastCenter(LoginActivity.this, loginResponse.getMsg());
                        }
                    }

//                    SharedPrefrenceUtils.setString(LoginActivity.this,"phone",phone);
//                    SharedPrefrenceUtils.setString(LoginActivity.this,"self_media_id","30");
//                    SharedPrefrenceUtils.setInt(LoginActivity.this,"is_first",1);
//
//                    Intent intent = new Intent(LoginActivity.this,Main2Activity.class);
//                    UIUtils.startActivity(intent);
//                    setResult(200);
//                    finish();
                    break;
                case LOGINFAILED: //登录失败
                    IOException e1 = (IOException) msg.obj;
                    tv_login.setEnabled(true);
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e1.getMessage());
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
        tv_login_text = findViewById(R.id.tv_login_text);

        initData();
        return rootView;
    }

    private void initData() {
        iv_close.setOnClickListener(this);
        tv_get_code.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        tv_user_protocol.setOnClickListener(this);
        tv_login_text.setOnClickListener(this);

        cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked = b;
            }
        });

        isChecked = true;
        Intent intent = getIntent();
        fromMine = intent.getBooleanExtra("fromMine", false);
        fromSplash = intent.getBooleanExtra("fromSplash", false);
        if (ctf == null) {
            ctf = Main2Activity.getCtf();
        }

        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(LoginActivity.this, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                if (fromMine) {
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
                if (!isChecked) {
                    UIUtils.showToastCenter(this, "请阅读并同意《用户服务协议》!");
                    return;
                }
                login();

                break;
            case R.id.tv_user_protocol:
                break;
            case R.id.tv_login_text:
                if (isChecked) {
                    cb_select.setChecked(false);
                } else {
                    cb_select.setChecked(true);
                }
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
        LogUtils.e(TAG + "登录参数:" + reqBody.toString());
        netUtils.postDataAsynToNet(Constants.LoginUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = LOGINSUCCESS;
                message.obj = string;
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
        count++;
        tv_get_code.setEnabled(false);
        if (count > 3) {
            Countdowmtimer(300000);
        } else {
            Countdowmtimer(60000);
        }

        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("user_phone", phone);
        reqBody.put("msg_type", "1");

        netUtils.postDataAsynToNet(Constants.GetCodeUrl, reqBody, new NetUtils.MyNetCall() {

            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETCODESUCCESS;
                message.obj = string;
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
                if (count > 3) {
                    time = 300;
                } else {
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
