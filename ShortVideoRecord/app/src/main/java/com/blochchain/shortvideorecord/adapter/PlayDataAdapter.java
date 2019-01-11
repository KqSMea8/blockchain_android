package com.blochchain.shortvideorecord.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blochchain.shortvideorecord.model.FansPlaceModel;
import com.blochchain.shortvideorecord.model.PlayDataModel;
import com.blockchain.shortvideorecord.R;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class PlayDataAdapter extends BaseAdapter{

    private String TAG=PlayDataAdapter.class.getSimpleName();
    private List<PlayDataModel> playDataModelList;
    private Context mContext;
    private int  selectItem=-1;
    public PlayDataAdapter(Context context, List<PlayDataModel> playDataModelList){
        mContext=context;
        this.playDataModelList=playDataModelList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return playDataModelList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.play_data_item, null);
            vh=new ViewHolder();
            vh.tv_postion=view.findViewById(R.id.tv_postion);
            vh.tv_title=view.findViewById(R.id.tv_title);
            vh.tv_income=view.findViewById(R.id.tv_income);
            vh.tv_play_amount=view.findViewById(R.id.tv_play_amount);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        vh.tv_postion.setText(String.valueOf(pos+1));
        PlayDataModel playDataModel = playDataModelList.get(pos);
        if(!TextUtils.isEmpty(playDataModel.getVideo_title())){
            vh.tv_title.setText(playDataModel.getVideo_title());
        }
        if(!TextUtils.isEmpty(playDataModel.getVideo_income())){
            vh.tv_income.setText(playDataModel.getVideo_income());
        }
        if(!TextUtils.isEmpty(playDataModel.getPlay_amount())){
            vh.tv_play_amount.setText(playDataModel.getPlay_amount());
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return playDataModelList.size();
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder{
        public TextView tv_postion;
        public TextView tv_title;
        public TextView tv_income;
        public TextView tv_play_amount;
    }
}
