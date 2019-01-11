package com.blockchain.shortvideoplayer.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.AccoountManagerActivity;
import com.blockchain.shortvideoplayer.activity.BuyVIPActivity;
import com.blockchain.shortvideoplayer.activity.HelpCenterActivity;
import com.blockchain.shortvideoplayer.activity.MessageActivity;
import com.blockchain.shortvideoplayer.activity.MyInfromationActivity;
import com.blockchain.shortvideoplayer.activity.RecommendSearchActivity;
import com.blockchain.shortvideoplayer.activity.SubscribeActivity;
import com.blockchain.shortvideoplayer.activity.SystemSettingActivity;
import com.blockchain.shortvideoplayer.adapter.VideoSearchAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView iv_back;
    private ImageView iv_message; //消息
    private ImageView iv_head; //头像
    private LinearLayout ll_setting; //设置
    private TextView tv_nickname;  //昵称
    private ImageView iv_sex;  //性别
    private TextView tv_level; //等级
    private TextView tv_describe; //自我介绍
    private TextView tv_follow;  //关注
    private LinearLayout ll_subscribe;  //订阅类目
    private LinearLayout ll_account;  //账号管理
    private LinearLayout ll_help;  //帮助中心
    private LinearLayout ll_bought_title; //已购买标签
    private TextView tv_bought;
    private ImageView iv_bought;
    private LinearLayout ll_record_title; //观看记录标签
    private TextView tv_record;
    private ImageView iv_record;
    private LinearLayout ll_collection_title; //收藏标签
    private TextView tv_collection;
    private ImageView iv_collection;
    private GridView gv_video;
    private List<VideoBean> videoBeanList;
    private VideoSearchAdapter videoSearchAdapter;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final int GETUSERINFOCESS = 0;
    private static final int GETUSERINFOFAILED = 1;
    private static final String TAG = MineFragment.class.getSimpleName();
    private String user_id;

    private Handler mHandller = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case GETUSERINFOCESS:  //获取用户信息成功
                    Response response = (Response) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"成功："+response.toString());
                    break;
                case GETUSERINFOFAILED: //获取用户信息失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"失败："+e.getMessage());
                    break;
            }
        }
    };
    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        iv_back = view.findViewById(R.id.iv_back);
        iv_message = view.findViewById(R.id.iv_message);
        iv_head = view.findViewById(R.id.iv_head);
        ll_setting = view.findViewById(R.id.ll_setting);
        tv_nickname = view.findViewById(R.id.tv_nickname);
        iv_sex = view.findViewById(R.id.iv_sex);
        tv_level = view.findViewById(R.id.tv_level);
        tv_describe = view.findViewById(R.id.tv_describe);
        tv_follow = view.findViewById(R.id.tv_follow);
        ll_subscribe = view.findViewById(R.id.ll_subscribe);
        ll_account = view.findViewById(R.id.ll_account);
        ll_help = view.findViewById(R.id.ll_help);
        ll_bought_title = view.findViewById(R.id.ll_bought_title);
        tv_bought = view.findViewById(R.id.tv_bought);
        iv_bought = view.findViewById(R.id.iv_bought);
        ll_record_title = view.findViewById(R.id.ll_record_title);
        tv_record = view.findViewById(R.id.tv_record);
        iv_record = view.findViewById(R.id.iv_record);
        ll_collection_title = view.findViewById(R.id.ll_collection_title);
        tv_collection = view.findViewById(R.id.tv_collection);
        iv_collection = view.findViewById(R.id.iv_collection);
        gv_video = view.findViewById(R.id.gv_video);

        iv_back.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_subscribe.setOnClickListener(this);
        ll_account.setOnClickListener(this);
        ll_help.setOnClickListener(this);
        ll_bought_title.setOnClickListener(this);
        ll_record_title.setOnClickListener(this);
        ll_collection_title.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        tv_nickname.setOnClickListener(this);
        tv_describe.setOnClickListener(this);
        iv_sex.setOnClickListener(this);
        tv_level.setOnClickListener(this);

//        initData();
        return view;
    }

    @Override
    protected void loadData() {
        initData();
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(getActivity(), true);

//        videoBeanList = new ArrayList<>();
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoBeanList.add(new VideoBean());
//        videoSearchAdapter = new VideoSearchAdapter(getActivity(), videoBeanList);
//        gv_video.setAdapter(videoSearchAdapter);

        getUserInfo();
    }

    private void getUserInfo() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
        netUtils.postDataAsynToNet(Constants.GetUserInfoUrl,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {

                Message message = new Message();
                message.what = GETUSERINFOCESS;
                message.obj = response;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETUSERINFOFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:  //返回
                break;
            case R.id.iv_message:  //消息
                intent = new Intent(getActivity(), MessageActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in, R.anim.animnext_out);
                break;
            case R.id.ll_setting: //设置
                intent = new Intent(getActivity(), SystemSettingActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in, R.anim.animnext_out);
                break;
            case R.id.ll_subscribe: //订阅类目
                intent = new Intent(getActivity(), SubscribeActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in, R.anim.animnext_out);
                break;
            case R.id.ll_account: //账号管理
                intent = new Intent(getActivity(), AccoountManagerActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in, R.anim.animnext_out);
                break;
            case R.id.ll_help: //帮助中心
                intent = new Intent(getActivity(), HelpCenterActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in, R.anim.animnext_out);
                break;
            case R.id.ll_bought_title: //已购买
                changeTitle(0);
                break;
            case R.id.ll_record_title: //观看记录
                changeTitle(1);
                break;
            case R.id.ll_collection_title: //
                changeTitle(2);
                break;
            case R.id.iv_head:
            case R.id.tv_nickname:
            case R.id.iv_sex:
            case R.id.tv_describe:
                intent = new Intent(getActivity(), MyInfromationActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in, R.anim.animnext_out);
                break;
            case R.id.tv_level:  //vip
                intent = new Intent(getActivity(), BuyVIPActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in, R.anim.animnext_out);
                break;
        }

    }

    private void changeTitle(int i) {
        switch (i) {
            case 0:
                tv_bought.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                tv_record.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                tv_collection.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                iv_bought.setImageResource(R.mipmap.bottom_blue);
                iv_record.setImageResource(R.mipmap.bottom_white);
                iv_collection.setImageResource(R.mipmap.bottom_white);
                break;
            case 1:
                tv_bought.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                tv_record.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                tv_collection.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                iv_bought.setImageResource(R.mipmap.bottom_white);
                iv_record.setImageResource(R.mipmap.bottom_blue);
                iv_collection.setImageResource(R.mipmap.bottom_white);
                break;
            case 2:
                tv_bought.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                tv_record.setTextColor(UIUtils.getColor(R.color.text_color_gray));
                tv_collection.setTextColor(UIUtils.getColor(R.color.text_color_blue));
                iv_bought.setImageResource(R.mipmap.bottom_white);
                iv_record.setImageResource(R.mipmap.bottom_white);
                iv_collection.setImageResource(R.mipmap.bottom_blue);
                break;
        }
    }
}
