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

import com.blochchain.shortvideorecord.model.FansPlaceModel;
import com.blochchain.shortvideorecord.model.WithdrawDetailModel;
import com.blockchain.shortvideorecord.R;

import java.util.List;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class WithdrawDetailAdapter extends BaseAdapter{

    private String TAG=WithdrawDetailAdapter.class.getSimpleName();
    private List<WithdrawDetailModel> withdrawDetailModelList;
    private Context mContext;
    private int  selectItem=-1;
    public WithdrawDetailAdapter(Context context, List<WithdrawDetailModel> withdrawDetailModelList){
        mContext=context;
        this.withdrawDetailModelList=withdrawDetailModelList;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return withdrawDetailModelList.get(arg0);
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
            view= LayoutInflater.from(mContext).inflate(R.layout.withdraw_detail_item, null);
            vh=new ViewHolder();
            vh.tv_bank=view.findViewById(R.id.tv_bank);
            vh.tv_money=view.findViewById(R.id.tv_money);
            vh.tv_time=view.findViewById(R.id.tv_time);
            vh.tv_state=view.findViewById(R.id.tv_state);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        WithdrawDetailModel withdrawDetailModel = withdrawDetailModelList.get(pos);
        String bank_card = withdrawDetailModel.getBank_card();
        if(!TextUtils.isEmpty(bank_card)){
            if(bank_card.length()>4){
                vh.tv_bank.setText("提现-"+bank_card.substring(0,4) + " ****** " + bank_card.substring(bank_card.length() - 4));
            }else {
                vh.tv_bank.setText("提现-"+bank_card);
            }
        }
        if(!TextUtils.isEmpty(withdrawDetailModel.getWithdraw_money())){
            vh.tv_money.setText(withdrawDetailModel.getWithdraw_money());
        }
        if(!TextUtils.isEmpty(withdrawDetailModel.getApply_time())){
            vh.tv_time.setText(withdrawDetailModel.getApply_time());
        }
        switch (withdrawDetailModel.getState()){//0=转账中,1=提现成功，-1=提现失败
            case 0:
                vh.tv_state.setText("转账中");
                break;
            case 1:
                vh.tv_state.setText("提现成功");
                break;
            case 2:
                vh.tv_state.setText("提现失败");
                break;
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return withdrawDetailModelList.size();
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    class ViewHolder{
        public TextView tv_bank;
        public TextView tv_money;
        public TextView tv_time;
        public TextView tv_state;
    }
}
