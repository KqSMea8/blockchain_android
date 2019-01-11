package com.blockchain.shortvideoplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.utils.UIUtils;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ClassRecyclerAdapter extends RecyclerView.Adapter<ClassRecyclerAdapter.MyViewHolder> {

    private String TAG = ClassRecyclerAdapter.class.getSimpleName();
    private List<ClassModel> classModelList;
    private Context mContext;
    private OnRecyclerItemClickListener mOnItemClickListener;//单击事件
    public ClassRecyclerAdapter(Context context, List<ClassModel> classModelList) {
        mContext = context;
        this.classModelList = classModelList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.class_recycler_item,parent,false);
        return new MyViewHolder(view);//将布局设置给holder
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.ll_all.getLayoutParams();
        layoutParams.width = UIUtils.dip2px(80);
        holder.ll_all.setLayoutParams(layoutParams);

        ClassModel classModel = classModelList.get(position);
        if (!TextUtils.isEmpty(classModel.getClass_name())) {
            holder.tv_name.setText(classModel.getClass_name());
        }

        //设置单击事件
        if(mOnItemClickListener !=null){
            holder.ll_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里是为textView设置了单击事件,回调出去
                    //mOnItemClickListener.onItemClick(v,position);这里需要获取布局中的position,不然乱序
                    mOnItemClickListener.onItemClick(v,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemCount() {
        return classModelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public LinearLayout ll_all;
        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            ll_all = view.findViewById(R.id.ll_all);
        }
    }

    /**
     * 添加并更新数据，同时具有动画效果
     */
    public void addDataAt(int position, List<ClassModel> data) {
        classModelList.addAll(position, data);
        notifyItemInserted(position);//更新数据集，注意如果用adapter.notifyDataSetChanged()将没有动画效果
    }
    /**
     * 移除并更新数据，同时具有动画效果
     */
    public void removeDataAt(int position) {
        classModelList.remove(position);
        notifyItemRemoved(position);
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
