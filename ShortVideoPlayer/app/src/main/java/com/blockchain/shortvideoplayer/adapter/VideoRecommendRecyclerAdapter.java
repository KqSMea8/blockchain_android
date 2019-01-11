package com.blockchain.shortvideoplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.utils.ScreenUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class VideoRecommendRecyclerAdapter extends RecyclerView.Adapter<VideoRecommendRecyclerAdapter.MyViewHolder> {

    private String TAG=VideoRecommendRecyclerAdapter.class.getSimpleName();
    private List<VideoBean> videoBeanList;
    private Context mContext;
    private OnRecyclerItemClickListener mOnItemClickListener;//单击事件
    public VideoRecommendRecyclerAdapter(Context context, List<VideoBean> videoBeanList){
        mContext=context;
        this.videoBeanList=videoBeanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.video_recommend_recyvler_item,parent,false);
        return new MyViewHolder(view);//将布局设置给holder
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final double wid = ScreenUtils.getWidth(mContext)/2;
        final int targetWidth = (int) wid;
        VideoBean videoBean = videoBeanList.get(position);
        if(!TextUtils.isEmpty(videoBean.getVideo_pic())){
            holder.iv_thumbnail.setTag(videoBean.getVideo_pic());
            Picasso.with(mContext)
                    .load(videoBean.getVideo_pic())
//                    .resize((int) targetWidth,UIUtils.dip2px(220))
                    .resize(targetWidth,0)
                    .config(Bitmap.Config.RGB_565)
//                    .transform(transformation)
//                    .centerCrop()
                    .into(holder.iv_thumbnail);
        }
        if(!TextUtils.isEmpty(videoBean.getHead_pic())){
//            Picasso.with(mContext).load(videoBean.getHead_pic()).into(vh.iv_head);
            Picasso.with(mContext)
                    .load(videoBean.getHead_pic())
                    .resize(UIUtils.dip2px(18),UIUtils.dip2px(18))
                    .config(Bitmap.Config.RGB_565)
                    .centerCrop().into(holder.iv_head);
        }
        if(!TextUtils.isEmpty(videoBean.getVideo_dec())){
            holder.tv_instroction.setText(videoBean.getVideo_dec());
        }
        if(!TextUtils.isEmpty(videoBean.getNickname())){
            holder.tv_nickname.setText(videoBean.getNickname());
        }
        if(!TextUtils.isEmpty(videoBean.getPlay_amount())){
            holder.tv_number.setText(videoBean.getPlay_amount());
        }

        //设置单击事件
        if(mOnItemClickListener !=null){
            holder.rl_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里是为textView设置了单击事件,回调出去
                    //mOnItemClickListener.onItemClick(v,position);这里需要获取布局中的position,不然乱序
                    mOnItemClickListener.onItemClick(v,holder.getLayoutPosition());
                }
            });
        }
    }

//    @Override
//    public long getItemId(int arg0) {
//        // TODO Auto-generated method stub
//        return 0;
//    }

    @Override
    public int getItemCount() {
        return videoBeanList.size();
    }

    /**
     * 添加并更新数据，同时具有动画效果
     */
    public void addDataAt(int position, List<VideoBean> data) {
        videoBeanList.addAll(position, data);
        notifyItemInserted(position);//更新数据集，注意如果用adapter.notifyDataSetChanged()将没有动画效果
    }
    /**
     * 移除并更新数据，同时具有动画效果
     */
    public void removeDataAt(int position) {
        videoBeanList.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_thumbnail;
        public CircleImageView iv_head;
        public TextView tv_nickname;
        public ImageView iv_eye;
        public TextView tv_number;
        public TextView tv_instroction;
        public RelativeLayout rl_all;

        public MyViewHolder(View view) {
            super(view);
            iv_thumbnail=view.findViewById(R.id.iv_thumbnail);
            iv_head=view.findViewById(R.id.iv_head);
            tv_nickname=view.findViewById(R.id.tv_nickname);
            iv_eye=view.findViewById(R.id.iv_eye);
            tv_number=view.findViewById(R.id.tv_number);
            tv_instroction=view.findViewById(R.id.tv_instroction);
            rl_all=view.findViewById(R.id.rl_all);
        }
    }

    /**
     * 处理item的点击事件,因为recycler没有提供单击事件,所以只能自己写了
     */
    public interface OnRecyclerItemClickListener {
        public void onItemClick(View view, int position);
    }

    /**
     * 暴露给外面的设置单击事件
     */
    public void setOnItemClickListener(OnRecyclerItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
}
