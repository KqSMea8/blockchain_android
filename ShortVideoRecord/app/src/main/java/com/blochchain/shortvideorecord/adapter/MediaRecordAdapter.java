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

import com.blochchain.shortvideorecord.model.MediaRecordBean;
import com.blockchain.shortvideorecord.R;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class MediaRecordAdapter extends BaseAdapter{

    private String TAG=MediaRecordAdapter.class.getSimpleName();
    private List<MediaRecordBean> mediaRecordBeanList;
    private Context mContext;
    private int  selectItem=-1;
    public MediaRecordAdapter(Context context, List<MediaRecordBean> mediaRecordBeanList){
        mContext=context;
        this.mediaRecordBeanList=mediaRecordBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return mediaRecordBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.media_record_item, null);
            vh=new ViewHolder();
            vh.tv_nickname=view.findViewById(R.id.tv_nickname);
            vh.tv_last_time=view.findViewById(R.id.tv_last_time);
            vh.tv_last_record=view.findViewById(R.id.tv_last_record);
            vh.tv_number=view.findViewById(R.id.tv_number);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        MediaRecordBean mediaRecordBean = mediaRecordBeanList.get(pos);
        if(!TextUtils.isEmpty(mediaRecordBean.getNickname())){
            vh.tv_nickname.setText(mediaRecordBean.getNickname());
        }
        if(!TextUtils.isEmpty(mediaRecordBean.getLast_time())){
            vh.tv_last_time.setText(mediaRecordBean.getLast_time());
        }
        if(!TextUtils.isEmpty(mediaRecordBean.getLast_record())){
            vh.tv_last_record.setText(mediaRecordBean.getLast_record());
        }
        if(mediaRecordBean.getRecord_list() != null ){
            vh.tv_number.setText(mediaRecordBean.getRecord_list().size()+"条");
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mediaRecordBeanList.size();
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder{
        public TextView tv_nickname;
        public TextView tv_last_time;
        public TextView tv_last_record;
        public TextView tv_number;
    }
}
