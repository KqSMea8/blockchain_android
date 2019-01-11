package com.blochchain.shortvideorecord.tabpager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @作者: 许明达
 * @创建时间: 2016年3月15日上午9:48:32
 * @版权: 特速
 * @描述: fragment的基类	
 */
public abstract class BaseFragment extends Fragment
{
	protected Activity mActivity;	// fragment的宿主
	protected View rootView;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.mActivity = getActivity();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = initView();
		return rootView;
	}
	
	/**
	 * @return the rootView
	 */   
	public View getRootView() {
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// 数据加载的操作
		initData();
	}

	/**
	 * 数据加载的方法，子类如果需要加载数据，就复写这个方法
	 */
	protected void initData()
	{
	}

	protected abstract View initView();
}
