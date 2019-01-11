package com.blockchain.shortvideoplayer.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.tabpager.ControlTabFragment;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.video.VerticalViewPager;
import com.blockchain.shortvideoplayer.widget.video.VerticalViewPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class VideoPlayActivity4 extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener,View.OnClickListener{
    private VerticalViewPager vvpBackPlay;
    private String videoListStr;
    private List<VideoBean> videoBeanList;
    private Gson gson;
    private int postion;
    private RadioGroup rg_content;
    private RadioButton rb_recommend_installment;
    private RadioButton rb_discover_installment;
    private RadioButton rb_subscribe_installment;
    private RadioButton rb_mine_installment;
    private ControlTabFragment ctf;
    private LinearLayout ll_recommend1;
    private ImageView iv_search1;
    private TextView tv_subscribe;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_video_play4, null);
        setContentView(rootView);

        vvpBackPlay = findViewById(R.id.vvp_back_play);
        rg_content =findViewById(R.id.rg_content);
        rb_recommend_installment = findViewById(R.id.rb_recommend_installment);
        rb_discover_installment = findViewById(R.id.rb_discover_installment);
        rb_subscribe_installment = findViewById(R.id.rb_subscribe_installment);
        rb_mine_installment = findViewById(R.id.rb_mine_installment);
        ll_recommend1 = findViewById(R.id.ll_recommend1);
        iv_search1 = findViewById(R.id.iv_search1);
        tv_subscribe = findViewById(R.id.tv_subscribe);

        rg_content.setOnCheckedChangeListener(this);
        ll_recommend1.setOnClickListener(this);
        iv_search1.setOnClickListener(this);
        tv_subscribe.setOnClickListener(this);
        initData();
        return rootView;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        return;
    }

    private void initData() {
        if(ctf == null){
            ctf = Main2Activity.getCtf();
        }
        gson = new Gson();
        Intent intent = getIntent();
        videoListStr= intent.getStringExtra("videoListStr");
        postion = intent.getIntExtra("postion",0);
        if(!TextUtils.isEmpty(videoListStr)){
            videoBeanList = gson.fromJson(videoListStr,new TypeToken<List<VideoBean>>(){}.getType());
            if(videoBeanList != null){
//                List<VideoBean> videoBeanList1 = new ArrayList<>();
//                if(postion >0 && videoBeanList.size()>postion){
//                    List<VideoBean> videoBeans = videoBeanList.subList(0,postion);
//                    List<VideoBean> videoBeanList2 = videoBeanList.subList(postion,videoBeanList.size());
//                    videoBeanList1.addAll(videoBeanList2);
//                    videoBeanList1.addAll(videoBeans);
//                }else {
//                    videoBeanList1.addAll(videoBeanList);
//                }
                VerticalViewPagerAdapter pagerAdapter = new VerticalViewPagerAdapter(getSupportFragmentManager(),false);
                vvpBackPlay.setVertical(true);
                if(videoBeanList.size()>3){
                    vvpBackPlay.setOffscreenPageLimit(3);
                }
                pagerAdapter.setUrlList(videoBeanList);
                vvpBackPlay.setAdapter(pagerAdapter);
            }
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_recommend_installment:
                break;
            case R.id.rb_discover_installment:
                ctf.setChecked(1);
                ctf.mCurrentIndex = 1;
                finish();
                break;
            case R.id.rb_subscribe_installment:
                ctf.setChecked(2);
                ctf.mCurrentIndex = 2;
                finish();
                break;
            case R.id.rb_mine_installment:
                ctf.setChecked(3);
                ctf.mCurrentIndex = 3;
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_recommend1:
                ctf.setChecked(0);
                ctf.mCurrentIndex = 0;
                finish();
                break;
            case R.id.iv_search1:
                Intent intent2 = new Intent(VideoPlayActivity4.this, RecommendSearchActivity.class);
                UIUtils.startActivity(intent2);
                finish();
                break;
            case R.id.tv_subscribe:
                ctf.setChecked(2);
                ctf.mCurrentIndex = 2;
                finish();
                break;
        }
    }
}
