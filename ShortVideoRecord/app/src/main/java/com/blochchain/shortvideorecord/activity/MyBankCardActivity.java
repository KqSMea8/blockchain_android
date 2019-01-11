package com.blochchain.shortvideorecord.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;

public class MyBankCardActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_back;
    private EditText et_bank_no;  //银行卡号
    private EditText et_bank_name;  //支行名称
    private TextView tv_submit;  //确定
    private String bank_no;
    private String bank_name;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_my_bank_card, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        et_bank_no = findViewById(R.id.et_bank_no);
        et_bank_name = findViewById(R.id.et_bank_name);
        tv_submit = findViewById(R.id.tv_submit);

        iv_back.setOnClickListener(this);
        tv_submit.setOnClickListener(this);

        initData();
        return rootView;
    }

    private void initData() {
        Intent intent = getIntent();
        bank_no = intent.getStringExtra("bank_card");
        bank_name = intent.getStringExtra("bank_name");
        if(!TextUtils.isEmpty(bank_no)){
            et_bank_no.setText(bank_no);
        }
        if(!TextUtils.isEmpty(bank_name)){
            et_bank_name.setText(bank_name);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_submit:
                bank_name = et_bank_name.getText().toString();
                bank_no = et_bank_no.getText().toString();
                if(TextUtils.isEmpty(bank_name)){
                    UIUtils.showToastCenter(MyBankCardActivity.this,"请输入支行名称");
                    return;
                }
                if(TextUtils.isEmpty(bank_no)){
                    UIUtils.showToastCenter(MyBankCardActivity.this,"请输入银行卡号");
                    return;
                }
                Intent intent = getIntent();
                intent.putExtra("bank_no",bank_no);
                intent.putExtra("bank_name",bank_name);
                setResult(200,intent);
                finish();
                break;
        }
    }
}
