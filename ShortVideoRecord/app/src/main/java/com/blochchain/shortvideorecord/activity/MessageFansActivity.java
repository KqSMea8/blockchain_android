package com.blochchain.shortvideorecord.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.blochchain.shortvideorecord.adapter.MediaRecordAdapter;
import com.blochchain.shortvideorecord.model.MediaRecordBean;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MessageFansActivity extends BaseActivity {
    private ImageView iv_back;
    private ListView lv_message;
    private List<MediaRecordBean> mediaRecordBeanList;
    private MediaRecordAdapter mediaRecordAdapter;
    private String mediaRecordListStr;
    private Gson gson;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_message_fans, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        lv_message = findViewById(R.id.lv_message);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
        return rootView;
    }

    private void initData() {
        gson = new Gson();
        final Intent intent = getIntent();
        mediaRecordListStr = intent.getStringExtra("mediaRecordListStr");
        mediaRecordBeanList = gson.fromJson(mediaRecordListStr,new TypeToken<List<MediaRecordBean>>(){}.getType());

//        mediaRecordBeanList = new ArrayList<>();
//        mediaRecordBeanList.add(new MediaRecordBean());
//        mediaRecordBeanList.add(new MediaRecordBean());
//        mediaRecordBeanList.add(new MediaRecordBean());
//        mediaRecordBeanList.add(new MediaRecordBean());
//        mediaRecordBeanList.add(new MediaRecordBean());

        if(mediaRecordBeanList != null){
            mediaRecordAdapter = new MediaRecordAdapter(this,mediaRecordBeanList);
            lv_message.setAdapter(mediaRecordAdapter);
        }
        lv_message.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MediaRecordBean mediaRecordBean = mediaRecordBeanList.get(i);
                Intent intent1 = new Intent(MessageFansActivity.this,MessageDetailActivity.class);
                intent1.putExtra("usrs_id",mediaRecordBean.getUsrs_id());
                UIUtils.startActivity(intent1);
            }
        });
    }
}
