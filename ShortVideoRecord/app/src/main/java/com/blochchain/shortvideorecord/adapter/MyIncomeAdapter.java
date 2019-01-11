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

import com.blochchain.shortvideorecord.model.MyIncomeModel;
import com.blochchain.shortvideorecord.model.PlayDataModel;
import com.blockchain.shortvideorecord.R;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class MyIncomeAdapter extends BaseAdapter{

    private String TAG=MyIncomeAdapter.class.getSimpleName();
    private List<MyIncomeModel> myIncomeModelList;
    private Context mContext;
    private int  selectItem=-1;
    public MyIncomeAdapter(Context context, List<MyIncomeModel> myIncomeModelList){
        mContext=context;
        this.myIncomeModelList=myIncomeModelList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return myIncomeModelList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.my_income_item, null);
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
        MyIncomeModel myIncomeModel = myIncomeModelList.get(pos);
        if(!TextUtils.isEmpty(myIncomeModel.getVideo_title())){
            vh.tv_title.setText(myIncomeModel.getVideo_title());
        }
        if(!TextUtils.isEmpty(myIncomeModel.getPlay_income())){
            vh.tv_income.setText(myIncomeModel.getPlay_income());
        }
        if(!TextUtils.isEmpty(myIncomeModel.getPlay_amount())){
            vh.tv_play_amount.setText(myIncomeModel.getPlay_amount());
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return myIncomeModelList.size();
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
