package com.blochchain.shortvideorecord.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blochchain.shortvideorecord.model.MessageBean;
import com.blochchain.shortvideorecord.utils.DateUtils;
import com.blochchain.shortvideorecord.widget.CircleImageView;
import com.blockchain.shortvideorecord.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class Messagedapter extends BaseAdapter {

    private String TAG = Messagedapter.class.getSimpleName();
    private List<MessageBean> messageBeanList;
    private Context mContext;
    private String otherHeadPic;
    private String myHeadPic;

    public Messagedapter(Context context, List<MessageBean> messageBeanList, String otherHeadPic, String myHeadPic) {
        mContext = context;
        this.messageBeanList = messageBeanList;
        this.otherHeadPic = otherHeadPic;
        this.myHeadPic = myHeadPic;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return messageBeanList.get(arg0);
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
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            vh = new ViewHolder();
            vh.tv_time = view.findViewById(R.id.tv_time);
            vh.ll_mine = view.findViewById(R.id.ll_mine);
            vh.my_content = view.findViewById(R.id.my_content);
            vh.iv_my_head = view.findViewById(R.id.iv_my_head);
            vh.ll_other = view.findViewById(R.id.ll_other);
            vh.tv_other_content = view.findViewById(R.id.tv_other_content);
            vh.iv_other_head = view.findViewById(R.id.iv_other_head);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        MessageBean messageBean = messageBeanList.get(pos);
        if(pos > 0){
            MessageBean messageBean1 = messageBeanList.get(pos-1);
            String cTimeStr = messageBean.getMsg_time();
            String lastTimeStr = messageBean1.getMsg_time();
            if(!TextUtils.isEmpty(cTimeStr)){
                long time1 = DateUtils.simpleDate(cTimeStr);
                long time2 = DateUtils.simpleDate(lastTimeStr);
                long time =Math.abs(time1 - time2);
                if(time <300000){
                    vh.tv_time.setVisibility(View.GONE);
                }else{
                    vh.tv_time.setVisibility(View.VISIBLE);
                }
            }
        }else {
            vh.tv_time.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(messageBean.getMsg_time())){
            vh.tv_time.setText(messageBean.getMsg_time());
        }
        if(!TextUtils.isEmpty(otherHeadPic)){
            Picasso.with(mContext).load(otherHeadPic).into(vh.iv_other_head);
        }
        if(!TextUtils.isEmpty(myHeadPic)){
            Picasso.with(mContext).load(myHeadPic).into(vh.iv_my_head);
        }
        switch (messageBean.getMsg_type()){
            case 1:
                vh.ll_mine.setVisibility(View.VISIBLE);
                vh.ll_other.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(messageBean.getMsg_content())){
                    vh.my_content.setText(messageBean.getMsg_content());
                }
                break;
            case 0:
            case 2:
            case 3:
                vh.ll_mine.setVisibility(View.GONE);
                vh.ll_other.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(messageBean.getMsg_content())){
                    vh.tv_other_content.setText(messageBean.getMsg_content());
                }
                break;
        }

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messageBeanList.size();
    }

    class ViewHolder {
        public TextView tv_time;
        public LinearLayout ll_mine;
        public TextView my_content;
        public CircleImageView iv_my_head;
        public LinearLayout ll_other;
        public TextView tv_other_content;
        public CircleImageView iv_other_head;
    }
}
