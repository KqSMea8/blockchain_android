package com.blockchain.shortvideoplayer.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.RecommendSearchActivity;
import com.blockchain.shortvideoplayer.activity.SelfMediaDetailActivity;
import com.blockchain.shortvideoplayer.adapter.SelfMediaSearchAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.SelfMediaBean;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.vodplayerview.activity.AliyunPlayerSkinActivity;
import com.blockchain.vodplayerview.constants.PlayParameter;
import com.blockchain.vodplayerview.widget.AliyunVodPlayerView;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscribeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscribeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscribeFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView lv_self_media;
    private List<SelfMediaBean> selfMediaBeanList;
    private SelfMediaSearchAdapter selfMediaSearchAdapter;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final int GETLISTSUCCESS = 0;
    private static final int GETLISTFAILED = 1;
    private static final String TAG = SubscribeFragment.class.getSimpleName();
    private String user_id;

    private Handler mHandller = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case GETLISTSUCCESS:  //获取列表成功
                    Response response = (Response) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"成功："+response.toString());
                    break;
                case GETLISTFAILED: //获取列表失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"失败："+e.getMessage());
                    break;
            }
        }
    };
    public SubscribeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubscribeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubscribeFragment newInstance(String param1, String param2) {
        SubscribeFragment fragment = new SubscribeFragment();
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
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);

        lv_self_media = view.findViewById(R.id.lv_self_media);

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

//        selfMediaBeanList = new ArrayList<>();
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaBeanList.add(new SelfMediaBean());
//        selfMediaSearchAdapter = new SelfMediaSearchAdapter(getActivity(),selfMediaBeanList);
//        lv_self_media.setAdapter(selfMediaSearchAdapter);

        getVideoList();

        lv_self_media.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SelfMediaDetailActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animnext_in,R.anim.animnext_out);
            }
        });
    }

    private void getVideoList() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
        netUtils.postDataAsynToNet(Constants.SubscribeSelfListUrl,reqBody ,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {

                Message message = new Message();
                message.what = GETLISTSUCCESS;
                message.obj = response;
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
