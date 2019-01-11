package com.blockchain.shortvideoplayer.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.RecommendSearchActivity;
import com.blockchain.shortvideoplayer.adapter.MyFragmentAdapter;
import com.blockchain.shortvideoplayer.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecommendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendFragment extends BaseFragment implements ViewPager.OnPageChangeListener,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout ll_recommend;
    private TextView tv_recommend;
    private ImageView iv_recommend;
    private LinearLayout ll_hot;
    private TextView tv_hot;
    private ImageView iv_hot;
    private ImageView iv_search;
    private ViewPager viewpager;

    private RecommendRecFragment recommendRecFragment;
    private RecommendHotFragment recommendHotFragment;
    private MyFragmentAdapter myFragmentAdapter;

    private int pos;


    public RecommendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        ll_recommend = view.findViewById(R.id.ll_recommend);
        tv_recommend = view.findViewById(R.id.tv_recommend);
        iv_recommend = view.findViewById(R.id.iv_recommend);
        ll_hot = view.findViewById(R.id.ll_hot);
        tv_hot = view.findViewById(R.id.tv_hot);
        iv_hot = view.findViewById(R.id.iv_hot);
        iv_search = view.findViewById(R.id.iv_search);
        viewpager = view.findViewById(R.id.viewpager);

        return view;
    }

    @Override
    protected void loadData() {
        initData();
    }

    private void initData() {
        recommendHotFragment = RecommendHotFragment.newInstance("recommend_hot","");
        recommendRecFragment = RecommendRecFragment.newInstance("recommend_rec","");

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(recommendRecFragment);
        fragmentList.add(recommendHotFragment);
        myFragmentAdapter = new MyFragmentAdapter(getChildFragmentManager(), fragmentList);
        viewpager.setAdapter(myFragmentAdapter);

        viewpager.setCurrentItem(0);
        switchTitle(0);
        viewpager.setOnPageChangeListener(this);

        ll_recommend.setOnClickListener(this);
        ll_hot.setOnClickListener(this);
        iv_hot.setOnClickListener(this);
        iv_search.setOnClickListener(this);
    }

    private void switchTitle(int i) {
        switch (i){
            case 0:
                tv_recommend.setTextColor(UIUtils.getColor(R.color.text_color_black));
                tv_hot.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                iv_recommend.setImageResource(R.mipmap.bottom_blue);
                iv_hot.setImageResource(R.mipmap.bottom_white);
                break;
            case 1:
                tv_recommend.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                tv_hot.setTextColor(UIUtils.getColor(R.color.text_color_black));
                iv_recommend.setImageResource(R.mipmap.bottom_white);
                iv_hot.setImageResource(R.mipmap.bottom_blue);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switchTitle(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_recommend:
                pos = 0;
                switchTitle(pos);
                viewpager.setCurrentItem(pos);
                break;
            case R.id.ll_hot:
                pos = 1;
                switchTitle(pos);
                viewpager.setCurrentItem(pos);
                break;
            case R.id.iv_search:
                Intent intent = new Intent(getActivity(), RecommendSearchActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in,R.anim.animnext_out);
                break;
        }
    }
}
