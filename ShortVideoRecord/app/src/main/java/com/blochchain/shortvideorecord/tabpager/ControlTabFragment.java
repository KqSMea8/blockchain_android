package com.blochchain.shortvideorecord.tabpager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blochchain.shortvideorecord.activity.RecordActivity;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;

import java.util.ArrayList;
import java.util.List;

public class ControlTabFragment extends BaseFragment implements
        RadioGroup.OnCheckedChangeListener {
    private static final String TAG = ControlTabFragment.class.getSimpleName();
    private RadioGroup mRadioGroup;
    private FrameLayout mFrameLayout;
    private RadioButton rb_main_installment;  //首页
    private RadioButton rb_income_installment;  //收益
    private RadioButton rb_record_installment;  //拍摄
    private RadioButton rb_product_installment;  //作品
    private RadioButton rb_mine_installment;  //我的

    private TabMainPager tabMainPager; //首页
    private TabIncomePager tabIncomePager; //收益
    private TabRecordPager tabRecordPager; //拍摄
    private TabProductPager tabProductPager; //作品
    private TabMinePager tabMinePager;  //我的

    // 默认选中第0页面
    public static int mCurrentIndex = 0;
    // 默认选中第0页面
    public static int beforeIndex = 0;
    private List<ViewTabBasePager> mPagerList;
    List<RadioButton> radioButtonList;
    public final int RECORD_CODE = 1;
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.rb_main_installment:
                beforeIndex = 0;
                mCurrentIndex = 0;
                break;
            case R.id.rb_income_installment:
                beforeIndex = 1;
                mCurrentIndex = 1;
                break;
            case R.id.rb_record_installment:
                beforeIndex = 2;
                mCurrentIndex = 2;
                break;
            case R.id.rb_product_installment:
                beforeIndex = 3;
                mCurrentIndex = 3;
                break;
            case R.id.rb_mine_installment:
                beforeIndex = 4;
                mCurrentIndex = 4;
                break;
            default:
                break;
        }
        switchCurrentPage();
    }

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.control_tab, null);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.rg_content);
        rb_main_installment = (RadioButton) view.findViewById(R.id.rb_main_installment);
        rb_income_installment = (RadioButton) view.findViewById(R.id.rb_income_installment);
        rb_record_installment = (RadioButton) view.findViewById(R.id.rb_record_installment);
        rb_product_installment = (RadioButton) view.findViewById(R.id.rb_product_installment);
        rb_mine_installment = (RadioButton) view.findViewById(R.id.rb_mine_installment);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.fl_content_fragment);

        return view;
    }

    @Override
    protected void initData() {
        mPagerList = new ArrayList<ViewTabBasePager>();
        tabMainPager = new TabMainPager(mActivity);
        mPagerList.add(tabMainPager);
        //收益
        tabIncomePager = new TabIncomePager(mActivity);
        mPagerList.add(tabIncomePager);
        // 拍摄
        tabRecordPager = new TabRecordPager(mActivity);
        mPagerList.add(tabRecordPager);
        // 作品
        tabProductPager = new TabProductPager(mActivity);
        mPagerList.add(tabProductPager);
        // 我的
        tabMinePager = new TabMinePager(mActivity);
        mPagerList.add(tabMinePager);
        // 给RadioGroup 设置监听
        getmRadioGroup().setOnCheckedChangeListener(this);
        if (radioButtonList == null) {
            radioButtonList = new ArrayList<RadioButton>();
            radioButtonList.add(rb_main_installment);
            radioButtonList.add(rb_income_installment);
            radioButtonList.add(rb_record_installment);
            radioButtonList.add(rb_product_installment);
            radioButtonList.add(rb_mine_installment);
        }
        switchCurrentPage();

        rb_record_installment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setChecked(int item) {
        switch (item) {
            case 0:
                if(rb_main_installment!=null){
                    rb_main_installment.setChecked(true);
                }
                break;
            case 1:
                if(rb_income_installment != null){
                    rb_income_installment.setChecked(true);
                }
                break;
            case 2:
                if(rb_record_installment != null){
                    rb_record_installment.setChecked(true);
                }
                break;
            case 3:
                if(rb_product_installment != null){
                    rb_product_installment.setChecked(true);
                }
                break;
            case 4:
                if(rb_mine_installment != null){
                    rb_mine_installment.setChecked(true);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 切换RadioGroup对应的页面
     */
    public void switchCurrentPage() {
        if(mFrameLayout != null){
            mFrameLayout.removeAllViews();
            ViewTabBasePager tabBasePager = mPagerList.get(mCurrentIndex);
            // 获得每个页面对应的布局
            View rootView = tabBasePager.getRootView();

            LogUtils.e(TAG + "mCurrentIndex:"+mCurrentIndex);

            // 填充数据
            if (mCurrentIndex == 2) {
                tabBasePager.initData();
                mFrameLayout.addView(rootView);
                Intent recordIntent = new Intent(UIUtils.getContext(),
                        RecordActivity.class);
                UIUtils.startActivityForResult(recordIntent,RECORD_CODE);

            }
//            else if(mCurrentIndex == 2){
//                // 没有登录 登录页面
//                TabBasePager tabLoginPager;
//
//                if (LoginUtils.isLogin()) {
//                    View myselfView = tabBasePager.getRootView();
//                    tabBasePager.initData();
//                    mFrameLayout.addView(myselfView);
//                    tabMyselfPager.Update();
//                } else {
//                    // 进入登录注册页面
//                    Intent inent=new Intent(UIUtils.getContext(), LoginActivity.class);
//                    UIUtils.startActivityNextAnim(inent);
//                }
//            }
            else {
                tabBasePager.initData();
                mFrameLayout.addView(rootView);
            }
            setSelectedColor(mCurrentIndex);
        }
    }


    public RadioGroup getmRadioGroup() {
        return mRadioGroup;
    }

    public TabMainPager getTabMainPager() {
        return tabMainPager;
    }

    public TabIncomePager getTabIncomePager() {
        return tabIncomePager;
    }

    public TabRecordPager getTabRecordPager() {
        return tabRecordPager;
    }
    public TabProductPager getTabProductPager() {
        return tabProductPager;
    }
    public TabMinePager getTabMinePager() {
        return tabMinePager;
    }

    public void setSelectedColor(int item) {
        for (int i = 0; i < radioButtonList.size(); i++) {
            RadioButton radioButton = radioButtonList.get(i);
            if (i == item) {
                //渐变色
                Shader shader = new LinearGradient(0, 0, 0, 60, UIUtils.getColor(R.color.text_color_blue), UIUtils.getColor(R.color.text_color_blue), Shader.TileMode.CLAMP);
                radioButton.getPaint().setShader(shader);
            } else {
                Shader shader = new LinearGradient(0, 0, 0, 0, UIUtils.getColor(R.color.text_color_gray), UIUtils.getColor(R.color.text_color_gray), Shader.TileMode.CLAMP);
                radioButton.getPaint().setShader(shader);
            }
            radioButton.postInvalidate();
        }
    }

}
