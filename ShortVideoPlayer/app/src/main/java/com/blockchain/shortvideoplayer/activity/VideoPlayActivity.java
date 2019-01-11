package com.blockchain.shortvideoplayer.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
import com.blockchain.shortvideoplayer.MutiApplication;
import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.ClassFirstModel;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.model.PayInfoModel;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetPayInfoResponse;
import com.blockchain.shortvideoplayer.response.GetStsTokenResponse;
import com.blockchain.shortvideoplayer.response.GetVideoInfoResponse;
import com.blockchain.shortvideoplayer.response.IsPraiseAttentionResponse;
import com.blockchain.shortvideoplayer.utils.DateUtils;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.SystemBarTintManager;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.CircleImageView;
import com.blockchain.shortvideoplayer.widget.SolveClickTouchConflictLayout;
import com.blockchain.shortvideoplayer.wxapi.WeChatPayCallback;
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
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
 * 播放器和播放列表界面 Created by Mulberry on 2018/4/9.
 */
public class VideoPlayActivity extends BaseActivity implements OnClickListener,WeChatPayCallback {
//    private static final String DEFAULT_URL = "http://gslb.miaopai.com/stream/ed5HCfnhovu3tyIQAiv60Q__.mp4";
    private static final String DEFAULT_URL = "";
    private static final String DEFAULT_VID = "a34647538a1a4c9f9a0cc1c9b01747c4";

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

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final String TAG = VideoPlayActivity.class.getSimpleName();
    private static final int GETINFOSUCCESS = 0;  //获取视频详情成功
    private static final int GETINFOFAILED = 1;   //获取视频详情失败
    private static final int ATTENTIONSUCCESS = 2;  //收藏视频成功
    private static final int ATTENTIONFAILED = 3; //收藏失败
    private static final int PRAISESUCCSS = 4; //点赞视频成功
    private static final int PRAISEFAILED = 5;  //点赞失败
    private static final int GETSTSSUCESS = 6; //获取sts成功
    private static final int GETSTSFAILED = 7;  //获取sts失败
    private static final int ISPRAISEATTENTIONSUCCESS = 10; //是否点赞和收藏
    private static final int ISPRAISEATTENTIONFAILED = 11;  //是否点赞和收藏
    private static final int GETBUYINFOSUCCESS = 12; //获取微信支付参数
    private static final int GETBYINFOFAILED = 13;
    private static final int SUBSCRIBECLASSSUCESS = 18; //订阅类目成功
    private static final int SUBSCRIBECLASSFAILED = 19;  //订阅类目失败
    private String usr_id ;
    private String video_id;

    private CircleImageView iv_haed;  //头像
    private ImageView iv_love;   //收藏
    private TextView tv_love_num;  //收藏数
    private ImageView iv_praise;   //点赞
    private TextView tv_praise;  //点赞数
    private TextView tv_describe;  //简介
    private TextView tv_nickname;  //昵称
    private VideoBean videoBean;
    private AlertDialog buyDialog;
    private int type ; //没有登录的时候  1 当天看3个以下可以播放，   2 看完3个需要登录登录
    private String videoListStr;
    private List<VideoBean> videoBeanList;
    private int postion;
    private SolveClickTouchConflictLayout rl_total;
    private ImageView iv_video;
    private boolean isAttentioned; //是否收藏
    private boolean isPraised;  //是否点赞

    private boolean isAttentionOrPraise;  //点赞和收藏后重新拉取数据不设置播放信息只更新点赞数和收藏数

    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private ImageView iv_back;
    private String v_id;
    private int fromSource;
    public static final int GOTOBUYVIP = 101;
    public static final int GOTOLOGIN = 102;
    private List<ClassFirstModel> classFirstModelList;
    private List<ClassModel> classModelList;
    private AlertDialog recommendDialog;  //推荐类目对话框
    private String selectClassIds = "";

