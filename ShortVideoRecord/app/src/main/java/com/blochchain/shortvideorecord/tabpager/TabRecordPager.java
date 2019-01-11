package com.blochchain.shortvideorecord.tabpager;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blockchain.shortvideorecord.R;


/**
 * @作者: 刘敏
 * @创建时间: 2016-4-27下午5:24:30
 * @版权: 特速版权所有
 * @描述: 详情页面
 * @更新人:
 * @更新时间:
 * @更新内容: TODO
 */
public class TabRecordPager extends ViewTabBasePager {
    protected static final int ERROR = 0;

    protected static final int SUCCESS = 1;

    protected static final int TIME_OUT = 2;
    protected static final int NETWORK_NOT_OPEN = 3;
    protected static final int EMPTY = 4;
    // 0 没有下一页
    private int next;

    /**
     * 关注品牌的页数，1表示第一页
     */
    private int currentPage = 1;
    private String scan_text;
    //css显示图片样式
    private String CSS_STYPE = "<head><style>img{max-width:340px !important;}</style></head>";
    private TextView tv_scan_text;
    public TabRecordPager(Context context) {
        super(context);
    }


    @Override
    protected View initView() {
        View view = View.inflate(mContext,
                R.layout.pager_record, null);
//        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        LogUtils.e("第一次:sasd");
    }



}