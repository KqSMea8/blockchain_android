package com.blockchain.shortvideoplayer.activity;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetCodeResponse;
import com.blockchain.shortvideoplayer.response.LoginResponse;
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

public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_back;
    private EditText et_phone;
    private EditText et_verification;
    private TextView tv_get_code;
    private TextView tv_commit;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private int time = 300;
    private NetUtils netUtils;
    private String TAG = ChangePhoneActivity.class.getSimpleName();
    private static final int GETCODESUCCESS = 0;
    private static final int GETCODEFAILED = 1;
    private static final int CHANGEPHONESUCESS = 2;
    private static final int CHANGEPHONEFAILED = 3;
    private int auth_code_id;
    private String phone;
    private String user_id;
    private String oldPhone;
    private String verification;
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
                            UIUtils.showToastCenter(ChangePhoneActivity.this,getCodeResponse.getMsg());
                        }
                    }
                    break;
                case GETCODEFAILED: //获取验证码失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
//                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    UIUtils.showToastCenter(ChangePhoneActivity.this,e.getMessage());
                    break;
                case CHANGEPHONESUCESS: //修改手机号成功
                    tv_commit.setEnabled(true);
                    loadingDialog.dismiss();
                    String string = (String) msg.obj;
                    LogUtils.e(TAG + "成功：" + string);
                    BaseResponse baseResponse = gson.fromJson(string,BaseResponse.class);
                    if(baseResponse != null){
                        if(baseResponse.getCode() == 0){
                            SharedPrefrenceUtils.setString(ChangePhoneActivity.this,"phone",phone);
                            setResult(200);
                            finish();
                        }else {
                            UIUtils.showToastCenter(ChangePhoneActivity.this,baseResponse.getMsg());
                        }
                    }
                    break;
                case CHANGEPHONEFAILED: //修改手机号失败失败
                    IOException e1 = (IOException) msg.obj;
                    tv_commit.setEnabled(true);
                    loadingDialog.dismiss();
//                    LogUtils.e(TAG + "失败：" + e1.getMessage());
                    UIUtils.showToastCenter(ChangePhoneActivity.this,e1.getMessage());
                    break;
            }
        }
    };
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_change_phone, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        et_phone = findViewById(R.id.et_phone);
        et_verification = findViewById(R.id.et_verification);
        tv_get_code = findViewById(R.id.tv_get_code);
        tv_commit = findViewById(R.id.tv_commit);

        initData();
        return rootView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandle != null){
            mHandle.removeCallbacksAndMessages(null);
        }
    }

    private void initData() {
        user_id = SharedPrefrenceUtils.getString(this,"usrs_id");
        oldPhone = SharedPrefrenceUtils.getString(this,"phone");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);

        iv_back.setOnClickListener(this);
        tv_get_code.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
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
            case R.id.tv_commit:
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
                changePhone();
                break;
        }
    }

    private void changePhone() {
        tv_commit.setEnabled(false);
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
        reqBody.put("old_mobile", oldPhone);
        reqBody.put("new_mobile", phone);
        reqBody.put("auth_code_id", String.valueOf(auth_code_id));
        reqBody.put("auth_code", verification);
        LogUtils.e(TAG + "修改手机号参数:" + reqBody.toString());
        netUtils.postDataAsynToNet(Constants.ChangePhoneUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = CHANGEPHONESUCESS;
                message.obj = str;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = CHANGEPHONEFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }

    private void getCode() {
        tv_get_code.setEnabled(false);
        Countdowmtimer(300000);

        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("user_phone",phone);
        reqBody.put("msg_type","2");
        netUtils.postDataAsynToNet(Constants.GetCodeUrl ,reqBody, new NetUtils.MyNetCall() {

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
                time = 300;
                tv_get_code.setText("获取验证码");
//                SpannableStringBuilder style=new SpannableStringBuilder(tv_time.getText());
//                style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink_text)), 8, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                tv_time.setText(style);
            }
        }.start();
    }
}
