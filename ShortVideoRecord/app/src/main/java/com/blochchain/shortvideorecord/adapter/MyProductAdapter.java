package com.blochchain.shortvideorecord.adapter;

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

import com.blochchain.shortvideorecord.model.FansPlaceModel;
import com.blochchain.shortvideorecord.model.MyProductModel;
import com.blockchain.shortvideorecord.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class MyProductAdapter extends BaseAdapter {

    private String TAG = MyProductAdapter.class.getSimpleName();
    private List<MyProductModel> myProductModelList;
    private Context mContext;
    private int selectItem = -1;

    public MyProductAdapter(Context context, List<MyProductModel> myProductModelList) {
        mContext = context;
        this.myProductModelList = myProductModelList;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return myProductModelList.get(arg0);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.my_product_item, null);
            vh = new ViewHolder();
            vh.iv_pic = view.findViewById(R.id.iv_pic);
            vh.tv_desc = view.findViewById(R.id.tv_desc);
            vh.tv_play_amount = view.findViewById(R.id.tv_play_amount);
            vh.tv_time = view.findViewById(R.id.tv_time);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        MyProductModel myProductModel = myProductModelList.get(pos);
        if(!TextUtils.isEmpty(myProductModel.getVideo_pic())){
            Picasso.with(mContext).load(myProductModel.getVideo_pic())
                    .into(vh.iv_pic);
        }
        if (!TextUtils.isEmpty(myProductModel.getPlay_amount())) {
            vh.tv_play_amount.setText(myProductModel.getPlay_amount());
        }
        String time = myProductModel.getRelease_time();
        if (!TextUtils.isEmpty(time)) {
            if (time.length() > 9)
                vh.tv_time.setText(time.substring(0, 10));
        }
        if (!TextUtils.isEmpty(myProductModel.getVideo_title())) {
            vh.tv_desc.setText(myProductModel.getVideo_title());
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return myProductModelList.size();
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder {
        public ImageView iv_pic;
        public TextView tv_desc;
        public TextView tv_play_amount;
        public TextView tv_time;
    }
}
