package com.blockchain.shortvideoplayer.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.MyFragmentAdapter;
import com.blockchain.shortvideoplayer.fragment.DiscoverFragment;
import com.blockchain.shortvideoplayer.fragment.MineFragment;
import com.blockchain.shortvideoplayer.fragment.RecommendFragment;
import com.blockchain.shortvideoplayer.fragment.SubscribeFragment;
import com.blockchain.shortvideoplayer.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener,SubscribeFragment.OnFragmentInteractionListener,RadioGroup.OnCheckedChangeListener {
    private ViewPager viewPager;
    private RadioGroup rg_content;
    private RadioButton rb_recommend_installment;
    private RadioButton rb_discover_installment;
    private RadioButton rb_subscribe_installment;
    private RadioButton rb_mine_installment;

    private DiscoverFragment discoverFragment;
    private MineFragment mineFragment;
    private RecommendFragment recommendFragment;
    private SubscribeFragment subscribeFragment;
    private MyFragmentAdapter myFragmentAdapter;
    private static int sOffScreenLimit = 1;

    private int pos;
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_main, null);
        setContentView(rootView);

        viewPager = findViewById(R.id.viewpager);
        rg_content = findViewById(R.id.rg_content);
        rb_recommend_installment = findViewById(R.id.rb_recommend_installment);
        rb_discover_installment = findViewById(R.id.rb_discover_installment);
        rb_subscribe_installment = findViewById(R.id.rb_subscribe_installment);
        rb_mine_installment = findViewById(R.id.rb_mine_installment);


        initData();
        return rootView;
    }

    private void initData() {
        discoverFragment = DiscoverFragment.newInstance("DiscoverFragment","");
        mineFragment = MineFragment.newInstance("MineFragment","");
        recommendFragment = RecommendFragment.newInstance("RecommendFragement","");
        subscribeFragment = SubscribeFragment.newInstance("SubScribeFragment","");

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(recommendFragment);
        fragmentList.add(discoverFragment);
        fragmentList.add(subscribeFragment);
        fragmentList.add(mineFragment);
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myFragmentAdapter);
        if(sOffScreenLimit >1){
            viewPager.setOffscreenPageLimit(sOffScreenLimit);
        }
        viewPager.setCurrentItem(0);
        switchRadioButton(0);
        viewPager.setOnPageChangeListener(this);
        rg_content.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switchRadioButton(position);

    }

    private void switchRadioButton(int position) {
        switch (position){
            case 0:
                rb_recommend_installment.setChecked(true);
                rb_recommend_installment.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                rb_discover_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_subscribe_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_mine_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                break;
            case 1:
                rb_discover_installment.setChecked(true);
                rb_recommend_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_discover_installment.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                rb_subscribe_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_mine_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                break;
            case 2:
                rb_subscribe_installment.setChecked(true);
                rb_recommend_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_discover_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_subscribe_installment.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                rb_mine_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                break;
            case 3:
                rb_mine_installment.setChecked(true);
                rb_recommend_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_discover_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_subscribe_installment.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                rb_mine_installment.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId){
            case R.id.rb_recommend_installment:
                pos = 0;
                break;
            case R.id.rb_discover_installment:
                pos = 1;
                break;
            case R.id.rb_subscribe_installment:
                pos = 2;
                break;
            case R.id.rb_mine_installment:
                pos = 3;
                break;
        }
        switchRadioButton(pos);
        viewPager.setCurrentItem(pos);
    }
}


