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
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.PicassoRoundTransform;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ClassTwoAdapter extends BaseAdapter {

    private String TAG = ClassTwoAdapter.class.getSimpleName();
    private List<ClassModel> classModelList;
    private Context mContext;
    private int  selectItem=-1;

    public ClassTwoAdapter(Context context, List<ClassModel> classModelList) {
        mContext = context;
        this.classModelList = classModelList;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return classModelList.get(arg0);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.class_two_item, null);
            vh = new ViewHolder();
            vh.tv_class_name = view.findViewById(R.id.tv_class_name);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        ClassModel classModel = classModelList.get(pos);
        if (!TextUtils.isEmpty(classModel.getClass_name())) {
            vh.tv_class_name.setText(classModel.getClass_name());
        }

        if (pos == selectItem) {
            view.setBackground(mContext.getResources().getDrawable(R.drawable.blue_round_shape));
            vh.tv_class_name.setTextColor(UIUtils.getColor(R.color.text_color_blue));
        }
        else {
            view.setBackground(mContext.getResources().getDrawable(R.drawable.gray_round_shape));
            vh.tv_class_name.setTextColor(UIUtils.getColor(R.color.text_color_gray));
        }

        return view;
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return classModelList.size();
    }

    class ViewHolder {
        public TextView tv_class_name;
    }
}