    private MyHandler mHandller = new MyHandler(this){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case GETINFOSUCCESS:  //获取详情成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取详情成功："+str);
                    GetVideoInfoResponse getVideoInfoResponse = gson.fromJson(str,GetVideoInfoResponse.class);
                    if(getVideoInfoResponse != null){
                        if(getVideoInfoResponse.getCode() ==0 ){
                            videoBean = getVideoInfoResponse.getData();
                            if(videoBean != null){
                                if(TextUtils.isEmpty(usr_id)){
                                    setPlayMessage();
                                    if(type == 1){
                                        if(isAttentionOrPraise){
                                            isAttentionOrPraise = false;
                                            setVideoShowMessage();
                                        }else {
                                            setVideoShowMessage();
//                                            setPlayInfoMessage();
                                            getStsToken();
                                        }
                                    }else {
                                        isAttentionOrPraise = false;
                                        UIUtils.showToastCenter(VideoPlayActivity.this,"您的今天的观看次数已用完，请登录后继续观看");
                                        Intent intent = new Intent(VideoPlayActivity.this, LoginActivity.class);
                                        UIUtils.startActivityForResult(intent,GOTOLOGIN);
                                        return;
                                    }
                                }else {
                                    if(isAttentionOrPraise){
                                        isAttentionOrPraise = false;
                                        setVideoShowMessage();
                                    }else {
                                        setVideoShowMessage();
//                                        setPlayInfoMessage();
                                        getStsToken();
                                    }
                                }
                            }
                        }else if(getVideoInfoResponse.getCode() == -2){
                            isAttentionOrPraise = false;
                            buyDialog = DialogUtils.showBuyDialog(VideoPlayActivity.this,VideoPlayActivity.this);
                            return;
                        }else {
                            isAttentionOrPraise = false;
//                            UIUtils.showToastCenter(VideoPlayActivity.this,getVideoInfoResponse.getMsg());
                        }
                    }
                    break;
                case GETINFOFAILED: //获取详情失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取详情失败："+e.getMessage());
//                    UIUtils.showToastCenter(VideoPlayActivity.this,e.getMessage());
                    break;
                case ATTENTIONSUCCESS: //收藏视频成功
                    String string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"收藏视频成功："+string);
                    BaseResponse baseResponse = gson.fromJson(string,BaseResponse.class);
                    if(baseResponse != null){
                        if(baseResponse.getCode() == 0){
                            if(isAttentioned){
                                isAttentioned = false;
                                iv_love.setImageResource(R.mipmap.love_unselected);
                            }else {
                                isAttentioned = true;
                                iv_love.setImageResource(R.mipmap.love_selected);
                            }
                            isAttentionOrPraise  = true;
                            getInfo();
                        }else {
//                            UIUtils.showToastCenter(VideoPlayActivity.this,baseResponse.getMsg());
                        }
                    }
                    break;
                case ATTENTIONFAILED: //收藏失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"收藏视频失败："+e1.getMessage());
//                    UIUtils.showToastCenter(VideoPlayActivity.this,e1.getMessage());
                    break;
                case PRAISESUCCSS: //点赞视频成功
                    String string1 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"点赞视频成功："+string1);
                    BaseResponse baseResponse1 = gson.fromJson(string1,BaseResponse.class);
                    if(baseResponse1 != null){
                        if(baseResponse1.getCode() == 0){
                            if(isPraised){
                                isPraised = false;
                                iv_praise.setImageResource(R.mipmap.praise_unselected);
                            }else {
                                isPraised = true;
                                iv_praise.setImageResource(R.mipmap.praise_selected);

                            }
                            isAttentionOrPraise  = true;
                            getInfo();
                        }else {
//                            UIUtils.showToastCenter(VideoPlayActivity.this,baseResponse1.getMsg());
                        }
                    }
                    break;
                case PRAISEFAILED: //点赞失败
                    IOException e2 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"点赞视频失败："+e2.getMessage());
//                    UIUtils.showToastCenter(VideoPlayActivity.this,e2.getMessage());
                    break;
                case ISPRAISEATTENTIONSUCCESS:  //是否点赞和收藏
                    String string3 = (String) msg.obj;
                    LogUtils.e(TAG+"是否点赞和收藏："+string3);
                    loadingDialog.dismiss();
                    IsPraiseAttentionResponse isPraiseAttentionResponse = gson.fromJson(string3,IsPraiseAttentionResponse.class);
                    if(isPraiseAttentionResponse != null){
                        if(isPraiseAttentionResponse.getCode() == 0){
                            if(isPraiseAttentionResponse.getIs_attention() == 1){
                                isAttentioned = true;
                                iv_love.setImageResource(R.mipmap.love_selected);
                            }else {
                                isAttentioned = false;
                                iv_love.setImageResource(R.mipmap.love_unselected);
                            }
                            if(isPraiseAttentionResponse.getIs_praised() == 1){
                                isPraised = true;
                                iv_praise.setImageResource(R.mipmap.praise_selected);
                            }else {
                                isPraised = false;
                                iv_praise.setImageResource(R.mipmap.praise_unselected);
                            }
                        }else {
//                            UIUtils.showToastCenter(VideoPlayActivity.this,isPraiseAttentionResponse.getMsg());
                        }
                    }
                    break;
                case ISPRAISEATTENTIONFAILED:  //是否点赞和收藏
                    IOException e5 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "是否收藏和点赞失败：" + e5.getMessage());
