package com.blockchain.shortvideoplayer.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.SearchBean;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class SearchCommenAdapter extends BaseAdapter {

    private String TAG=SearchCommenAdapter.class.getSimpleName();
    private List<SearchBean> searchBeanList;
    private Context mContext;
    public SearchCommenAdapter(Context context, List<SearchBean> searchBeanList){
        mContext=context;
        this.searchBeanList=searchBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return searchBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.search_commen_item, null);
            vh=new ViewHolder();
            vh.tv_string=view.findViewById(R.id.tv_string);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        if(!TextUtils.isEmpty(searchBeanList.get(pos).getSearch_name())){
            vh.tv_string.setText(searchBeanList.get(pos).getSearch_name());
        }

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return searchBeanList.size();
    }

    class ViewHolder{
        public TextView tv_string;
    }
}
