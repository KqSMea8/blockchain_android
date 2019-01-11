package com.aliyun.demo.effects.time;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.demo.editor.R;
import com.aliyun.demo.effects.control.BaseChooser;
import com.aliyun.demo.effects.control.EffectInfo;
import com.aliyun.demo.effects.control.UIEditorPage;
import com.aliyun.editor.TimeEffectType;

import java.util.ArrayList;
import java.util.List;

public class TimeChooserMediator extends BaseChooser implements View.OnClickListener {

    private ImageView mTimeNone, mTimeSlow, mTimeFast, mTimeRepeat2Invert;
    private TextView mTimeMoment, mTimeAll;
    private boolean mIsMoment = true;
    private Integer currentIndex;
    private Integer currentMode;

    private List<View> viewList = new ArrayList<>();

    /**
     * 速度的模式下标,  某刻:0   全程:1
     */
    private static final int TIME_MODE_INDEX_MOMENT = 0;
    private static final int TIME_MODE_INDEX_ALL = 1;

    public static TimeChooserMediator newInstance() {
        TimeChooserMediator dialog = new TimeChooserMediator();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        currentMode = (Integer)arguments.get("mode");
        currentIndex = (Integer)arguments.get("index");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.aliyun_svideo_time_view, container);
        mTimeNone = (ImageView)rootView.findViewById(R.id.time_effect_none);
        mTimeNone.setOnClickListener(this);
        mTimeSlow = (ImageView)rootView.findViewById(R.id.time_effect_slow);
        mTimeSlow.setOnClickListener(this);
        mTimeFast = (ImageView)rootView.findViewById(R.id.time_effect_speed_up);
        mTimeFast.setOnClickListener(this);
        mTimeRepeat2Invert = (ImageView)rootView.findViewById(R.id.time_effect_repeat_invert);
        mTimeRepeat2Invert.setOnClickListener(this);
        mTimeMoment = (TextView)rootView.findViewById(R.id.time_moment);
        mTimeMoment.setOnClickListener(this);
        mTimeAll = (TextView)rootView.findViewById(R.id.time_all);
        mTimeAll.setOnClickListener(this);
        mTimeMoment.performClick();
        viewList.add(mTimeNone);
        viewList.add(mTimeSlow);
        viewList.add(mTimeFast);
        viewList.add(mTimeRepeat2Invert);
        selectNormal();
        return rootView;
    }

    private void selectNormal() {
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            if (i == currentIndex) {
                viewList.get(i).setSelected(true);
            } else {
                viewList.get(i).setSelected(false);
            }
        }

        if (currentMode == TIME_MODE_INDEX_MOMENT) {
            speedModeChange(true);
        } else if (currentMode == TIME_MODE_INDEX_ALL) {
            speedModeChange(false);
        }
    }

    private void resetBtn() {
        mTimeNone.setSelected(false);
        mTimeSlow.setSelected(false);
        mTimeFast.setSelected(false);
        mTimeRepeat2Invert.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        resetBtn();
        v.setSelected(true);
        if (id == R.id.time_effect_none) {
            if (mOnEffectChangeListener != null) {
                EffectInfo effectInfo = new EffectInfo();
                effectInfo.type = UIEditorPage.TIME;
                effectInfo.timeEffectType = TimeEffectType.TIME_EFFECT_TYPE_NONE;
                effectInfo.isMoment = mIsMoment;
                mOnEffectChangeListener.onEffectChange(effectInfo);
            }
        } else if (id == R.id.time_effect_slow) {
            if (mOnEffectChangeListener != null) {
                EffectInfo effectInfo = new EffectInfo();
                effectInfo.type = UIEditorPage.TIME;
                effectInfo.timeEffectType = TimeEffectType.TIME_EFFECT_TYPE_RATE;
                effectInfo.timeParam = 0.5f;
                effectInfo.isMoment = mIsMoment;
                mOnEffectChangeListener.onEffectChange(effectInfo);
            }

        } else if (id == R.id.time_effect_speed_up) {
            if (mOnEffectChangeListener != null) {
                EffectInfo effectInfo = new EffectInfo();
                effectInfo.type = UIEditorPage.TIME;
                effectInfo.timeEffectType = TimeEffectType.TIME_EFFECT_TYPE_RATE;
                effectInfo.timeParam = 2.0f;
                effectInfo.isMoment = mIsMoment;
                mOnEffectChangeListener.onEffectChange(effectInfo);
            }

        } else if (id == R.id.time_effect_repeat_invert) {
            if (mOnEffectChangeListener != null) {
                EffectInfo effectInfo = new EffectInfo();
                effectInfo.type = UIEditorPage.TIME;
                effectInfo.isMoment = mIsMoment;
                if (mIsMoment) {
                    effectInfo.timeEffectType = TimeEffectType.TIME_EFFECT_TYPE_REPEAT;
                } else {
                    effectInfo.timeEffectType = TimeEffectType.TIME_EFFECT_TYPE_INVERT;
                }
                mOnEffectChangeListener.onEffectChange(effectInfo);
            }
        } else if (id == R.id.time_moment) {
            speedModeChange(true);
        } else if (id == R.id.time_all) {
            speedModeChange(false);
        }

    }

    private void speedModeChange(boolean isMoment) {
        if (isMoment) {
            mTimeAll.setTextColor(getResources().getColor(R.color.aliyun_powered_text_color));
            mTimeMoment.setTextColor(getResources().getColor(R.color.white));
            mTimeRepeat2Invert.setImageResource(R.drawable.aliyun_svideo_video_edit_time_repeat_selector);
            mIsMoment = true;
        } else {
            mTimeMoment.setTextColor(getResources().getColor(R.color.aliyun_powered_text_color));
            mTimeAll.setTextColor(getResources().getColor(R.color.white));
            mTimeRepeat2Invert.setImageResource(R.drawable.aliyun_svideo_video_edit_time_invert_selector);
            mIsMoment = false;
        }

    }

}

