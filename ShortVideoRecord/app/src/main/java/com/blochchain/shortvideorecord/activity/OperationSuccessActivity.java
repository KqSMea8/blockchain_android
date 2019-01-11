package com.blochchain.shortvideorecord.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.blockchain.shortvideorecord.R;

public class OperationSuccessActivity extends BaseActivity {
    private ImageView iv_back;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_operation_success, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        return rootView;
    }
}
