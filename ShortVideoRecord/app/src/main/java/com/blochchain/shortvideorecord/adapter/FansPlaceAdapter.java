package com.blochchain.shortvideorecord.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blochchain.shortvideorecord.model.FansPlaceModel;
import com.blockchain.shortvideorecord.R;

import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class FansPlaceAdapter extends BaseAdapter{

    private String TAG=FansPlaceAdapter.class.getSimpleName();
    private List<FansPlaceModel> fansPlaceModelList;
    private Context mContext;
    private int  selectItem=-1;
    public FansPlaceAdapter(Context context, List<FansPlaceModel> fansPlaceModelList){
        mContext=context;
        this.fansPlaceModelList=fansPlaceModelList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return fansPlaceModelList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        final ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.fans_place_item, null);
            vh=new ViewHolder();
            vh.tv_place=view.findViewById(R.id.tv_place);
            vh.tv_ratio=view.findViewById(R.id.tv_ratio);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        FansPlaceModel fansPlaceModel = fansPlaceModelList.get(pos);
        if(!TextUtils.isEmpty(fansPlaceModel.getPlace())){
            vh.tv_place.setText((pos+1) + " "+fansPlaceModel.getPlace());
        }
        if(!TextUtils.isEmpty(fansPlaceModel.getRatio())){
            vh.tv_ratio.setText(fansPlaceModel.getRatio());
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return fansPlaceModelList.size();
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder{
        public TextView tv_place;
        public TextView tv_ratio;
    }
}
