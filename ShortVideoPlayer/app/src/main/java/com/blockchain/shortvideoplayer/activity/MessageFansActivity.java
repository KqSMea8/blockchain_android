package com.blockchain.shortvideoplayer.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.MediaRecordAdapter;
import com.blockchain.shortvideoplayer.model.MediaRecordBean;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MessageFansActivity extends BaseActivity {
    private ListView lv_message;
    private List<MediaRecordBean> mediaRecordBeanList;
    private MediaRecordAdapter mediaRecordAdapter;
    private String mediaRecordListStr;
    private Gson gson;
    private ImageView iv_back;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_message_fans, null);
        setContentView(rootView);

        lv_message = findViewById(R.id.lv_message);
        iv_back = findViewById(R.id.iv_back);

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
                intent1.putExtra("self_media_id",mediaRecordBean.getSelf_media_id());
                UIUtils.startActivity(intent1);
            }
        });
    }
}
