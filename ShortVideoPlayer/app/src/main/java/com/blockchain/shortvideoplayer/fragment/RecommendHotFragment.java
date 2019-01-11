package com.blockchain.shortvideoplayer.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.downloader.AliyunDownloadConfig;
import com.aliyun.vodplayer.downloader.AliyunDownloadInfoListener;
import com.aliyun.vodplayer.downloader.AliyunDownloadManager;
import com.aliyun.vodplayer.downloader.AliyunDownloadMediaInfo;
import com.aliyun.vodplayer.downloader.AliyunRefreshStsCallback;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.activity.VideoPlayActivity;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.CircleImageView;
import com.blockchain.vodplayerview.constants.PlayParameter;
import com.blockchain.vodplayerview.playlist.AlivcPlayListAdapter;
import com.blockchain.vodplayerview.playlist.AlivcPlayListManager;
import com.blockchain.vodplayerview.playlist.AlivcVideoInfo;
import com.blockchain.vodplayerview.utils.Commen;
import com.blockchain.vodplayerview.utils.VidStsUtil;
import com.blockchain.vodplayerview.utils.download.DownloadDBHelper;
import com.blockchain.vodplayerview.view.choice.AlivcShowMoreDialog;
import com.blockchain.vodplayerview.view.control.ControlView;
import com.blockchain.vodplayerview.view.download.AddDownloadView;
import com.blockchain.vodplayerview.view.download.AlivcDialog;
import com.blockchain.vodplayerview.view.download.AlivcDownloadMediaInfo;
import com.blockchain.vodplayerview.view.download.DownloadChoiceDialog;
import com.blockchain.vodplayerview.view.download.DownloadDataProvider;
import com.blockchain.vodplayerview.view.download.DownloadView;
import com.blockchain.vodplayerview.view.more.AliyunShowMoreValue;
import com.blockchain.vodplayerview.view.more.ShowMoreView;
import com.blockchain.vodplayerview.view.more.SpeedValue;
import com.blockchain.vodplayerview.view.tipsview.ErrorInfo;
import com.blockchain.vodplayerview.widget.AliyunScreenMode;
import com.blockchain.vodplayerview.widget.AliyunVodPlayerView;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecommendHotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendHotFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //        private static final String DEFAULT_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";
    private static final String DEFAULT_URL = "http://gslb.miaopai.com/stream/ed5HCfnhovu3tyIQAiv60Q__.mp4";
    private static final String DEFAULT_VID = "6e783360c811449d8692b2117acc9212";

    private ErrorInfo currentError = ErrorInfo.Normal;
    private int currentVideoPosition;
    private ArrayList<AlivcVideoInfo.Video> alivcVideoInfos;

    private DownloadView downloadView;
    private AliyunVodPlayerView mAliyunVodPlayerView = null;

    private DownloadDataProvider downloadDataProvider;
    private AliyunDownloadManager downloadManager;
    private AlivcPlayListAdapter alivcPlayListAdapter;
    private DownloadDBHelper dbHelper;
    private AliyunDownloadConfig config;
    private PlayerHandler playerHandler;
    private DownloadView dialogDownloadView;
    private AlivcShowMoreDialog showMoreDialog;

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");
    private List<String> logStrs = new ArrayList<>();

    private AliyunScreenMode currentScreenMode = AliyunScreenMode.Full;

    private boolean inRequest;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private boolean isStrangePhone() {
        boolean strangePhone = "mx5".equalsIgnoreCase(Build.DEVICE)
                || "Redmi Note2".equalsIgnoreCase(Build.DEVICE)
                || "Z00A_1".equalsIgnoreCase(Build.DEVICE)
                || "hwH60-L02".equalsIgnoreCase(Build.DEVICE)
                || "hermes".equalsIgnoreCase(Build.DEVICE)
                || ("V4".equalsIgnoreCase(Build.DEVICE) && "Meitu".equalsIgnoreCase(Build.MANUFACTURER))
                || ("m1metal".equalsIgnoreCase(Build.DEVICE) && "Meizu".equalsIgnoreCase(Build.MANUFACTURER));

        VcPlayerLog.e("lfj1115 ", " Build.Device = " + Build.DEVICE + " , isStrange = " + strangePhone);
        return strangePhone;
    }

    private CircleImageView iv_haed;
    private ImageView iv_love;
    private TextView tv_love_num;
    private ImageView iv_praise;
    private TextView tv_praise;
    private TextView tv_describe;
    private TextView tv_nickname;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private static final int GETLISTSUCCESS = 0;  //获取热门视频成功
    private static final int GETLISTFAILED = 1;   //获取热门视频失败
    private static final int ATTENTIONSUCCESS = 2;  //收藏视频成功
    private static final int ATTENTIONFAILED = 3; //收藏失败
    private static final int PRAISESUCCSS = 4; //点赞视频成功
    private static final int PRAISEFAILED = 5;  //点赞失败
    private String usr_id;
    private String video_id;
    private static final String TAG = RecommendHotFragment.class.getSimpleName();

    private Handler mHandller = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GETLISTSUCCESS:  //获取热门视频成功
                    String str = (String) msg.obj;
                    LogUtils.e(TAG + "成功：" + str);
                    break;
                case GETLISTFAILED: //获取热门视频失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    break;
                case ATTENTIONSUCCESS: //收藏视频成功
                    String string = (String) msg.obj;
                    LogUtils.e(TAG + "成功：" + string);
                    break;
                case ATTENTIONFAILED: //收藏失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e1.getMessage());
                    break;
                case PRAISESUCCSS: //点赞视频成功
                    String string1 = (String) msg.obj;
                    LogUtils.e(TAG + "成功：" + string1);
                    break;
                case PRAISEFAILED: //点赞失败
                    IOException e2 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e2.getMessage());
                    break;
            }
        }
    };

    public RecommendHotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommendHotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommendHotFragment newInstance(String param1, String param2) {
        RecommendHotFragment fragment = new RecommendHotFragment();
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
        View view = inflater.inflate(R.layout.fragment_recommend_hot, container, false);

        copyAssets();
        initAliyunPlayerView(view);

        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(getActivity(), true);

        setPlaySource();
        loadPlayList();
        return view;
    }

    @Override
    protected void loadData() {
//        loadPlayList();
//        getRecommendHotList();
    }

    private void initAliyunPlayerView(View view) {
        mAliyunVodPlayerView = (AliyunVodPlayerView) view.findViewById(com.blockchain.vodplayer.R.id.video_view);
        //保持屏幕敞亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        PlayParameter.PLAY_PARAM_URL = DEFAULT_URL;
        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
        mAliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
        //mAliyunVodPlayerView.setCirclePlay(true);
        mAliyunVodPlayerView.setAutoPlay(false);

        mAliyunVodPlayerView.setOnPreparedListener(new RecommendHotFragment.MyPrepareListener(this));
        mAliyunVodPlayerView.setNetConnectedListener(new RecommendHotFragment.MyNetConnectedListener(this));
        mAliyunVodPlayerView.setOnCompletionListener(new RecommendHotFragment.MyCompletionListener(this));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new RecommendHotFragment.MyFrameInfoListener(this));
        mAliyunVodPlayerView.setOnChangeQualityListener(new RecommendHotFragment.MyChangeQualityListener(this));
        mAliyunVodPlayerView.setOnStoppedListener(new RecommendHotFragment.MyStoppedListener(this));
        mAliyunVodPlayerView.setmOnPlayerViewClickListener(new RecommendHotFragment.MyPlayViewClickListener());
        mAliyunVodPlayerView.setOrientationChangeListener(new RecommendHotFragment.MyOrientationChangeListener(this));
        mAliyunVodPlayerView.setOnUrlTimeExpiredListener(new RecommendHotFragment.MyOnUrlTimeExpiredListener(this));
        mAliyunVodPlayerView.setOnShowMoreClickListener(new RecommendHotFragment.MyShowMoreClickLisener(this));
        mAliyunVodPlayerView.enableNativeLog();

        iv_haed = view.findViewById(R.id.iv_haed);
        iv_love = view.findViewById(R.id.iv_love);
        tv_love_num = view.findViewById(R.id.tv_love_num);
        iv_praise = view.findViewById(R.id.iv_praise);
        tv_praise = view.findViewById(R.id.tv_praise);
        tv_describe = view.findViewById(R.id.tv_describe);
        tv_nickname = view.findViewById(R.id.tv_nickname);

        iv_haed.setOnClickListener(this);
        iv_love.setOnClickListener(this);
        iv_praise.setOnClickListener(this);

    }

    private void copyAssets() {
        Commen.getInstance(getActivity().getApplicationContext()).copyAssetsToSD("encrypt", "aliyun").setFileOperateCallback(

                new Commen.FileOperateCallback() {
                    @Override
                    public void onSuccess() {
                        config = new AliyunDownloadConfig();
                        config.setSecretImagePath(
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/aliyun/encryptedApp.dat");
                        //        config.setDownloadPassword("123456789");
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save/");
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        config.setDownloadDir(file.getAbsolutePath());
                        //设置同时下载个数
                        config.setMaxNums(2);
                        // 获取AliyunDownloadManager对象
                        downloadManager = AliyunDownloadManager.getInstance(getActivity().getApplicationContext());
                        downloadManager.setDownloadConfig(config);

                        downloadDataProvider = DownloadDataProvider.getSingleton(getActivity().getApplicationContext());
                        // 更新sts回调
                        downloadManager.setRefreshStsCallback(new RecommendHotFragment.MyRefreshStsCallback());
                        // 视频下载的回调
//                        downloadManager.setDownloadInfoListener(new RecommendHotFragment.MyDownloadInfoListener(downloadView));
                        //
//                        downloadViewSetting(downloadView);
                        Log.e("Test", "assets copyt success");
                    }

                    @Override
                    public void onFailed(String error) {
                        Log.e("Test", "assets copyt error, msg:::::" + error);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_haed:
                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                UIUtils.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
                break;
            case R.id.iv_love:
//                iv_love.setImageResource(R.mipmap.love_selected);
                attentionVideo();
                break;
            case R.id.iv_praise:
//                iv_praise.setImageResource(R.mipmap.praise_selected);
                praiseVideo();
                break;
        }
    }

    /**
     * 收藏视频
     */
    private void attentionVideo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", usr_id);
        reqBody.put("video_id", video_id);
        netUtils.postDataAsynToNet(Constants.AttentionVideoUrl,reqBody, new NetUtils.MyNetCall() {

            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = ATTENTIONSUCCESS;
                message.obj = string;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = ATTENTIONFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    /**
     * 点赞视频
     */
    private void praiseVideo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", usr_id);
        reqBody.put("video_id", video_id);
        netUtils.postDataAsynToNet(Constants.PraisedVideoUrl,reqBody, new NetUtils.MyNetCall() {

            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = PRAISESUCCSS;
                message.obj = string;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = PRAISEFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    private static class MyRefreshStsCallback implements AliyunRefreshStsCallback {

        @Override
        public AliyunVidSts refreshSts(String vid, String quality, String format, String title, boolean encript) {
            VcPlayerLog.d("refreshSts ", "refreshSts , vid = " + vid);
            //NOTE: 注意：这个不能启动线程去请求。因为这个方法已经在线程中调用了。
            AliyunVidSts vidSts = VidStsUtil.getVidSts(vid);
            if (vidSts == null) {
                return null;
            } else {
                vidSts.setVid(vid);
                vidSts.setQuality(quality);
                vidSts.setTitle(title);
                return vidSts;
            }
        }
    }

    /**
     * 切换播放资源
     *
     * @param position 需要播放的数据在集合中的下标
     */
    private void changePlaySource(int position) {

        currentVideoPosition = position;

        AlivcVideoInfo.Video video = alivcVideoInfos.get(position);

        changePlayVidSource(video.getVideoId(), video.getTitle());
    }

    private class MyDownloadInfoListener implements AliyunDownloadInfoListener {

        private DownloadView downloadView;

        public MyDownloadInfoListener(DownloadView downloadView) {
            this.downloadView = downloadView;
        }

        @Override
        public void onPrepared(List<AliyunDownloadMediaInfo> infos) {
            Collections.sort(infos, new Comparator<AliyunDownloadMediaInfo>() {
                @Override
                public int compare(AliyunDownloadMediaInfo mediaInfo1, AliyunDownloadMediaInfo mediaInfo2) {
                    if (mediaInfo1.getSize() > mediaInfo2.getSize()) {
                        return 1;
                    }
                    if (mediaInfo1.getSize() < mediaInfo2.getSize()) {
                        return -1;
                    }

                    if (mediaInfo1.getSize() == mediaInfo2.getSize()) {
                        return 0;
                    }
                    return 0;
                }
            });
            onDownloadPrepared(infos);
        }

        @Override
        public void onStart(AliyunDownloadMediaInfo info) {
            Log.d("yds100", "onStart");
            Toast.makeText(getContext(), "start...download......", Toast.LENGTH_SHORT).show();
            //downloadView.addDownloadMediaInfo(info);
            //dbHelper.insert(info, DownloadDBHelper.DownloadState.STATE_DOWNLOADING);
            if (!downloadDataProvider.hasAdded(info)) {
//                updateDownloadTaskTip();
                downloadDataProvider.addDownloadMediaInfo(info);
            }

        }

        @Override
        public void onProgress(AliyunDownloadMediaInfo info, int percent) {
            downloadView.updateInfo(info);
            if (dialogDownloadView != null) {
                dialogDownloadView.updateInfo(info);
            }
            Log.e("Test", "download....progress....." + info.getProgress() + ",  " + percent);
            Log.d("yds100", "onProgress");
        }

        @Override
        public void onStop(AliyunDownloadMediaInfo info) {
            Log.d("yds100", "onStop");
            downloadView.updateInfo(info);
            if (dialogDownloadView != null) {
                dialogDownloadView.updateInfo(info);
            }
            //dbHelper.update(info, DownloadDBHelper.DownloadState.STATE_PAUSE);
        }

        @Override
        public void onCompletion(AliyunDownloadMediaInfo info) {
            Log.d("yds100", "onCompletion");
            downloadView.updateInfoByComplete(info);
            if (dialogDownloadView != null) {
                dialogDownloadView.updateInfoByComplete(info);
            }
            downloadDataProvider.addDownloadMediaInfo(info);
            //aliyunDownloadMediaInfoList.remove(info);
        }

        @Override
        public void onError(AliyunDownloadMediaInfo info, int code, String msg, String requestId) {
            Log.d("yds100", "onError" + msg);
            Log.e("Test", "download...onError...msg:::" + msg + ", requestId:::" + requestId + ", code:::" + code);
            downloadView.updateInfoByError(info);
            if (dialogDownloadView != null) {
                dialogDownloadView.updateInfoByError(info);
            }
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString(DOWNLOAD_ERROR_KEY, msg);
            message.setData(bundle);
            message.what = DOWNLOAD_ERROR;
            playerHandler = new PlayerHandler(RecommendHotFragment.this);
            playerHandler.sendMessage(message);
        }

        @Override
        public void onWait(AliyunDownloadMediaInfo outMediaInfo) {
            Log.d("yds100", "onWait");
        }

        @Override
        public void onM3u8IndexUpdate(AliyunDownloadMediaInfo outMediaInfo, int index) {
            Log.d("yds100", "onM3u8IndexUpdate");
        }
    }

    /**
     * 播放本地资源
     *
     * @param url
     * @param title
     */
    private void changePlayLocalSource(String url, String title) {
        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        alsb.setSource(url);
        alsb.setTitle(title);
        AliyunLocalSource localSource = alsb.build();
        mAliyunVodPlayerView.setLocalSource(localSource);
    }

    /**
     * 切换播放vid资源
     *
     * @param vid   切换视频的vid
     * @param title 切换视频的title
     */
    private void changePlayVidSource(String vid, String title) {
        AliyunVidSts vidSts = new AliyunVidSts();
        vidSts.setVid(vid);
        vidSts.setAcId(PlayParameter.PLAY_PARAM_AK_ID);
        vidSts.setAkSceret(PlayParameter.PLAY_PARAM_AK_SECRE);
        vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
        vidSts.setTitle(title);
        mAliyunVodPlayerView.setVidSts(vidSts);
        downloadManager.prepareDownloadMedia(vidSts);
    }


    private static class MyPrepareListener implements IAliyunVodPlayer.OnPreparedListener {

        private WeakReference<RecommendHotFragment> activityWeakReference;

        public MyPrepareListener(RecommendHotFragment skinActivity) {
            activityWeakReference = new WeakReference<RecommendHotFragment>(skinActivity);
        }

        @Override
        public void onPrepared() {
            RecommendHotFragment activity = activityWeakReference.get();
            if (activity != null) {
                activity.onPrepared();
            }
        }
    }

    private void onPrepared() {
//        logStrs.add(format.format(new Date()) + getString(com.blockchain.vodplayer.R.string.log_prepare_success));
//
//        for (String log : logStrs) {
//            tvLogs.append(log + "\n");
//        }
//        Toast.makeText(getActivity().getApplicationContext(), com.blockchain.vodplayer.R.string.toast_prepare_success,
//                Toast.LENGTH_SHORT).show();
    }

    private static class MyCompletionListener implements IAliyunVodPlayer.OnCompletionListener {

        private WeakReference<RecommendHotFragment> activityWeakReference;

        public MyCompletionListener(RecommendHotFragment skinActivity) {
            activityWeakReference = new WeakReference<RecommendHotFragment>(skinActivity);
        }

        @Override
        public void onCompletion() {

            RecommendHotFragment activity = activityWeakReference.get();
            if (activity != null) {
                activity.onCompletion();
            }
        }
    }

    private void onCompletion() {
//        logStrs.add(format.format(new Date()) + getString(com.blockchain.vodplayer.R.string.log_play_completion));
//        for (String log : logStrs) {
//            tvLogs.append(log + "\n");
//        }
//        Toast.makeText(getActivity().getApplicationContext(), com.blockchain.vodplayer.R.string.toast_play_compleion,
//                Toast.LENGTH_SHORT).show();

        // 当前视频播放结束, 播放下一个视频
        onNext();
    }

    private void onNext() {
        if (currentError == ErrorInfo.UnConnectInternet) {
            // 此处需要判断网络和播放类型
            // 网络资源, 播放完自动波下一个, 无网状态提示ErrorTipsView
            // 本地资源, 播放完需要重播, 显示Replay, 此处不需要处理
            if ("vidsts".equals(PlayParameter.PLAY_PARAM_TYPE)) {
                mAliyunVodPlayerView.showErrorTipView(4014, -1, "当前网络不可用");
            }
            return;
        }

        currentVideoPosition++;
        if (currentVideoPosition >= alivcVideoInfos.size() - 1) {
            //列表循环播放，如发现播放完成了从列表的第一个开始重新播放
            currentVideoPosition = 0;
        }

        AlivcVideoInfo.Video video = alivcVideoInfos.get(currentVideoPosition);
        if (video != null) {
            changePlayVidSource(video.getVideoId(), video.getTitle());
        }

    }

    private static class MyFrameInfoListener implements IAliyunVodPlayer.OnFirstFrameStartListener {

        private WeakReference<RecommendHotFragment> activityWeakReference;

        public MyFrameInfoListener(RecommendHotFragment skinActivity) {
            activityWeakReference = new WeakReference<RecommendHotFragment>(skinActivity);
        }

        @Override
        public void onFirstFrameStart() {

            RecommendHotFragment activity = activityWeakReference.get();
            if (activity != null) {
                activity.onFirstFrameStart();
            }
        }
    }

    private void onFirstFrameStart() {
        Map<String, String> debugInfo = mAliyunVodPlayerView.getAllDebugInfo();
        long createPts = 0;
        if (debugInfo.get("create_player") != null) {
            String time = debugInfo.get("create_player");
            createPts = (long) Double.parseDouble(time);
            logStrs.add(format.format(new Date(createPts)) + getString(com.blockchain.vodplayer.R.string.log_player_create_success));
        }
        if (debugInfo.get("open-url") != null) {
            String time = debugInfo.get("open-url");
            long openPts = (long) Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(openPts)) + getString(com.blockchain.vodplayer.R.string.log_open_url_success));
        }
        if (debugInfo.get("find-stream") != null) {
            String time = debugInfo.get("find-stream");
            long findPts = (long) Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(findPts)) + getString(com.blockchain.vodplayer.R.string.log_request_stream_success));
        }
        if (debugInfo.get("open-stream") != null) {
            String time = debugInfo.get("open-stream");
            long openPts = (long) Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(openPts)) + getString(com.blockchain.vodplayer.R.string.log_start_open_stream));
        }
        logStrs.add(format.format(new Date()) + getString(com.blockchain.vodplayer.R.string.log_first_frame_played));
//        for (String log : logStrs) {
//            tvLogs.append(log + "\n");
//        }
    }

    private class MyPlayViewClickListener implements AliyunVodPlayerView.OnPlayerViewClickListener {
        @Override
        public void onClick(AliyunScreenMode screenMode, AliyunVodPlayerView.PlayViewType viewType) {
            // 如果当前的Type是Download, 就显示Download对话框
            if (viewType == AliyunVodPlayerView.PlayViewType.Download) {
                showAddDownloadView(screenMode);
            }
        }
    }

    /**
     * 显示download 对话框
     *
     * @param screenMode
     */
    private void showAddDownloadView(AliyunScreenMode screenMode) {
        downloadDialog = new DownloadChoiceDialog(getActivity(), screenMode);
        final AddDownloadView contentView = new AddDownloadView(getActivity(), screenMode);
        contentView.onPrepared(aliyunDownloadMediaInfoList);
        contentView.setOnViewClickListener(viewClickListener);
        final View inflate = LayoutInflater.from(getActivity().getApplicationContext()).inflate(
                com.blockchain.vodplayer.R.layout.alivc_dialog_download_video, null, false);
        dialogDownloadView = inflate.findViewById(com.blockchain.vodplayer.R.id.download_view);
        downloadDialog.setContentView(contentView);
        downloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        downloadDialog.show();
        downloadDialog.setCanceledOnTouchOutside(true);

        if (screenMode == AliyunScreenMode.Full) {
            contentView.setOnShowVideoListLisener(new AddDownloadView.OnShowNativeVideoBtnClickListener() {
                @Override
                public void onShowVideo() {
                    downloadViewSetting(dialogDownloadView);
                    downloadDialog.setContentView(inflate);
                }
            });
        }
    }

    private Dialog downloadDialog = null;

    private AliyunDownloadMediaInfo aliyunDownloadMediaInfo;
    private long currentDownloadIndex = 0;
    /**
     * 开始下载的事件监听
     */
    private AddDownloadView.OnViewClickListener viewClickListener = new AddDownloadView.OnViewClickListener() {
        @Override
        public void onCancel() {
            if (downloadDialog != null) {
                downloadDialog.dismiss();
            }
        }

        @Override
        public void onDownload(AliyunDownloadMediaInfo info) {
            if (downloadDialog != null) {
                downloadDialog.dismiss();
            }

            aliyunDownloadMediaInfo = info;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                int permission = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE);

                } else {
                    addNewInfo(info);
                }
            } else {
                addNewInfo(info);
            }

        }
    };

    private void addNewInfo(AliyunDownloadMediaInfo info) {
        downloadManager.addDownloadMedia(info);
        downloadManager.startDownloadMedia(info);
        downloadView.addDownloadMediaInfo(info);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addNewInfo(aliyunDownloadMediaInfo);
            } else {
                // Permission Denied
                Toast.makeText(getActivity(), "没有sd卡读写权限, 无法下载", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    List<AliyunDownloadMediaInfo> aliyunDownloadMediaInfoList;

    private void onDownloadPrepared(List<AliyunDownloadMediaInfo> infos) {
        aliyunDownloadMediaInfoList = new ArrayList<>();
        aliyunDownloadMediaInfoList.addAll(infos);
    }

    private static class MyChangeQualityListener implements IAliyunVodPlayer.OnChangeQualityListener {

        private WeakReference<RecommendHotFragment> activityWeakReference;

        public MyChangeQualityListener(RecommendHotFragment skinActivity) {
            activityWeakReference = new WeakReference<RecommendHotFragment>(skinActivity);
        }

        @Override
        public void onChangeQualitySuccess(String finalQuality) {

            RecommendHotFragment activity = activityWeakReference.get();
            if (activity != null) {
                activity.onChangeQualitySuccess(finalQuality);
            }
        }

        @Override
        public void onChangeQualityFail(int code, String msg) {
            RecommendHotFragment activity = activityWeakReference.get();
            if (activity != null) {
                activity.onChangeQualityFail(code, msg);
            }
        }
    }

    private void onChangeQualitySuccess(String finalQuality) {
        logStrs.add(format.format(new Date()) + getString(com.blockchain.vodplayer.R.string.log_change_quality_success));
        Toast.makeText(getActivity().getApplicationContext(),
                getString(com.blockchain.vodplayer.R.string.log_change_quality_success), Toast.LENGTH_SHORT).show();
    }

    void onChangeQualityFail(int code, String msg) {
        logStrs.add(format.format(new Date()) + getString(com.blockchain.vodplayer.R.string.log_change_quality_fail) + " : " + msg);
        Toast.makeText(getActivity().getApplicationContext(),
                getString(com.blockchain.vodplayer.R.string.log_change_quality_fail), Toast.LENGTH_SHORT).show();
    }

    private static class MyStoppedListener implements IAliyunVodPlayer.OnStoppedListener {

        private WeakReference<RecommendHotFragment> activityWeakReference;

        public MyStoppedListener(RecommendHotFragment skinActivity) {
            activityWeakReference = new WeakReference<RecommendHotFragment>(skinActivity);
        }

        @Override
        public void onStopped() {

            RecommendHotFragment activity = activityWeakReference.get();
            if (activity != null) {
                activity.onStopped();
            }
        }
    }

    private void onStopped() {
        Toast.makeText(getActivity().getApplicationContext(), com.blockchain.vodplayer.R.string.log_play_stopped,
                Toast.LENGTH_SHORT).show();
    }

    private void setPlaySource() {
        if ("localSource".equals(PlayParameter.PLAY_PARAM_TYPE)) {
            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            alsb.setSource(PlayParameter.PLAY_PARAM_URL);
            Uri uri = Uri.parse(PlayParameter.PLAY_PARAM_URL);
            if ("rtmp".equals(uri.getScheme())) {
                alsb.setTitle("");
            }
            AliyunLocalSource localSource = alsb.build();
            mAliyunVodPlayerView.setLocalSource(localSource);

        } else if ("vidsts".equals(PlayParameter.PLAY_PARAM_TYPE)) {
            if (!inRequest) {
                AliyunVidSts vidSts = new AliyunVidSts();
                vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
                vidSts.setAcId(PlayParameter.PLAY_PARAM_AK_ID);
                vidSts.setAkSceret(PlayParameter.PLAY_PARAM_AK_SECRE);
                vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setVidSts(vidSts);
                }
//                downloadManager.prepareDownloadMedia(vidSts);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updatePlayerViewMode();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onStop();
        }

        if (downloadManager != null && downloadDataProvider != null) {
            downloadManager.stopDownloadMedias(downloadDataProvider.getAllDownloadMediaInfo());
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }


//    private void updateDownloadTaskTip() {
//        if (currentTab != TAB_DOWNLOAD_LIST) {
//
//            Drawable drawable = getResources().getDrawable(com.blockchain.vodplayer.R.drawable.alivc_download_new_task);
//            drawable.setBounds(0, 0, 20, 20);
//            tvTabDownloadVideo.setCompoundDrawablePadding(-20);
//            tvTabDownloadVideo.setCompoundDrawables(null, null,  drawable, null);
//        } else {
//            tvTabDownloadVideo.setCompoundDrawables(null, null, null, null);
//        }
//    }

    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //转为竖屏了。
                //显示状态栏
                //                if (!isStrangePhone()) {
                //                    getSupportActionBar().show();
                //                }

                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                //设置view的布局，宽高之类
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
//                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(getContext()) * 9.0f / 16);
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //                if (!isStrangePhone()) {
                //                    aliVcVideoViewLayoutParams.topMargin = getSupportActionBar().getHeight();
                //                }

            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }

                //设置view的布局，宽高
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //                if (!isStrangePhone()) {
                //                    aliVcVideoViewLayoutParams.topMargin = 0;
                //                }
            }

        }
    }

    @Override
    public void onDestroy() {
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }

        if (playerHandler != null) {
            playerHandler.removeMessages(DOWNLOAD_ERROR);
            playerHandler = null;
        }
        super.onDestroy();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (mAliyunVodPlayerView != null) {
//            boolean handler = mAliyunVodPlayerView.onKeyDown(keyCode, event);
//            if (!handler) {
//                return false;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        //解决某些手机上锁屏之后会出现标题栏的问题。
//        updatePlayerViewMode();
//    }

    private static final int DOWNLOAD_ERROR = 1;
    private static final String DOWNLOAD_ERROR_KEY = "error_key";

    private static class PlayerHandler extends Handler {
        //持有弱引用RecommendHotFragment,GC回收时会被回收掉.
        private final WeakReference<RecommendHotFragment> mActivty;

        public PlayerHandler(RecommendHotFragment activity) {
            mActivty = new WeakReference<RecommendHotFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecommendHotFragment activity = mActivty.get();
            super.handleMessage(msg);
            if (activity != null) {
                switch (msg.what) {
                    case DOWNLOAD_ERROR:
                        Toast.makeText(mActivty.get().getActivity(), msg.getData().getString(DOWNLOAD_ERROR_KEY), Toast.LENGTH_LONG)
                                .show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static class MyStsListener implements VidStsUtil.OnStsResultListener {

        private WeakReference<RecommendHotFragment> weakctivity;

        public MyStsListener(RecommendHotFragment act) {
            weakctivity = new WeakReference<RecommendHotFragment>(act);
        }

        @Override
        public void onSuccess(String vid, String akid, String akSecret, String token) {
            RecommendHotFragment activity = weakctivity.get();
            if (activity != null) {
                activity.onStsSuccess(vid, akid, akSecret, token);
            }
        }

        @Override
        public void onFail() {
            RecommendHotFragment activity = weakctivity.get();
            if (activity != null) {
                activity.onStsFail();
            }
        }
    }

    private void onStsFail() {

        Toast.makeText(getActivity().getApplicationContext(), com.blockchain.vodplayer.R.string.request_vidsts_fail, Toast.LENGTH_LONG).show();
        inRequest = false;
        //finish();
    }

    private void onStsSuccess(String mVid, String akid, String akSecret, String token) {

        PlayParameter.PLAY_PARAM_VID = mVid;
        PlayParameter.PLAY_PARAM_AK_ID = akid;
        PlayParameter.PLAY_PARAM_AK_SECRE = akSecret;
        PlayParameter.PLAY_PARAM_SCU_TOKEN = token;

        inRequest = false;
        // 请求sts成功后, 加载播放资源,和视频列表
        setPlaySource();
        if (alivcVideoInfos != null) {
            alivcVideoInfos.clear();
        }
        loadPlayList();
    }

    /**
     * 获取播放列表数据
     */
    private void loadPlayList() {

        AlivcPlayListManager.getInstance().fetchPlayList(PlayParameter.PLAY_PARAM_AK_ID,
                PlayParameter.PLAY_PARAM_AK_SECRE,
                PlayParameter.PLAY_PARAM_SCU_TOKEN, new AlivcPlayListManager.PlayListListener() {
                    @Override
                    public void onPlayList(int code, final ArrayList<AlivcVideoInfo.Video> videos) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alivcVideoInfos.addAll(videos);
                                alivcPlayListAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                });
    }

    private void getRecommendHotList() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", usr_id);
        netUtils.postDataAsynToNet(Constants.GetRecommendHotVideoListUrl ,reqBody, new NetUtils.MyNetCall() {

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

    private static class MyOrientationChangeListener implements AliyunVodPlayerView.OnOrientationChangeListener {

        private final WeakReference<RecommendHotFragment> weakReference;

        public MyOrientationChangeListener(RecommendHotFragment activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void orientationChange(boolean from, AliyunScreenMode currentMode) {
            RecommendHotFragment activity = weakReference.get();
            activity.hideDownloadDialog(from, currentMode);
            activity.hideShowMoreDialog(from, currentMode);
        }
    }

    private void hideShowMoreDialog(boolean from, AliyunScreenMode currentMode) {
        if (showMoreDialog != null) {
            if (currentMode == AliyunScreenMode.Small) {
                showMoreDialog.dismiss();
                currentScreenMode = currentMode;
            }
        }
    }

    private void hideDownloadDialog(boolean from, AliyunScreenMode currentMode) {

        if (downloadDialog != null) {
            if (currentScreenMode != currentMode) {
                downloadDialog.dismiss();
                currentScreenMode = currentMode;
            }
        }
    }

    /**
     * 判断是否有网络的监听
     */
    private class MyNetConnectedListener implements AliyunVodPlayerView.NetConnectedListener {
        WeakReference<RecommendHotFragment> weakReference;

        public MyNetConnectedListener(RecommendHotFragment activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            RecommendHotFragment activity = weakReference.get();
            activity.onReNetConnected(isReconnect);
        }

        @Override
        public void onNetUnConnected() {
            RecommendHotFragment activity = weakReference.get();
            activity.onNetUnConnected();
        }
    }

    private void onNetUnConnected() {
        currentError = ErrorInfo.UnConnectInternet;
        if (aliyunDownloadMediaInfoList != null && aliyunDownloadMediaInfoList.size() > 0) {
            downloadManager.stopDownloadMedias(aliyunDownloadMediaInfoList);
        }
    }

    private void onReNetConnected(boolean isReconnect) {
        if (isReconnect) {
            if (aliyunDownloadMediaInfoList != null && aliyunDownloadMediaInfoList.size() > 0) {
                int unCompleteDownload = 0;
                for (AliyunDownloadMediaInfo info : aliyunDownloadMediaInfoList) {
                    //downloadManager.startDownloadMedia(info);
                    if (info.getStatus() == AliyunDownloadMediaInfo.Status.Stop) {

                        unCompleteDownload++;
                    }
                }

                if (unCompleteDownload > 0) {
                    Toast.makeText(getActivity(), "网络恢复, 请手动开启下载任务...", Toast.LENGTH_SHORT).show();
                }
            }
            VidStsUtil.getVidSts(PlayParameter.PLAY_PARAM_VID, new RecommendHotFragment.MyStsListener(this));
        }
    }

    private static class MyOnUrlTimeExpiredListener implements IAliyunVodPlayer.OnUrlTimeExpiredListener {
        WeakReference<RecommendHotFragment> weakReference;

        public MyOnUrlTimeExpiredListener(RecommendHotFragment activity) {
            weakReference = new WeakReference<RecommendHotFragment>(activity);
        }

        @Override
        public void onUrlTimeExpired(String s, String s1) {
            RecommendHotFragment activity = weakReference.get();
            activity.onUrlTimeExpired(s, s1);
        }
    }

    private void onUrlTimeExpired(String oldVid, String oldQuality) {
        //requestVidSts();
        AliyunVidSts vidSts = VidStsUtil.getVidSts(oldVid);
        PlayParameter.PLAY_PARAM_VID = vidSts.getVid();
        PlayParameter.PLAY_PARAM_AK_SECRE = vidSts.getAkSceret();
        PlayParameter.PLAY_PARAM_AK_ID = vidSts.getAcId();
        PlayParameter.PLAY_PARAM_SCU_TOKEN = vidSts.getSecurityToken();

    }

    private static class MyShowMoreClickLisener implements ControlView.OnShowMoreClickListener {
        WeakReference<RecommendHotFragment> weakReference;

        MyShowMoreClickLisener(RecommendHotFragment activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void showMore() {
            RecommendHotFragment activity = weakReference.get();
            activity.showMore(activity);
        }
    }

    private void showMore(final RecommendHotFragment activity) {
        showMoreDialog = new AlivcShowMoreDialog(getActivity());
        AliyunShowMoreValue moreValue = new AliyunShowMoreValue();
        moreValue.setSpeed(mAliyunVodPlayerView.getCurrentSpeed());
        moreValue.setVolume(mAliyunVodPlayerView.getCurrentVolume());
        moreValue.setScreenBrightness(mAliyunVodPlayerView.getCurrentScreenBrigtness());

        ShowMoreView showMoreView = new ShowMoreView(getActivity(), moreValue);
        showMoreDialog.setContentView(showMoreView);
        showMoreDialog.show();
        showMoreView.setOnDownloadButtonClickListener(new ShowMoreView.OnDownloadButtonClickListener() {
            @Override
            public void onDownloadClick() {
                // 点击下载
                showMoreDialog.dismiss();
                if ("localSource".equals(PlayParameter.PLAY_PARAM_TYPE)) {
                    Toast.makeText(getContext(), "Url类型不支持下载", Toast.LENGTH_SHORT).show();
                    return;
                }
                showAddDownloadView(AliyunScreenMode.Full);
            }
        });

        showMoreView.setOnScreenCastButtonClickListener(new ShowMoreView.OnScreenCastButtonClickListener() {
            @Override
            public void onScreenCastClick() {
                Toast.makeText(getContext(), "功能开发中, 敬请期待...", Toast.LENGTH_SHORT).show();
            }
        });

        showMoreView.setOnBarrageButtonClickListener(new ShowMoreView.OnBarrageButtonClickListener() {
            @Override
            public void onBarrageClick() {
                Toast.makeText(getContext(), "功能开发中, 敬请期待...", Toast.LENGTH_SHORT).show();
            }
        });

        showMoreView.setOnSpeedCheckedChangedListener(new ShowMoreView.OnSpeedCheckedChangedListener() {
            @Override
            public void onSpeedChanged(RadioGroup group, int checkedId) {
                // 点击速度切换
                if (checkedId == com.blockchain.vodplayer.R.id.rb_speed_normal) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.One);
                } else if (checkedId == com.blockchain.vodplayer.R.id.rb_speed_onequartern) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.OneQuartern);
                } else if (checkedId == com.blockchain.vodplayer.R.id.rb_speed_onehalf) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.OneHalf);
                } else if (checkedId == com.blockchain.vodplayer.R.id.rb_speed_twice) {
                    mAliyunVodPlayerView.changeSpeed(SpeedValue.Twice);
                }

            }
        });

        // 亮度seek
        showMoreView.setOnLightSeekChangeListener(new ShowMoreView.OnLightSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                mAliyunVodPlayerView.setCurrentScreenBrigtness(progress);
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });

        showMoreView.setOnVoiceSeekChangeListener(new ShowMoreView.OnVoiceSeekChangeListener() {
            @Override
            public void onStart(SeekBar seekBar) {

            }

            @Override
            public void onProgress(SeekBar seekBar, int progress, boolean fromUser) {
                mAliyunVodPlayerView.setCurrentVolume(progress);
            }

            @Override
            public void onStop(SeekBar seekBar) {

            }
        });

    }

    /**
     * downloadView的配置 里面配置了需要下载的视频的信息, 事件监听等 抽取该方法的主要目的是, 横屏下download dialog的离线视频列表中也用到了downloadView, 而两者显示内容和数据是同步的,
     * 所以在此进行抽取 AliyunPlayerSkinActivity.class#showAddDownloadView(DownloadVie view)中使用
     *
     * @param downloadView
     */
    private void downloadViewSetting(final DownloadView downloadView) {
        downloadView.addAllDownloadMediaInfo(downloadDataProvider.getAllDownloadMediaInfo());

        downloadView.setOnDownloadViewListener(new DownloadView.OnDownloadViewListener() {
            @Override
            public void onStop(AliyunDownloadMediaInfo downloadMediaInfo) {
                downloadManager.stopDownloadMedia(downloadMediaInfo);
            }

            @Override
            public void onStart(AliyunDownloadMediaInfo downloadMediaInfo) {
                downloadManager.startDownloadMedia(downloadMediaInfo);
            }

            @Override
            public void onDeleteDownloadInfo(final ArrayList<AlivcDownloadMediaInfo> alivcDownloadMediaInfos) {
                // 视频删除的dialog
                final AlivcDialog alivcDialog = new AlivcDialog(getContext());
                alivcDialog.setDialogIcon(com.blockchain.vodplayer.R.drawable.icon_delete_tips);
                alivcDialog.setMessage(getResources().getString(com.blockchain.vodplayer.R.string.alivc_delete_confirm));
                alivcDialog.setOnConfirmclickListener(getResources().getString(com.blockchain.vodplayer.R.string.alivc_dialog_sure),
                        new AlivcDialog.onConfirmClickListener() {
                            @Override
                            public void onConfirm() {
                                alivcDialog.dismiss();
                                if (alivcDownloadMediaInfos != null && alivcDownloadMediaInfos.size() > 0) {
                                    downloadView.deleteDownloadInfo();
                                    if (dialogDownloadView != null) {

                                        dialogDownloadView.deleteDownloadInfo();
                                    }
                                    downloadDataProvider.deleteAllDownloadInfo(alivcDownloadMediaInfos);
                                } else {
                                    Toast.makeText(getContext(), "没有删除的视频选项...", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        });
                alivcDialog.setOnCancelOnclickListener(getResources().getString(com.blockchain.vodplayer.R.string.alivc_dialog_cancle),
                        new AlivcDialog.onCancelOnclickListener() {
                            @Override
                            public void onCancel() {
                                alivcDialog.dismiss();
                            }
                        });
                alivcDialog.show();
            }
        });
    }

}
