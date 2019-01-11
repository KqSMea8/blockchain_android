package com.blochchain.shortvideorecord.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blochchain.shortvideorecord.model.ClassFirstModel;
import com.blochchain.shortvideorecord.model.ClassItemModel;
import com.blochchain.shortvideorecord.widget.XCRoundRectImageView;
import com.blockchain.shortvideorecord.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class ClassFirstAdapter extends BaseAdapter {

    private String TAG = ClassFirstAdapter.class.getSimpleName();
    private List<ClassItemModel> classItemModelList;
    private Context mContext;

    public ClassFirstAdapter(Context context, List<ClassItemModel> classItemModelList) {
        mContext = context;
        this.classItemModelList = classItemModelList;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return classItemModelList.get(arg0);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.class_first_item, null);
            vh = new ViewHolder();
            vh.tv_first_name = view.findViewById(R.id.tv_first_name);
            vh.tv_second_name = view.findViewById(R.id.tv_second_name);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        ClassItemModel classItemModel = classItemModelList.get(pos);

        if (!TextUtils.isEmpty(classItemModel.getFirst_class_name())) {
            vh.tv_first_name.setText(classItemModel.getFirst_class_name());
        }
        if(!TextUtils.isEmpty(classItemModel.getSecond_class_name())){
            vh.tv_second_name.setText(classItemModel.getSecond_class_name());
        }

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return classItemModelList.size();
    }

    class ViewHolder {
        public TextView tv_first_name;
        public TextView tv_second_name;
    }
}
