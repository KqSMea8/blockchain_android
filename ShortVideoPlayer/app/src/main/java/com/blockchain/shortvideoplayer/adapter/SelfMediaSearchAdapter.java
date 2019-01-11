package com.blockchain.shortvideoplayer.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.SelfMediaBean;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.CircleImageView;
import com.blockchain.shortvideoplayer.widget.XCRoundRectImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class SelfMediaSearchAdapter extends BaseAdapter {

    private String TAG=SelfMediaSearchAdapter.class.getSimpleName();
    private List<SelfMediaBean> selfMediaBeanList;
    private Context mContext;
    public SelfMediaSearchAdapter(Context context, List<SelfMediaBean> selfMediaBeanList){
        mContext=context;
        this.selfMediaBeanList=selfMediaBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return selfMediaBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.self_media_search_item, null);
            vh=new ViewHolder();
            vh.iv_head=view.findViewById(R.id.iv_head);
            vh.tv_nickname=view.findViewById(R.id.tv_nickname);
            vh.tv_describe=view.findViewById(R.id.tv_describe);
            vh.tv_amoount=view.findViewById(R.id.tv_amoount);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        SelfMediaBean selfMediaBean = selfMediaBeanList.get(pos);
        if(!TextUtils.isEmpty(selfMediaBean.getHead_pic())){
//            Picasso.with(mContext).load(selfMediaBean.getHead_pic()).error(R.mipmap.head).into(vh.iv_head);
            Picasso.with(mContext)
                    .load(selfMediaBean.getHead_pic())
                    .resize(UIUtils.dip2px(40),UIUtils.dip2px(40))
                    .config(Bitmap.Config.RGB_565)
                    .centerCrop().into(vh.iv_head);
        }
        if(!TextUtils.isEmpty(selfMediaBean.getNickname())){
            vh.tv_nickname.setText(selfMediaBean.getNickname());
        }
        if(!TextUtils.isEmpty(selfMediaBean.getSelf_intr())){
            vh.tv_describe.setText(selfMediaBean.getSelf_intr());
        }
        if(!TextUtils.isEmpty(selfMediaBean.getFans_amount())){
            vh.tv_amoount.setText("粉丝："+selfMediaBean.getFans_amount());
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return selfMediaBeanList.size();
    }

    class ViewHolder{
        public CircleImageView iv_head;
        public TextView tv_nickname;
        public TextView tv_describe;
        public TextView tv_amoount;
    }
}
