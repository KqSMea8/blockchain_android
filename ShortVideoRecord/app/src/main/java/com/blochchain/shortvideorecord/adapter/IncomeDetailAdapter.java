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
import com.blochchain.shortvideorecord.model.IncomeDetailModel;
import com.blockchain.shortvideorecord.R;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class IncomeDetailAdapter extends BaseAdapter{

    private String TAG=IncomeDetailAdapter.class.getSimpleName();
    private List<IncomeDetailModel> incomeDetailModelList;
    private Context mContext;
    private int  selectItem=-1;
    public IncomeDetailAdapter(Context context, List<IncomeDetailModel> incomeDetailModelList){
        mContext=context;
        this.incomeDetailModelList=incomeDetailModelList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return incomeDetailModelList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.income_detail_item, null);
            vh=new ViewHolder();
            vh.tv_source=view.findViewById(R.id.tv_source);
            vh.tv_title=view.findViewById(R.id.tv_title);
            vh.tv_nickname=view.findViewById(R.id.tv_nickname);
            vh.tv_time=view.findViewById(R.id.tv_time);
            vh.tv_money=view.findViewById(R.id.tv_money);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        IncomeDetailModel incomeDetailModel = incomeDetailModelList.get(pos);
        switch (incomeDetailModel.getBay_source()){
            case 0:
                vh.tv_source.setText("购买vip");
                break;
            case 1:
                vh.tv_source.setText("购买视频：");
                break;
            case 2:
                vh.tv_source.setText("打赏");
                break;
        }

        if(!TextUtils.isEmpty(incomeDetailModel.getBay_money())){
            vh.tv_money.setText(incomeDetailModel.getBay_money());
        }
        if(!TextUtils.isEmpty(incomeDetailModel.getBay_time())){
            vh.tv_time.setText(incomeDetailModel.getBay_time());
        }
        if(!TextUtils.isEmpty(incomeDetailModel.getNickname())){
            vh.tv_nickname.setText(incomeDetailModel.getNickname());
        }
        if(!TextUtils.isEmpty(incomeDetailModel.getVideo_title())){
            vh.tv_title.setText(incomeDetailModel.getVideo_title());
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return incomeDetailModelList.size();
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder{
        public TextView tv_source;
        public TextView tv_title;
        public TextView tv_nickname;
        public TextView tv_time;
        public TextView tv_money;
    }
}