//                    UIUtils.showToastCenter(VideoPlayActivity.this,e5.getMessage());
                    break;
                case GETSTSSUCESS: //获取sts成功
                    String string4 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取sts成功："+string4);
                    GetStsTokenResponse getStsTokenResponse = gson.fromJson(string4,GetStsTokenResponse.class);
                    if(getStsTokenResponse != null){
                        if(getStsTokenResponse.getCode() ==0 ){
                            accessKeyId = getStsTokenResponse.getAccessKeyId();
                            accessKeySecret = getStsTokenResponse.getAccessKeySecret();
                            securityToken = getStsTokenResponse.getSecurityToken();
                            setPlayInfoMessage();
                        }else {
                            LogUtils.e("获取sts失败:"+getStsTokenResponse.getMsg());
                        }
                    }
                    break;
                case GETSTSFAILED: //获取sts失败
                    IOException e6 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "获取sts失败：" + e6.getMessage());
                    break;
                case GETBUYINFOSUCCESS:  //获取支付信息成功
                    String str3 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取支付信息成功："+str3);
                    GetPayInfoResponse getPayInfoResponse = gson.fromJson(str3,GetPayInfoResponse.class);
                    if(getPayInfoResponse != null ){
                        if(getPayInfoResponse.getCode() == 0){
                            PayInfoModel payInfoModel = getPayInfoResponse.getData();
                            if(payInfoModel != null){
                                weixinPay(payInfoModel);
                            }
                        }else {
                            UIUtils.showToastCenter(VideoPlayActivity.this,getPayInfoResponse.getMsg());
                        }
                    }
                    break;
                case GETBYINFOFAILED: //获取支付信息失败
                    IOException e3 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取支付信息失败："+e3.getMessage());
                    UIUtils.showToastCenter(VideoPlayActivity.this,e3.getMessage());
                    break;
                case SUBSCRIBECLASSSUCESS:  //订阅该类目
                    String string9 = (String) msg.obj;
                    LogUtils.e(TAG+"订阅："+string9);
                    BaseResponse baseResponse5 = gson.fromJson(string9,BaseResponse.class);
                    if(baseResponse5 != null){
                        if(baseResponse5.getCode() == 0){
                            recommendDialog.dismiss();
                            SharedPrefrenceUtils.setInt(VideoPlayActivity.this,"is_first",0);
                            getInfo();
                        }else {
                            UIUtils.showToastCenter(VideoPlayActivity.this,baseResponse5.getMsg());
                        }
                    }
                    break;
                case SUBSCRIBECLASSFAILED: //订阅该类目
                    IOException e9 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"订阅失败:"+e9.getMessage());
                    UIUtils.showToastCenter(VideoPlayActivity.this,e9.getMessage());
                    break;
            }
        }
    };


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

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_video_play_layout, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        rl_total = findViewById(R.id.rl_total);
        iv_video = findViewById(R.id.iv_video);
        iv_haed = findViewById(R.id.iv_haed);
        iv_love = findViewById(R.id.iv_love);
        tv_love_num = findViewById(R.id.tv_love_num);
        iv_praise = findViewById(R.id.iv_praise);
        tv_praise = findViewById(R.id.tv_praise);
        tv_describe = findViewById(R.id.tv_describe);
        tv_nickname = findViewById(R.id.tv_nickname);

        iv_haed.setOnClickListener(this);
        iv_love.setOnClickListener(this);
        iv_praise.setOnClickListener(this);

        setStatusBar1(R.mipmap.title_background);
