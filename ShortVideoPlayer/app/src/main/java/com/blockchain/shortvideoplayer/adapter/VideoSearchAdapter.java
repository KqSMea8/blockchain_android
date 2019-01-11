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
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.MyImageView;
import com.blockchain.shortvideoplayer.widget.XCRoundRectImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class VideoSearchAdapter extends BaseAdapter {

    private String TAG=VideoSearchAdapter.class.getSimpleName();
    private List<VideoBean> videoBeanList;
    private Context mContext;
    public VideoSearchAdapter(Context context, List<VideoBean> videoBeanList){
        mContext=context;
        this.videoBeanList=videoBeanList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return videoBeanList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.video_search_item, null);
            vh=new ViewHolder();
            vh.iv_thumbnail=view.findViewById(R.id.iv_thumbnail);
            vh.tv_instroction=view.findViewById(R.id.tv_instroction);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        VideoBean videoBean = videoBeanList.get(pos);
        if(!TextUtils.isEmpty(videoBean.getVideo_pic())){
//            Picasso.with(mContext).load(videoBean.getVideo_pic()).into(vh.iv_thumbnail);
            Picasso.with(mContext)
                    .load(videoBean.getVideo_pic())
                    .resize(UIUtils.dip2px(179),UIUtils.dip2px(220))
                    .config(Bitmap.Config.RGB_565)
                    .centerCrop().into(vh.iv_thumbnail);
        }
        if(!TextUtils.isEmpty(videoBean.getVideo_dec())){
            vh.tv_instroction.setText(videoBean.getVideo_dec());
        }

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return videoBeanList.size();
    }

    class ViewHolder{
        public XCRoundRectImageView iv_thumbnail;
        public TextView tv_instroction;
    }
}
