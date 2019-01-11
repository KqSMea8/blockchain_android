package com.blockchain.shortvideoplayer.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.VideoPlayActivity;
import com.blockchain.shortvideoplayer.adapter.VideoRecommendAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.GetRecommendVideoListResponse;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecommendRecFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendRecFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GridView gv_videos;
    private VideoRecommendAdapter videoRecommendAdapter;
    private List<VideoBean> videoBeanList;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private String user_id;
    private int screenWidth;
    private static final int GETLISTSUCCESS = 0;
    private static final int GETLISTFAILED = 1;
    private static final String TAG = RecommendRecFragment.class.getSimpleName();

    private Handler mHandller = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GETLISTSUCCESS:  //获取列表成功
                    loadingDialog.dismiss();
                        String str = (String) msg.obj;
                        LogUtils.e(TAG + "成功：" + str);
                        GetRecommendVideoListResponse getRecommendVideoListResponse = gson.fromJson(str, GetRecommendVideoListResponse.class);
                        if (getRecommendVideoListResponse != null) {
                            videoBeanList = getRecommendVideoListResponse.getData();
                            if (videoBeanList != null) {
                                videoRecommendAdapter = new VideoRecommendAdapter(getActivity(), videoBeanList,screenWidth);
                                gv_videos.setAdapter(videoRecommendAdapter);
                            }
                        }
                    break;
                case GETLISTFAILED: //获取列表失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    break;
            }
        }
    };

    public RecommendRecFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommendRecFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommendRecFragment newInstance(String param1, String param2) {
        RecommendRecFragment fragment = new RecommendRecFragment();
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
        View view = inflater.inflate(R.layout.fragment_recommend_rec, container, false);

        gv_videos = view.findViewById(R.id.gv_videos);

        screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
//        initData();
        return view;
    }

    @Override
    protected void loadData() {
        LogUtils.e("333333333333333333333333333");
//        initData();
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(getActivity(), true);

        videoBeanList = new ArrayList<>();
        VideoBean videoBean = new VideoBean();
        videoBean.setVideo_dec("12312341jijij");
        videoBean.setNickname("张三");
        videoBean.setPlay_amount("11w");
        videoBeanList.add(videoBean);
        VideoBean videoBean1 = new VideoBean();
        videoBean1.setVideo_dec("12312341jijij111");
        videoBean1.setNickname("张三1");
        videoBean1.setPlay_amount("12w");
        videoBeanList.add(videoBean1);
        VideoBean videoBean2 = new VideoBean();
        videoBean2.setVideo_dec("12312341jijij222");
        videoBean2.setNickname("张三2");
        videoBean2.setPlay_amount("112w");
        videoBeanList.add(videoBean2);
        VideoBean videoBean3 = new VideoBean();
        videoBean3.setVideo_dec("12312341jijij3322");
        videoBean3.setNickname("张三3");
        videoBean3.setPlay_amount("13w");
        videoBeanList.add(videoBean3);
        VideoBean videoBean4 = new VideoBean();
        videoBean4.setVideo_dec("12312341j4422");
        videoBean4.setNickname("张三4");
        videoBean4.setPlay_amount("14w");
        videoBeanList.add(videoBean4);

        videoRecommendAdapter = new VideoRecommendAdapter(getActivity(),videoBeanList,screenWidth);
        gv_videos.setAdapter(videoRecommendAdapter);

//        getRecommendList();

        gv_videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
            }
        });
    }

    private void getRecommendList() {
        String url = Constants.GetRecommendVideoListUrl;
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(user_id)){
            reqBody.put("usrs_id", user_id);
        }
        netUtils.postDataAsynToNet(url,reqBody, new NetUtils.MyNetCall() {

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETLISTSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETLISTFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

}