//        setStatusBar1(R.color.transparent);

        usr_id = SharedPrefrenceUtils.getString(this,"usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);

        Intent intent = getIntent();
        video_id = intent.getStringExtra("video_id");
        postion = intent.getIntExtra("postion",0);
        videoListStr= intent.getStringExtra("videoListStr");
        fromSource = intent.getIntExtra("fromSource",0);
        if(!TextUtils.isEmpty(videoListStr)){
            videoBeanList = gson.fromJson(videoListStr,new TypeToken<List<VideoBean>>(){}.getType());
        }

//        copyAssets();
        initAliyunPlayerView(rootView);

//        setPlaySource();


//        loadPlayList();
        getInfo();
        if(!TextUtils.isEmpty(usr_id)){
            isAttentionAndPraise();
        }
        return rootView;
    }

    private void weixinPay(PayInfoModel payInfoModel) {
        MutiApplication.getApplication().weChatPayCallback=this;
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.WeiXinAppId,false);;
        PayReq req;
        req = new PayReq();
        req.appId = payInfoModel.getAppid();//APPID
        req.partnerId = payInfoModel.getPartnerid();//    商户号
        req.prepayId = payInfoModel.getPrepayid();//  预付款ID
        req.nonceStr = payInfoModel.getNoncestr();//随机数
        req.timeStamp = payInfoModel.getTimestamp();//时间戳
        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
        req.sign = payInfoModel.getSign();//签名
        api.registerApp(Constants.WeiXinAppId);
        api.sendReq(req);
    }

    /**
     * 设置上下滑动作监听器
     * @author jczmdeveloper
     */
    private void setGestureListener(){

        rl_total.setmSetOnSlideListener(new SolveClickTouchConflictLayout.OnSlideListener() {
            @Override
            public void onRightToLeftSlide() {

            }

            @Override
            public void onLeftToRightSlide() {

            }

            @Override
            public void onUpToDownSlide() {
                if(videoBeanList != null && videoBeanList.size()>0){
                    postion --;
                    mAliyunVodPlayerView.pause();
                    iv_video.setVisibility(View.VISIBLE);
                    if(postion <0){
                        postion = 0;
                        UIUtils.showToastCenter(VideoPlayActivity.this,"已经是第一个视频");
                    }else {
                        getVideoIdByPostion();
                    }
                }
            }

            @Override
            public void onDownToUpSlide() {
                if(videoBeanList != null && videoBeanList.size()>0){
                    postion ++;
                    mAliyunVodPlayerView.pause();
                    iv_video.setVisibility(View.VISIBLE);
                    if(postion>= videoBeanList.size()){
                        postion = videoBeanList.size() -1;
                        UIUtils.showToastCenter(VideoPlayActivity.this,"已经是最后一个视频");
                    }else {
                        getVideoIdByPostion();
                    }
                }
            }
        });
        mAliyunVodPlayerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAliyunVodPlayerView.isPlaying()){
                    mAliyunVodPlayerView.pause();
                    iv_video.setVisibility(View.VISIBLE);
                }
            }
        });

        iv_video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAliyunVodPlayerView.start();
                iv_video.setVisibility(View.GONE);
            }
        });
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getVideoIdByPostion() {
        if(videoBeanList != null && videoBeanList.size()>0){
            if(postion>=0 && postion< videoBeanList.size()){
                video_id = videoBeanList.get(postion).getVideo_id();
                if(!TextUtils.isEmpty(video_id)){
                    getInfo();
                    if(!TextUtils.isEmpty(usr_id)){
                        isAttentionAndPraise();
                    }
                }
            }
        }
    }

    /**
     * 查看视频是否被收藏和点赞
     */
    private void isAttentionAndPraise() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(usr_id)){
            reqBody.put("usrs_id",usr_id);
        }
        reqBody.put("video_id",video_id);
        LogUtils.e(TAG+"是否收藏和点赞参数:"+reqBody.toString());
        loadingDialog.show();
        netUtils.postDataAsynToNet(Constants.IsPraiseAttentionUrl,reqBody, new NetUtils.MyNetCall() {

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = ISPRAISEATTENTIONSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = ISPRAISEATTENTIONFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    /**
     * 设置播放信息
     * 1、第一次下载未登录情况下可以免费看3个视频点击第四个视频跳转到登录页面，必须登录后才能继续观看
     * 2、	用户第一次打开app能看3个视频，看完三个后提示注册，注册成功后每天可免费观看30个，看完提示需要单个购买或者购买VIP。
     * 3、	当用户累计看满303个后，每天只能看10个，看满10个后需要购买VIP或者单独购买
     * 4、	当用户累计看满403个后，每天只能看5个，看满5个后需要买VIP或者单独购买
     * 5、	当用户累计看满503个后，每天只能看1个，看满1个有需要购买VIP或单独购买。
     * 6、	用户充值VIP后免费观看平台所有视频
     * 7、	支付成功后吐司提示购买成功
     * 8、	用户点击一次计算一次观看次数，重复观看也计算在内，付费后看的不计算次数
     */
    private void setPlayMessage() {
        String cuDayAndMonth = DateUtils.milliToDayAndMonthAndYear(System.currentTimeMillis());
        String dayAndMonth = SharedPrefrenceUtils.getString(this,"dayAndMonthAndYear");
        if(TextUtils.isEmpty(dayAndMonth)){ //第一次下载，没有播放过
            SharedPrefrenceUtils.setString(this,"dayAndMonthAndYear",cuDayAndMonth);
            SharedPrefrenceUtils.setInt(this,"playCount",1);
            type = 1;
        }else if(!cuDayAndMonth.equals(dayAndMonth)){//当天没有播放过
            SharedPrefrenceUtils.setString(this,"dayAndMonthAndYear",cuDayAndMonth);
            SharedPrefrenceUtils.setInt(this,"playCount",1);
            type = 1;
        }else {//当天播放过
            int count = SharedPrefrenceUtils.getInt(this,"playCount",0);
            if(count < 3){
                count ++;
                SharedPrefrenceUtils.setInt(this,"playCount",count);
                type = 1;
            }else {
                type = 2;
            }
        }
    }

    private void setPlayInfoMessage() {
        if(!TextUtils.isEmpty(videoBean.getV_url())){
            if(!TextUtils.isEmpty(videoBean.getVideo_pic())){
//                if(mAliyunVodPlayerView != null){
//                    mAliyunVodPlayerView.setCoverUri(videoBean.getVideo_pic());
//                }
            }
            PlayParameter.PLAY_PARAM_URL = videoBean.getV_url();
            PlayParameter.PLAY_PARAM_TYPE = "localSource";
        }else {
            PlayParameter.PLAY_PARAM_TYPE = "vidsts";
        }
        iv_video.setVisibility(View.GONE);
        setPlaySource();
    }

    private void setVideoShowMessage() {
        if(!TextUtils.isEmpty(videoBean.getHead_pic())){
//            Picasso.with(this).load(videoBean.getHead_pic()).into(iv_haed);
            Picasso.with(this)
                    .load(videoBean.getHead_pic())
                    .resize(UIUtils.dip2px(35),UIUtils.dip2px(35))
                    .config(Bitmap.Config.RGB_565)
                    .centerCrop().into(iv_haed);
        }
        if(!TextUtils.isEmpty(videoBean.getNickname())){
            tv_nickname.setText(videoBean.getNickname());
        }
        if(!TextUtils.isEmpty(videoBean.getVideo_dec())){
            tv_describe.setText(videoBean.getVideo_dec());
        }
        if(!TextUtils.isEmpty(videoBean.getAttention_amount())){
            tv_love_num.setText(videoBean.getAttention_amount());
        }
        if(!TextUtils.isEmpty(videoBean.getPraised_amount())){
            tv_praise.setText(videoBean.getPraised_amount());
        }
        v_id = videoBean.getV_id();
    }




    private void getInfo() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(usr_id)){
            reqBody.put("usrs_id", usr_id);
        }else {
            reqBody.put("usrs_id", "0");
        }
        reqBody.put("video_id", video_id);
        reqBody.put("play_source", String.valueOf(fromSource));
        LogUtils.e(TAG+"获取视频详情参数："+reqBody.toString());
        loadingDialog.show();
        netUtils.postDataAsynToNet(Constants.GetVideoInfoUrl,reqBody ,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETINFOSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETINFOFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
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
        mAliyunVodPlayerView.setAutoPlay(true);
//        mAliyunVodPlayerView.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        mAliyunVodPlayerView.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);


        mAliyunVodPlayerView.setOnPreparedListener(new MyPrepareListener(this));
        mAliyunVodPlayerView.setNetConnectedListener(new MyNetConnectedListener(this));
        mAliyunVodPlayerView.setOnCompletionListener(new MyCompletionListener(this));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new MyFrameInfoListener(this));
        mAliyunVodPlayerView.setOnChangeQualityListener(new MyChangeQualityListener(this));
        mAliyunVodPlayerView.setOnStoppedListener(new MyStoppedListener(this));
        mAliyunVodPlayerView.setmOnPlayerViewClickListener(new MyPlayViewClickListener());
        mAliyunVodPlayerView.setOrientationChangeListener(new MyOrientationChangeListener(this));
        mAliyunVodPlayerView.setOnUrlTimeExpiredListener(new MyOnUrlTimeExpiredListener(this));
        mAliyunVodPlayerView.setOnShowMoreClickListener(new MyShowMoreClickLisener(this));
        mAliyunVodPlayerView.enableNativeLog();

        iv_video.setVisibility(View.GONE);
        setGestureListener();

    }

    private void copyAssets() {
        Commen.getInstance(getApplicationContext()).copyAssetsToSD("encrypt", "aliyun").setFileOperateCallback(

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
                        downloadManager = AliyunDownloadManager.getInstance(getApplicationContext());
                        downloadManager.setDownloadConfig(config);

                        downloadDataProvider = DownloadDataProvider.getSingleton(getApplicationContext());
                        // 更新sts回调
                        downloadManager.setRefreshStsCallback(new MyRefreshStsCallback());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOTOBUYVIP){
            if(resultCode == 200){
                if(buyDialog != null){
                    buyDialog.dismiss();
                }
                usr_id = SharedPrefrenceUtils.getString(this,"usrs_id");
//                UIUtils.showToastCenter(VideoPlayActivity.this,"支付成功");
                getInfo();
            }
        }else if(requestCode == GOTOLOGIN){
            if(resultCode == 200){
                usr_id = SharedPrefrenceUtils.getString(this,"usrs_id");
                int is_first = SharedPrefrenceUtils.getInt(this,"is_first",0);
                if(is_first == 1){
                    String classModelListStr = SharedPrefrenceUtils.getString(VideoPlayActivity.this,"classModelListStr");
                    if(!TextUtils.isEmpty(classModelListStr)){
                        classModelList = gson.fromJson(classModelListStr,new TypeToken<List<ClassModel>>(){}.getType());
                        if(classModelList != null && classModelList.size()>0){
                            recommendDialog = DialogUtils.showRecommendAttentionDialog(VideoPlayActivity.this,classModelList,this);
                        }
                    }
                }
            }
        }
    }

    public void pausePlay(){
        if(mAliyunVodPlayerView != null && mAliyunVodPlayerView.isPlaying()){
            mAliyunVodPlayerView.pause();
            iv_video.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_haed:
                pausePlay();
                if(videoBean != null && !TextUtils.isEmpty(usr_id)){
                    Intent intent = new Intent(VideoPlayActivity.this,SelfMediaDetailActivity.class);
                    intent.putExtra("self_media_id",videoBean.getSelf_media_id());
                    intent.putExtra("fromSource",fromSource);
                    UIUtils.startActivity(intent);
                }
                break;
            case R.id.iv_love:
                if(!TextUtils.isEmpty(video_id) && !TextUtils.isEmpty(usr_id)){
                    attentionVideo();
                }
                break;
            case R.id.iv_praise:
                if(!TextUtils.isEmpty(video_id) && !TextUtils.isEmpty(usr_id)){
                    praiseVideo();
                }
                break;
            case R.id.btn_buy_vip:
                Intent intent1 = new Intent(VideoPlayActivity.this, BuyVIPActivity.class);
                UIUtils.startActivityForResult(intent1,GOTOBUYVIP);
                break;
            case R.id.btn_buy_one:
//                buyDialog.dismiss();
                pay();
                break;
            case R.id.iv_close:
                buyDialog.dismiss();
                finish();
                break;
            case R.id.btn_pass:
                recommendDialog.dismiss();
                SharedPrefrenceUtils.setInt(VideoPlayActivity.this,"is_first",0);
                getInfo();
                break;
            case R.id.btn_submit:
                selectClassIds = "";
                if(classModelList != null && classModelList.size()>0){
                    for(ClassModel classModel : classModelList){
                        if(classModel.isSelected()){
                            selectClassIds = selectClassIds + ","+classModel.getClass_id();
                        }
                    }
                }
                if(selectClassIds.length()>0){
                    selectClassIds = selectClassIds.substring(1);
                    subscribClass();
                }else {
                    recommendDialog.dismiss();
                }
                break;
        }
    }

    private void subscribClass(){
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("class_id_list",selectClassIds);
        reqBody.put("usrs_id",usr_id);
        LogUtils.e(TAG+"订阅参数："+reqBody.toString());
        netUtils.postDataAsynToNet(Constants.SubscribeClassUrl,reqBody,new NetUtils.MyNetCall(){
            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = SUBSCRIBECLASSSUCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = SUBSCRIBECLASSFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }


    private void pay(){
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", usr_id);
//        reqBody.put("bay_money", "1");
        reqBody.put("bay_money", "0.01");
        reqBody.put("bay_source", "1");
        reqBody.put("video_id", video_id);
        reqBody.put("self_media_id", "0");
        netUtils.postDataAsynToNet(Constants.WeiXinPayUrl,reqBody ,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETBUYINFOSUCCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETBYINFOFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });
    }

    /**
     * 收藏视频
     */
    private void attentionVideo() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", usr_id);
        reqBody.put("video_id", video_id);
        if(isAttentioned){
            reqBody.put("operate_type", "1");
        }else {
            reqBody.put("operate_type", "0");

        }
        loadingDialog.show();
        netUtils.postDataAsynToNet(Constants.AttentionVideoUrl,reqBody ,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = ATTENTIONSUCCESS;
                message.obj = str;
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
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", usr_id);
        reqBody.put("video_id", video_id);
        if(isPraised){
            reqBody.put("operate_type", "1");
        }else {
            reqBody.put("operate_type", "0");
        }
        loadingDialog.show();
        netUtils.postDataAsynToNet(Constants.PraisedVideoUrl,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = PRAISESUCCSS;
                message.obj = str;
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

    @Override
    public void onWeChatPaySuccess() {
        buyDialog.dismiss();
        getInfo();
    }

    @Override
    public void onWeChatPayFailure() {
        UIUtils.showToastCenter(VideoPlayActivity.this,"支付失败");
    }

    @Override
    public void onWeChatPayCancel() {
        UIUtils.showToastCenter(VideoPlayActivity.this,"支付取消");
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
            Toast.makeText(VideoPlayActivity.this, "start...download......", Toast.LENGTH_SHORT).show();
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
            Log.e("Test","download...onError...msg:::" + msg + ", requestId:::" + requestId + ", code:::" + code);
            downloadView.updateInfoByError(info);
            if (dialogDownloadView != null) {
                dialogDownloadView.updateInfoByError(info);
            }
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString(DOWNLOAD_ERROR_KEY, msg);
            message.setData(bundle);
            message.what = DOWNLOAD_ERROR;
            playerHandler = new PlayerHandler(VideoPlayActivity.this);
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

        private WeakReference<VideoPlayActivity> activityWeakReference;

        public MyPrepareListener(VideoPlayActivity skinActivity) {
            activityWeakReference = new WeakReference<VideoPlayActivity>(skinActivity);
        }

        @Override
        public void onPrepared() {
            VideoPlayActivity activity = activityWeakReference.get();
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
//        Toast.makeText(this, com.blockchain.vodplayer.R.string.toast_prepare_success,
//                Toast.LENGTH_SHORT).show();
    }

    private static class MyCompletionListener implements IAliyunVodPlayer.OnCompletionListener {

        private WeakReference<VideoPlayActivity> activityWeakReference;

        public MyCompletionListener(VideoPlayActivity skinActivity) {
            activityWeakReference = new WeakReference<VideoPlayActivity>(skinActivity);
        }

        @Override
        public void onCompletion() {

            VideoPlayActivity activity = activityWeakReference.get();
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
//        Toast.makeText(this, com.blockchain.vodplayer.R.string.toast_play_compleion,
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
        if(alivcVideoInfos != null && alivcVideoInfos.size()>0){
            if (currentVideoPosition >= alivcVideoInfos.size() - 1) {
                //列表循环播放，如发现播放完成了从列表的第一个开始重新播放
                currentVideoPosition = 0;
            }

            AlivcVideoInfo.Video video = alivcVideoInfos.get(currentVideoPosition);
            if (video != null) {
                changePlayVidSource(video.getVideoId(), video.getTitle());
            }
        }

    }

    private static class MyFrameInfoListener implements IAliyunVodPlayer.OnFirstFrameStartListener {

        private WeakReference<VideoPlayActivity> activityWeakReference;

        public MyFrameInfoListener(VideoPlayActivity skinActivity) {
            activityWeakReference = new WeakReference<VideoPlayActivity>(skinActivity);
        }

        @Override
        public void onFirstFrameStart() {

            VideoPlayActivity activity = activityWeakReference.get();
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
        downloadDialog = new DownloadChoiceDialog(this, screenMode);
        final AddDownloadView contentView = new AddDownloadView(this, screenMode);
        contentView.onPrepared(aliyunDownloadMediaInfoList);
        contentView.setOnViewClickListener(viewClickListener);
        final View inflate = LayoutInflater.from(getApplicationContext()).inflate(
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
                int permission = ContextCompat.checkSelfPermission(VideoPlayActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(VideoPlayActivity.this, PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE);

                } else {
                    addNewInfo(info);
                }
            } else {
                addNewInfo(info);
            }

        }
    };

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        return;
    }

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
                Toast.makeText(this, "没有sd卡读写权限, 无法下载", Toast.LENGTH_SHORT).show();
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

        private WeakReference<VideoPlayActivity> activityWeakReference;

        public MyChangeQualityListener(VideoPlayActivity skinActivity) {
            activityWeakReference = new WeakReference<VideoPlayActivity>(skinActivity);
        }

        @Override
        public void onChangeQualitySuccess(String finalQuality) {

            VideoPlayActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onChangeQualitySuccess(finalQuality);
            }
        }

        @Override
        public void onChangeQualityFail(int code, String msg) {
            VideoPlayActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onChangeQualityFail(code, msg);
            }
        }
    }

    private void onChangeQualitySuccess(String finalQuality) {
        logStrs.add(format.format(new Date()) + getString(com.blockchain.vodplayer.R.string.log_change_quality_success));
        Toast.makeText(this,
                getString(com.blockchain.vodplayer.R.string.log_change_quality_success), Toast.LENGTH_SHORT).show();
    }

    void onChangeQualityFail(int code, String msg) {
        logStrs.add(format.format(new Date()) + getString(com.blockchain.vodplayer.R.string.log_change_quality_fail) + " : " + msg);
        Toast.makeText(this,
                getString(com.blockchain.vodplayer.R.string.log_change_quality_fail), Toast.LENGTH_SHORT).show();
    }

    private static class MyStoppedListener implements IAliyunVodPlayer.OnStoppedListener {

        private WeakReference<VideoPlayActivity> activityWeakReference;

        public MyStoppedListener(VideoPlayActivity skinActivity) {
            activityWeakReference = new WeakReference<VideoPlayActivity>(skinActivity);
        }

        @Override
        public void onStopped() {

            VideoPlayActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onStopped();
            }
        }
    }

    private void onStopped() {
        Toast.makeText(this,com.blockchain.vodplayer.R.string.log_play_stopped,
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
//                vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
//                vidSts.setAcId(PlayParameter.PLAY_PARAM_AK_ID);
//                vidSts.setAkSceret(PlayParameter.PLAY_PARAM_AK_SECRE);
//                vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
                if(!TextUtils.isEmpty(v_id) && !v_id.equals("1")){
                    vidSts.setVid(v_id);
                }else {
                    vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
                }
                vidSts.setAcId(accessKeyId);
                vidSts.setAkSceret(accessKeySecret);
                vidSts.setSecurityToken(securityToken);
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

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
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

        if(mHandller != null){
            mHandller.removeCallbacksAndMessages(null);
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
        private final WeakReference<VideoPlayActivity> mActivty;

        public PlayerHandler(VideoPlayActivity activity) {
            mActivty = new WeakReference<VideoPlayActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoPlayActivity activity = mActivty.get();
            super.handleMessage(msg);
            if (activity != null) {
                switch (msg.what) {
                    case DOWNLOAD_ERROR:
                        Toast.makeText(mActivty.get(), msg.getData().getString(DOWNLOAD_ERROR_KEY), Toast.LENGTH_LONG)
                                .show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static class MyStsListener implements VidStsUtil.OnStsResultListener {

        private WeakReference<VideoPlayActivity> weakctivity;

        public MyStsListener(VideoPlayActivity act) {
            weakctivity = new WeakReference<VideoPlayActivity>(act);
        }

        @Override
        public void onSuccess(String vid, String akid, String akSecret, String token) {
            VideoPlayActivity activity = weakctivity.get();
            if (activity != null) {
                activity.onStsSuccess(vid, akid, akSecret, token);
            }
        }

        @Override
        public void onFail() {
            VideoPlayActivity activity = weakctivity.get();
            if (activity != null) {
                activity.onStsFail();
            }
        }
    }

    private void onStsFail() {

        Toast.makeText(this, com.blockchain.vodplayer.R.string.request_vidsts_fail, Toast.LENGTH_LONG).show();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alivcVideoInfos.addAll(videos);
                                alivcPlayListAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                });
    }

    private static class MyOrientationChangeListener implements AliyunVodPlayerView.OnOrientationChangeListener {

        private final WeakReference<VideoPlayActivity> weakReference;

        public MyOrientationChangeListener(VideoPlayActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void orientationChange(boolean from, AliyunScreenMode currentMode) {
            VideoPlayActivity activity = weakReference.get();
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
        WeakReference<VideoPlayActivity> weakReference;

        public MyNetConnectedListener(VideoPlayActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            VideoPlayActivity activity = weakReference.get();
            activity.onReNetConnected(isReconnect);
        }

        @Override
        public void onNetUnConnected() {
            VideoPlayActivity activity = weakReference.get();
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
                    Toast.makeText(this, "网络恢复, 请手动开启下载任务...", Toast.LENGTH_SHORT).show();
                }
            }
            VidStsUtil.getVidSts(PlayParameter.PLAY_PARAM_VID, new MyStsListener(this));
        }
    }

    private static class MyOnUrlTimeExpiredListener implements IAliyunVodPlayer.OnUrlTimeExpiredListener {
        WeakReference<VideoPlayActivity> weakReference;

        public MyOnUrlTimeExpiredListener(VideoPlayActivity activity) {
            weakReference = new WeakReference<VideoPlayActivity>(activity);
        }

        @Override
        public void onUrlTimeExpired(String s, String s1) {
            VideoPlayActivity activity = weakReference.get();
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
        WeakReference<VideoPlayActivity> weakReference;

        MyShowMoreClickLisener(VideoPlayActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void showMore() {
            VideoPlayActivity activity = weakReference.get();
            activity.showMore(activity);
        }
    }

    private void showMore(final VideoPlayActivity activity) {
        showMoreDialog = new AlivcShowMoreDialog(this);
        AliyunShowMoreValue moreValue = new AliyunShowMoreValue();
        moreValue.setSpeed(mAliyunVodPlayerView.getCurrentSpeed());
        moreValue.setVolume(mAliyunVodPlayerView.getCurrentVolume());
        moreValue.setScreenBrightness(mAliyunVodPlayerView.getCurrentScreenBrigtness());

        ShowMoreView showMoreView = new ShowMoreView(this, moreValue);
        showMoreDialog.setContentView(showMoreView);
        showMoreDialog.show();
        showMoreView.setOnDownloadButtonClickListener(new ShowMoreView.OnDownloadButtonClickListener() {
            @Override
            public void onDownloadClick() {
                // 点击下载
                showMoreDialog.dismiss();
                if ("localSource".equals(PlayParameter.PLAY_PARAM_TYPE)) {
                    Toast.makeText(VideoPlayActivity.this, "Url类型不支持下载", Toast.LENGTH_SHORT).show();
                    return;
                }
                showAddDownloadView(AliyunScreenMode.Full);
            }
        });

        showMoreView.setOnScreenCastButtonClickListener(new ShowMoreView.OnScreenCastButtonClickListener() {
            @Override
            public void onScreenCastClick() {
                Toast.makeText(VideoPlayActivity.this, "功能开发中, 敬请期待...", Toast.LENGTH_SHORT).show();
            }
        });

        showMoreView.setOnBarrageButtonClickListener(new ShowMoreView.OnBarrageButtonClickListener() {
            @Override
            public void onBarrageClick() {
                Toast.makeText(VideoPlayActivity.this, "功能开发中, 敬请期待...", Toast.LENGTH_SHORT).show();
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
                final AlivcDialog alivcDialog = new AlivcDialog(VideoPlayActivity.this);
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
                                    Toast.makeText(VideoPlayActivity.this, "没有删除的视频选项...", Toast.LENGTH_SHORT)
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

    private void getStsToken() {

        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
//        loadingDialog.show();
        netUtils.postDataAsynToNet(Constants.GetStsToken,reqBody ,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETSTSSUCESS;
                message.obj = str;
                mHandller.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETSTSFAILED;
                message.obj = e;
                mHandller.sendMessage(message);
            }
        });

    }

    public void setStatusBar1(int colorId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(colorId);
        Class clazz = this.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if(true){
                extraFlagField.invoke(this.getWindow(),darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
            }else{
                extraFlagField.invoke(this.getWindow(), 0, darkModeFlag);//清除黑色字体
            }
        }catch (Exception e){

        }
    }
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
    }

}
