package com.blockchain.shortvideoplayer.adapter;

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

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.UsrBuyRecordBean;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class UsrBuyRecordAdapter extends BaseAdapter{

    private String TAG="DictionaryAdapter";
    private List<UsrBuyRecordBean> usrBuyRecordBeanList;
    private Context mContext;
    private int  selectItem=-1;
    public UsrBuyRecordAdapter(Context context, List<UsrBuyRecordBean> usrBuyRecordBeanList){
        mContext=context;
        this.usrBuyRecordBeanList=usrBuyRecordBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return usrBuyRecordBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.usr_buy_record_item, null);
            vh=new ViewHolder();
            vh.tv_source=(TextView)view.findViewById(R.id.tv_source);
            vh.tv_time=(TextView)view.findViewById(R.id.tv_time);
            vh.tv_money=(TextView)view.findViewById(R.id.tv_money);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        UsrBuyRecordBean usrBuyRecordBean = usrBuyRecordBeanList.get(pos);
        switch (usrBuyRecordBean.getBay_source()){
            case 0:
                vh.tv_source.setText("购买vip会员");
                break;
            case 1:
                vh.tv_source.setText("购买视频");
                break;
            case 2:
                vh.tv_source.setText("打赏");
                break;
        }
        if(!TextUtils.isEmpty(usrBuyRecordBean.getBay_time())){
            vh.tv_time.setText(usrBuyRecordBean.getBay_time());
        }
        if(!TextUtils.isEmpty(usrBuyRecordBean.getBay_money())){
            vh.tv_money.setText(usrBuyRecordBean.getBay_money());
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return usrBuyRecordBeanList.size();
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder{
        public TextView tv_source;
        public TextView tv_time;
        public TextView tv_money;
    }
}
