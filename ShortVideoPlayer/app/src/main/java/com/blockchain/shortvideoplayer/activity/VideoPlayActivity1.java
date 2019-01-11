package com.blockchain.shortvideoplayer.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.widget.video.VerticalViewPager;
import com.blockchain.shortvideoplayer.widget.video.VerticalViewPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class VideoPlayActivity1 extends BaseActivity {
    private VerticalViewPager vvpBackPlay;
    private String videoListStr;
    private List<VideoBean> videoBeanList;
    private Gson gson;
    private int postion;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View rootView = View.inflate(this, R.layout.activity_video_play1, null);
        setContentView(rootView);

        vvpBackPlay = findViewById(R.id.vvp_back_play);
        initData();
        return rootView;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        return;
    }

    private void initData() {
        gson = new Gson();
        Intent intent = getIntent();
        videoListStr= intent.getStringExtra("videoListStr");
        postion = intent.getIntExtra("postion",0);
        if(!TextUtils.isEmpty(videoListStr)){
            videoBeanList = gson.fromJson(videoListStr,new TypeToken<List<VideoBean>>(){}.getType());
            if(videoBeanList != null){
                List<VideoBean> videoBeanList1 = new ArrayList<>();
                if(postion >0 && videoBeanList.size()>postion){
                    List<VideoBean> videoBeans = videoBeanList.subList(0,postion);
                    List<VideoBean> videoBeanList2 = videoBeanList.subList(postion,videoBeanList.size());
                    videoBeanList1.addAll(videoBeanList2);
                    videoBeanList1.addAll(videoBeans);
                }else {
                    videoBeanList1.addAll(videoBeanList);
                }
                VerticalViewPagerAdapter pagerAdapter = new VerticalViewPagerAdapter(getSupportFragmentManager(),true);
                vvpBackPlay.setVertical(true);
                if(videoBeanList.size()>3){
                    vvpBackPlay.setOffscreenPageLimit(3);
                }
                pagerAdapter.setUrlList(videoBeanList1);
                vvpBackPlay.setAdapter(pagerAdapter);

            }
        }

    }

}
