package com.blochchain.shortvideorecord.tabpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * @作者: 刘敏
 * @创建时间: 2015-4-27下午5:26:31
 * @版权: 特速版权所有
 * @描述: 附近商场的每一个子页面的controller的基类
 * @更新人: 
 * @更新时间:
 * @更新内容: TODO	 	
 */
public abstract class ViewTabBasePager {
	protected Context mContext; // 上下文
	protected View mRootView; // 根视图

	public ViewTabBasePager(Context context) {
		this.mContext = context;
		mRootView = initView();
	}

	public static class MyTabHandler extends Handler {
		WeakReference<ViewTabBasePager> mWeakReference;
		public MyTabHandler(ViewTabBasePager basePager)
		{
			mWeakReference=new WeakReference<ViewTabBasePager>(basePager);
		}
		@Override
		public void handleMessage(Message msg) {
		}
	}

	protected abstract View initView();
	/**
	 * 数据加载的方法，子类如果要实现数据加载，就需要复写这个方法
	 */
	public void initData() {

	}

	public View getRootView() {
		return mRootView;
	}

}
