package com.blochchain.shortvideorecord.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blochchain.shortvideorecord.model.DictionaryBean;
import com.blockchain.shortvideorecord.R;

import java.util.HashMap;
import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class DictionaryAdapter extends BaseAdapter{

    private String TAG=DictionaryAdapter.class.getSimpleName();
    private List<DictionaryBean> dictionaryBeans;
    private Context mContext;
    private int  selectItem=-1;
    public DictionaryAdapter(Context context, List<DictionaryBean> dictionaryBeans){
        mContext=context;
        this.dictionaryBeans=dictionaryBeans;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return dictionaryBeans.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.dictonary_item, null);
            vh=new ViewHolder();
            vh.tv_name=(TextView)view.findViewById(R.id.tv_name);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }
        vh.tv_name.setText(""+dictionaryBeans.get(pos).getDictionary_value_name());

        if (pos == selectItem) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.text_color_blue));
        }
        else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dictionaryBeans.size();
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder{
        public TextView tv_name;
    }
}
