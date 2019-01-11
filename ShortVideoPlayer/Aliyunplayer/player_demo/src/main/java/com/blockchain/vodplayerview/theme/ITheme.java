package com.blockchain.vodplayerview.theme;

import com.blockchain.vodplayerview.view.tipsview.ErrorView;
import com.blockchain.vodplayerview.view.tipsview.NetChangeView;
import com.blockchain.vodplayerview.view.tipsview.ReplayView;
import com.blockchain.vodplayerview.widget.AliyunVodPlayerView;
import com.blockchain.vodplayerview.view.control.ControlView;
import com.blockchain.vodplayerview.view.guide.GuideView;
import com.blockchain.vodplayerview.view.quality.QualityView;
import com.blockchain.vodplayerview.view.speed.SpeedView;
import com.blockchain.vodplayerview.view.tipsview.TipsView;
import com.blockchain.vodplayerview.widget.AliyunVodPlayerView;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 主题的接口。用于变换UI的主题。
 * 实现类有{@link ErrorView}，{@link NetChangeView} , {@link ReplayView} ,{@link ControlView},
 * {@link GuideView} , {@link QualityView}, {@link SpeedView} , {@link TipsView},
 * {@link AliyunVodPlayerView}
 */

public interface ITheme {
    /**
     * 设置主题
     * @param theme 支持的主题
     */
    void setTheme(AliyunVodPlayerView.Theme theme);
}
