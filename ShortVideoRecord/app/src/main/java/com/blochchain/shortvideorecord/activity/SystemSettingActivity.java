package com.blochchain.shortvideorecord.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blochchain.shortvideorecord.tabpager.ControlTabFragment;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blochchain.shortvideorecord.utils.Utils;
import com.blockchain.shortvideorecord.R;

public class SystemSettingActivity extends BaseActivity implements View.OnClickListener{
    private LinearLayout ll_phone;
    private TextView tv_phone;
    private TextView tv_version;
    private TextView tv_logout;
    private ImageView iv_back;
    private AlertDialog logoutDialog;
    private ControlTabFragment ctf;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_system_setting, null);
        setContentView(rootView);

        ll_phone = findViewById(R.id.ll_phone);
        tv_phone = findViewById(R.id.tv_phone);
        tv_version = findViewById(R.id.tv_version);
        tv_logout = findViewById(R.id.tv_logout);
        iv_back = findViewById(R.id.iv_back);

        ll_phone.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        initData();
        return rootView;
    }

    private void initData() {
        String version = Utils.getVersionName(this);
        if(!TextUtils.isEmpty(version)){
            tv_version.setText(version);
        }

        String phone = SharedPrefrenceUtils.getString(this, "phone");
        if (!TextUtils.isEmpty(phone)) {
            tv_phone.setText(phone);
        }
        if (ctf == null) {
            ctf = Main2Activity.getCtf();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode == 200){
                String phone = SharedPrefrenceUtils.getString(this, "phone");
                if (!TextUtils.isEmpty(phone)) {
                    tv_phone.setText(phone);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_phone:
                Intent intent = new Intent(this,ChangePhoneActivity.class);
                UIUtils.startActivityForResult(intent,100);
                overridePendingTransition(R.anim.animnext_in,R.anim.animnext_out);
                break;
            case R.id.tv_logout:
                logoutDialog = DialogUtils.showLogoutDialog(SystemSettingActivity.this, null, this);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_cancle:
                logoutDialog.dismiss();
                break;
            case R.id.tv_ensure:
                logout();
                ctf.setChecked(0);
                ctf.mCurrentIndex = 0;
                finish();
                break;
        }
    }
    private void logout() {
        SharedPrefrenceUtils.setString(this, "phone", "");
        SharedPrefrenceUtils.setString(this, "self_media_id", "");
        SharedPrefrenceUtils.setInt(this,"is_first",-1);
    }
}
