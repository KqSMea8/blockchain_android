package com.blockchain.shortvideoplayer.tabpager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @作者: 许明达
 * @创建时间: 2015年3月31日下午3:14:18
 * @版权: 微位科技版权所有
 * @描述: 控制侧滑菜单以及 附近 找店 发现 我的四个页面
 */
public class ControlTabFragment extends MyBaseFragment implements
        RadioGroup.OnCheckedChangeListener {
    private static final String TAG = ControlTabFragment.class.getSimpleName();

    private ViewPager viewPager;
    private RadioGroup rg_content;
    private RadioButton rb_recommend_installment;
    private RadioButton rb_discover_installment;
    private RadioButton rb_subscribe_installment;
    private RadioButton rb_mine_installment;
    private FrameLayout fl_content_fragment;

    private TabDiscoverPager tabDiscoverPager;  //发现
    private TabMinePager tabMinePager;    //我的
    private TabRecommendPager tabRecommendPager;  //推荐
    private TabSubscribePager tabSubscribePager;   //订阅

    private List<ViewTabBasePager> mPagerList;
    List<RadioButton> radioButtonList;
    // 默认选中第0页面
    public static int mCurrentIndex = 0;
    // 默认选中第0页面
    public static int beforeIndex = 0;

    private boolean useThemestatusBarColor = true;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    private boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.rb_recommend_installment:
                beforeIndex = 0;
                mCurrentIndex = 0;
                break;
            case R.id.rb_discover_installment:
                beforeIndex = 1;
                mCurrentIndex = 1;
                break;
            case R.id.rb_subscribe_installment:
                beforeIndex = 2;
                mCurrentIndex = 2;
                break;
            case R.id.rb_mine_installment:
                beforeIndex = 3;
                mCurrentIndex = 3;
                break;
            default:
                break;
        }
        switchCurrentPage();
    }

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.control_tab, null);
        rg_content = (RadioGroup) view.findViewById(R.id.rg_content);
        rb_recommend_installment = (RadioButton) view.findViewById(R.id.rb_recommend_installment);
        rb_discover_installment = (RadioButton) view.findViewById(R.id.rb_discover_installment);
        rb_subscribe_installment = (RadioButton) view.findViewById(R.id.rb_subscribe_installment);
        rb_mine_installment = (RadioButton) view.findViewById(R.id.rb_mine_installment);
        fl_content_fragment = (FrameLayout) view.findViewById(R.id.fl_content_fragment);

        return view;
    }

    @Override
    protected void initData() {
        mPagerList = new ArrayList<ViewTabBasePager>();
        //推荐
        tabRecommendPager = new TabRecommendPager(mActivity);
        mPagerList.add(tabRecommendPager);
        //发现
        tabDiscoverPager = new TabDiscoverPager(mActivity);
        mPagerList.add(tabDiscoverPager);
        // 订阅
        tabSubscribePager = new TabSubscribePager(mActivity);
        mPagerList.add(tabSubscribePager);
        // 我的
        tabMinePager = new TabMinePager(mActivity);
        mPagerList.add(tabMinePager);
        // 给RadioGroup 设置监听
        getmRadioGroup().setOnCheckedChangeListener(this);
        if (radioButtonList == null) {
            radioButtonList = new ArrayList<RadioButton>();
            radioButtonList.add(rb_recommend_installment);
            radioButtonList.add(rb_discover_installment);
            radioButtonList.add(rb_subscribe_installment);
            radioButtonList.add(rb_mine_installment);
        }
        switchCurrentPage();

    }

    public void setChecked(int item) {
        switch (item) {
            case 0:
                if(rb_recommend_installment!=null){
                    rb_recommend_installment.setChecked(true);
                }
                break;
            case 1:
                if(rb_discover_installment != null){
                    rb_discover_installment.setChecked(true);
                }
                break;
            case 2:
                if(rb_subscribe_installment != null){
                    rb_subscribe_installment.setChecked(true);
                }
                break;
            case 3:
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
        if(fl_content_fragment != null){
            fl_content_fragment.removeAllViews();
            ViewTabBasePager tabBasePager = mPagerList.get(mCurrentIndex);
            // 获得每个页面对应的布局
            View rootView = tabBasePager.getRootView();

            LogUtils.e(TAG + "mCurrentIndex:"+mCurrentIndex);


            // 填充数据
                tabBasePager.initData();
            fl_content_fragment.addView(rootView);
            setSelectedColor(mCurrentIndex);

//            if(tabRecommendPager != null){
//                tabRecommendPager.releaseVideo(0);
//            }

        }
    }


    public RadioGroup getmRadioGroup() {
        return rg_content;
    }

    public TabSubscribePager getTabSubscribPager() {
        return tabSubscribePager;
    }

    public TabDiscoverPager getTabDiscoverPager() {
        return tabDiscoverPager;
    }

    public TabRecommendPager getTabRecommendPager() {
        return tabRecommendPager;
    }
    public TabMinePager getTabMinePager() {
        return tabMinePager;
    }

    public void setSelectedColor(int item) {
        for (int i = 0; i < radioButtonList.size(); i++) {
            RadioButton radioButton = radioButtonList.get(i);
            if (i == item) {
                //渐变色
                Shader shader = new LinearGradient(0, 0, 0, 60, UIUtils.getColor(R.color.text_color_black), UIUtils.getColor(R.color.text_color_black), Shader.TileMode.CLAMP);
                radioButton.getPaint().setShader(shader);
            } else {
                Shader shader = new LinearGradient(0, 0, 0, 0, UIUtils.getColor(R.color.text_color_light_gray), UIUtils.getColor(R.color.text_color_light_gray), Shader.TileMode.CLAMP);
                radioButton.getPaint().setShader(shader);
            }
            radioButton.postInvalidate();
        }
    }

    public void setStatusBar(int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = mActivity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                mActivity.getWindow().setStatusBarColor(getResources().getColor(colorId));//设置状态栏背景色
            } else {
                mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = mActivity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {
//            Toast.makeText(mActivity, "低于4.4的android系统版本不存在沉浸式状态栏", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
//            if(checkDeviceHasNavigationBar(this)){
//                getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0, 0, 0, UIUtils.getNavigationBarHeight(this));
//            }
//        }

    }

    //判断是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }
}
