package com.blockchain.shortvideoplayer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.blockchain.shortvideoplayer.MutiApplication;
import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.constants.Constants;
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
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.layoutmanagergroup.viewpager.OnViewPagerListener;
import com.blockchain.shortvideoplayer.widget.layoutmanagergroup.viewpager.ViewPagerLayoutManager;
import com.blockchain.shortvideoplayer.wxapi.WeChatPayCallback;
import com.blockchain.vodplayerview.constants.PlayParameter;
import com.blockchain.vodplayerview.widget.AliyunVodPlayerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class VideoPlayActivity3 extends BaseActivity implements View.OnClickListener, WeChatPayCallback {
    private static final String TAG = VideoPlayActivity3.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ViewPagerLayoutManager mLayoutManager;
    private static final String DEFAULT_URL = "";
    //    private String video_id;
    private String videoListStr;
    private List<VideoBean> videoBeanList;
    private int selectPostion;
    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private String usr_id;
    private int fromSource;
//    private VideoBean videoBean;
    private AlertDialog buyDialog;
    public static final int GOTOBUYVIP = 101;
    public static final int GOTOLOGIN = 102;
    private int type; //没有登录的时候  1 当天看3个以下可以播放，   2 看完3个需要登录登录
    private boolean isAttentioned; //是否收藏
    private boolean isPraised;  //是否点赞
    private boolean isAttentionOrPraise;  //点赞和收藏后重新拉取数据不设置播放信息只更新点赞数和收藏数

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
    private AlertDialog recommendDialog;  //推荐类目对话框
    private List<ClassModel> classModelList;
    private AsyncTask<String,Integer,String> task ;

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_video_play3, null);
        setContentView(rootView);

        mRecyclerView = findViewById(R.id.recycler);

        initData();
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mAdapter = new MyAdapter(this, videoBeanList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        initListener();
        return rootView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOTOBUYVIP) {
            if (resultCode == 200) {
                if (buyDialog != null) {
                    buyDialog.dismiss();
                }
                usr_id = SharedPrefrenceUtils.getString(this, "usrs_id");
//                UIUtils.showToastCenter(VideoPlayActivity.this,"支付成功");
//                getInfo();
            }
        } else if (requestCode == GOTOLOGIN) {
            if (resultCode == 200) {
                usr_id = SharedPrefrenceUtils.getString(this, "usrs_id");
                int is_first = SharedPrefrenceUtils.getInt(this, "is_first", 0);
                if (is_first == 1) {
                    String classModelListStr = SharedPrefrenceUtils.getString(VideoPlayActivity3.this, "classModelListStr");
                    if (!TextUtils.isEmpty(classModelListStr)) {
                        classModelList = gson.fromJson(classModelListStr, new TypeToken<List<ClassModel>>() {
                        }.getType());
                        if (classModelList != null && classModelList.size() > 0) {
                            recommendDialog = DialogUtils.showRecommendAttentionDialog(VideoPlayActivity3.this, classModelList, this);
                        }
                    }
                }
            }
        }
    }

    private void initData() {
        usr_id = SharedPrefrenceUtils.getString(this, "usrs_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        Intent intent = getIntent();
//        video_id = intent.getStringExtra("video_id");
        selectPostion = intent.getIntExtra("postion", 0);
        videoListStr = intent.getStringExtra("videoListStr");
        fromSource = intent.getIntExtra("fromSource", 0);
        if (!TextUtils.isEmpty(videoListStr)) {
            videoBeanList = gson.fromJson(videoListStr, new TypeToken<List<VideoBean>>() {
            }.getType());
        }

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
        String dayAndMonth = SharedPrefrenceUtils.getString(this, "dayAndMonthAndYear");
        if (TextUtils.isEmpty(dayAndMonth)) { //第一次下载，没有播放过
            SharedPrefrenceUtils.setString(this, "dayAndMonthAndYear", cuDayAndMonth);
            SharedPrefrenceUtils.setInt(this, "playCount", 1);
            type = 1;
        } else if (!cuDayAndMonth.equals(dayAndMonth)) {//当天没有播放过
            SharedPrefrenceUtils.setString(this, "dayAndMonthAndYear", cuDayAndMonth);
            SharedPrefrenceUtils.setInt(this, "playCount", 1);
            type = 1;
        } else {//当天播放过
            int count = SharedPrefrenceUtils.getInt(this, "playCount", 0);
            if (count < 3) {
                count++;
                SharedPrefrenceUtils.setInt(this, "playCount", count);
                type = 1;
            } else {
                type = 2;
            }
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            onKeyDownMethod(keyCode, event);
            releaseVideo(0);
            finish();
            overridePendingTransition(R.anim.animprv_in, R.anim.animprv_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

            @Override
            public void onInitComplete() {
                Log.e(TAG, "onInitComplete");
                playVideo(0);

            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.e(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                playVideo(0);
            }


        });
    }

    private void playVideo(int position) {
        View itemView = mRecyclerView.getChildAt(position);
        final AliyunVodPlayerView videoView = itemView.findViewById(R.id.video_view);
        final ImageView imgPlay = itemView.findViewById(R.id.iv_video);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
        videoView.start();
        imgThumb.animate().alpha(0).setDuration(200).start();
//        videoView.setOnInfoListener(new IAliyunVodPlayer.OnInfoListener() {
//            @Override
//            public void onInfo(int i, int i1) {
//                imgThumb.animate().alpha(0).setDuration(200).start();
//            }
//        });
//        videoView.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared() {
//                imgThumb.animate().alpha(0).setDuration(200).start();
//            }
//        });
//        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                mediaPlayer[0] = mp;
//                mp.setLooping(true);
//                imgThumb.animate().alpha(0).setDuration(200).start();
//                return false;
//            }
//        });
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//
//            }
//        });


        imgPlay.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = true;

            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    imgPlay.animate().alpha(1f).start();
                    videoView.pause();
                    isPlaying = false;
                } else {
                    imgPlay.animate().alpha(0f).start();
                    videoView.start();
                    isPlaying = true;
                }
            }
        });
    }

    private void releaseVideo(int index) {
        View itemView = mRecyclerView.getChildAt(index);
        final AliyunVodPlayerView videoView = itemView.findViewById(R.id.video_view);
        final ImageView imgPlay = itemView.findViewById(R.id.iv_video);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        imgThumb.animate().alpha(1).start();
        videoView.pause();
        imgPlay.animate().alpha(0f).start();
    }

    @Override
    public void onWeChatPaySuccess() {
        buyDialog.dismiss();
        mAdapter.getInfo();
    }

    @Override
    public void onWeChatPayFailure() {
        UIUtils.showToastCenter(VideoPlayActivity3.this, "支付失败");
    }

    @Override
    public void onWeChatPayCancel() {
        UIUtils.showToastCenter(VideoPlayActivity3.this, "支付取消");
    }

    @Override
    public void onClick(View view) {

    }

    private void weixinPay(PayInfoModel payInfoModel) {
        MutiApplication.getApplication().weChatPayCallback = this;
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.WeiXinAppId, false);
        ;
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


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
        //        private int[] imgs = {R.mipmap.img_video_1,R.mipmap.img_video_2};
//        private int[] videos = {R.raw.video_1,R.raw.video_2};
        private List<VideoBean> videoBeanList;
        private String accessKeyId;
        private String accessKeySecret;
        private String securityToken;
        private AliyunVodPlayerView mAliyunVodPlayerView = null;
        private String v_id;
        private ImageView iv_back;
        private ImageView iv_video;
        private ImageView iv_haed;
        private ImageView iv_love;
        private TextView tv_love_num;
        private ImageView iv_praise;
        private TextView tv_praise;
        private TextView tv_describe;
        private TextView tv_aite;
        private TextView tv_nickname;
        private Context mContext;
        private String videoId;
        private String selectClassIds = "";
        private boolean isPlaying = true;
        private VideoBean videoBean;
        private boolean isQuestInfoSuccess = true;
//        private boolean isQuestLoveAndPraiseSuccess = true;

        private MyHandler mHandller = new MyHandler(VideoPlayActivity3.this) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case GETINFOSUCCESS:  //获取详情成功
                        isQuestInfoSuccess = true;
                        String str = (String) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "获取详情成功：" + str);
//                        LogUtils.log2File(VideoPlayActivity3.this,"videoId:"+videoId+",获取详情成功：" + str);
                        GetVideoInfoResponse getVideoInfoResponse = gson.fromJson(str, GetVideoInfoResponse.class);
                        if (getVideoInfoResponse != null) {
                            if (getVideoInfoResponse.getCode() == 0) {
                                videoBean = getVideoInfoResponse.getData();
                                if (videoBean != null) {
//                                    if (videoBean.getVideo_id().equals(videoId)) {
                                        if (TextUtils.isEmpty(usr_id)) {
                                            setPlayMessage();
                                            if (type == 1) {
                                                if (isAttentionOrPraise) {
                                                    isAttentionOrPraise = false;
                                                    setVideoShowMessage();
                                                } else {
                                                    setVideoShowMessage();
//                                            setPlayInfoMessage();
                                                    getStsToken();
                                                }
                                            } else {
                                                isAttentionOrPraise = false;
                                                UIUtils.showToastCenter(VideoPlayActivity3.this, "您的今天的观看次数已用完，请登录后继续观看");
                                                Intent intent = new Intent(VideoPlayActivity3.this, LoginActivity.class);
                                                UIUtils.startActivityForResult(intent, GOTOLOGIN);
                                                return;
                                            }
                                        } else {
                                            if (isAttentionOrPraise) {
                                                isAttentionOrPraise = false;
                                                setVideoShowMessage();
                                            } else {
                                                setVideoShowMessage();
//                                        setPlayInfoMessage();
                                                getStsToken();
                                            }
                                        }
//                                    }
                                }
//                                videoBean = getVideoInfoResponse.getData();
                            } else if (getVideoInfoResponse.getCode() == -2) {
                                isAttentionOrPraise = false;
                                buyDialog = DialogUtils.showBuyDialog(VideoPlayActivity3.this, MyAdapter.this);
                                return;
                            } else {
                                isAttentionOrPraise = false;
//                            UIUtils.showToastCenter(VideoPlayActivity.this,getVideoInfoResponse.getMsg());
                            }
                        }
                        break;
                    case GETINFOFAILED: //获取详情失败
                        isQuestInfoSuccess = true;
                        IOException e = (IOException) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "获取详情失败：" + e.getMessage());
//                        LogUtils.log2File(VideoPlayActivity3.this,"videoId:"+videoId+",获取详情失败：" + e.getMessage());
                        break;
                    case ATTENTIONSUCCESS: //收藏视频成功
                        String string = (String) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "收藏视频成功：" + string);
                        BaseResponse baseResponse = gson.fromJson(string, BaseResponse.class);
                        if (baseResponse != null) {
                            if (baseResponse.getCode() == 0) {
                                if (isAttentioned) {
                                    isAttentioned = false;
                                    iv_love.setImageResource(R.mipmap.love_unselected);
                                } else {
                                    isAttentioned = true;
                                    iv_love.setImageResource(R.mipmap.love_selected);
                                }
                                isAttentionOrPraise = true;
                                getInfo();
                            } else {
//                            UIUtils.showToastCenter(VideoPlayActivity.this,baseResponse.getMsg());
                            }
                        }
                        break;
                    case ATTENTIONFAILED: //收藏失败
                        IOException e1 = (IOException) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "收藏视频失败：" + e1.getMessage());
//                    UIUtils.showToastCenter(VideoPlayActivity.this,e1.getMessage());
                        break;
                    case PRAISESUCCSS: //点赞视频成功
                        String string1 = (String) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "点赞视频成功：" + string1);
//                        LogUtils.log2File(VideoPlayActivity3.this,"videoId:"+"点赞视频成功：" + string1);
                        BaseResponse baseResponse1 = gson.fromJson(string1, BaseResponse.class);
                        if (baseResponse1 != null) {
                            if (baseResponse1.getCode() == 0) {
                                if (isPraised) {
                                    isPraised = false;
                                    iv_praise.setImageResource(R.mipmap.praise_unselected);
                                } else {
                                    isPraised = true;
                                    iv_praise.setImageResource(R.mipmap.praise_selected);

                                }
                                isAttentionOrPraise = true;
                                getInfo();
                            } else {
//                            UIUtils.showToastCenter(VideoPlayActivity.this,baseResponse1.getMsg());
                            }
                        }
                        break;
                    case PRAISEFAILED: //点赞失败
                        IOException e2 = (IOException) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "点赞视频失败：" + e2.getMessage());
//                    UIUtils.showToastCenter(VideoPlayActivity.this,e2.getMessage());
                        break;
                    case ISPRAISEATTENTIONSUCCESS:  //是否点赞和收藏
//                        isQuestLoveAndPraiseSuccess = true;
                        String string3 = (String) msg.obj;
                        LogUtils.e(TAG + "是否点赞和收藏：" + string3);
                        loadingDialog.dismiss();
                        IsPraiseAttentionResponse isPraiseAttentionResponse = gson.fromJson(string3, IsPraiseAttentionResponse.class);
                        if (isPraiseAttentionResponse != null) {
                            if (isPraiseAttentionResponse.getCode() == 0) {
                                if (isPraiseAttentionResponse.getIs_attention() == 1) {
                                    isAttentioned = true;
                                    iv_love.setImageResource(R.mipmap.love_selected);
                                } else {
                                    isAttentioned = false;
                                    iv_love.setImageResource(R.mipmap.love_unselected);
                                }
                                if (isPraiseAttentionResponse.getIs_praised() == 1) {
                                    isPraised = true;
                                    iv_praise.setImageResource(R.mipmap.praise_selected);
                                } else {
                                    isPraised = false;
                                    iv_praise.setImageResource(R.mipmap.praise_unselected);
                                }
                            } else {
//                            UIUtils.showToastCenter(VideoPlayActivity.this,isPraiseAttentionResponse.getMsg());
                            }
                        }
                        break;
                    case ISPRAISEATTENTIONFAILED:  //是否点赞和收藏
//                        isQuestLoveAndPraiseSuccess = true;
                        IOException e5 = (IOException) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "是否收藏和点赞失败：" + e5.getMessage());
//                    UIUtils.showToastCenter(VideoPlayActivity.this,e5.getMessage());
                        break;
                    case GETSTSSUCESS: //获取sts成功
                        String string4 = (String) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "获取sts成功：" + string4);
                        GetStsTokenResponse getStsTokenResponse = gson.fromJson(string4, GetStsTokenResponse.class);
                        if (getStsTokenResponse != null) {
                            if (getStsTokenResponse.getCode() == 0) {
                                accessKeyId = getStsTokenResponse.getAccessKeyId();
                                accessKeySecret = getStsTokenResponse.getAccessKeySecret();
                                securityToken = getStsTokenResponse.getSecurityToken();
                                setPlayInfoMessage();
                            } else {
                                LogUtils.e("获取sts失败:" + getStsTokenResponse.getMsg());
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
                        LogUtils.e(TAG + "获取支付信息成功：" + str3);
                        GetPayInfoResponse getPayInfoResponse = gson.fromJson(str3, GetPayInfoResponse.class);
                        if (getPayInfoResponse != null) {
                            if (getPayInfoResponse.getCode() == 0) {
                                PayInfoModel payInfoModel = getPayInfoResponse.getData();
                                if (payInfoModel != null) {
                                    weixinPay(payInfoModel);
                                }
                            } else {
                                UIUtils.showToastCenter(VideoPlayActivity3.this, getPayInfoResponse.getMsg());
                            }
                        }
                        break;
                    case GETBYINFOFAILED: //获取支付信息失败
                        IOException e3 = (IOException) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "获取支付信息失败：" + e3.getMessage());
                        UIUtils.showToastCenter(VideoPlayActivity3.this, e3.getMessage());
                        break;
                    case SUBSCRIBECLASSSUCESS:  //订阅该类目
                        String string9 = (String) msg.obj;
                        LogUtils.e(TAG + "订阅：" + string9);
                        BaseResponse baseResponse5 = gson.fromJson(string9, BaseResponse.class);
                        if (baseResponse5 != null) {
                            if (baseResponse5.getCode() == 0) {
                                recommendDialog.dismiss();
                                SharedPrefrenceUtils.setInt(VideoPlayActivity3.this, "is_first", 0);
                                getInfo();
                            } else {
                                UIUtils.showToastCenter(VideoPlayActivity3.this, baseResponse5.getMsg());
                            }
                        }
                        break;
                    case SUBSCRIBECLASSFAILED: //订阅该类目
                        IOException e9 = (IOException) msg.obj;
                        loadingDialog.dismiss();
                        LogUtils.e(TAG + "订阅失败:" + e9.getMessage());
                        UIUtils.showToastCenter(VideoPlayActivity3.this, e9.getMessage());
                        break;
                }
            }
        };

        private void setVideoShowMessage() {
            if (!TextUtils.isEmpty(videoBean.getHead_pic())) {
//            Picasso.with(this).load(videoBean.getHead_pic()).into(iv_haed);
                Picasso.with(mContext)
                        .load(videoBean.getHead_pic())
                        .resize(UIUtils.dip2px(35), UIUtils.dip2px(35))
                        .config(Bitmap.Config.RGB_565)
                        .centerCrop().into(iv_haed);
            }
            if (!TextUtils.isEmpty(videoBean.getNickname())) {
                tv_nickname.setText(videoBean.getNickname());
            }
            if (!TextUtils.isEmpty(videoBean.getVideo_dec())) {
                tv_describe.setText(videoBean.getVideo_dec());
            }
            if (!TextUtils.isEmpty(videoBean.getAttention_amount())) {
                tv_love_num.setText(videoBean.getAttention_amount());
            }
            if (!TextUtils.isEmpty(videoBean.getPraised_amount())) {
                tv_praise.setText(videoBean.getPraised_amount());
            }
            v_id = videoBean.getV_id();
        }

        private void setPlayInfoMessage() {
            if (!TextUtils.isEmpty(videoBean.getV_url())) {
                if (!TextUtils.isEmpty(videoBean.getVideo_pic())) {
//                if(mAliyunVodPlayerView != null){
//                    mAliyunVodPlayerView.setCoverUri(videoBean.getVideo_pic());
//                }
                }
                PlayParameter.PLAY_PARAM_URL = videoBean.getV_url();
                PlayParameter.PLAY_PARAM_TYPE = "localSource";
            } else {
                PlayParameter.PLAY_PARAM_TYPE = "vidsts";
            }
//            iv_video.setVisibility(View.GONE);
            setPlaySource();
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
//                if (!inRequest) {
                AliyunVidSts vidSts = new AliyunVidSts();
//                vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
//                vidSts.setAcId(PlayParameter.PLAY_PARAM_AK_ID);
//                vidSts.setAkSceret(PlayParameter.PLAY_PARAM_AK_SECRE);
//                vidSts.setSecurityToken(PlayParameter.PLAY_PARAM_SCU_TOKEN);
                if (!TextUtils.isEmpty(v_id) && !v_id.equals("1")) {
                    vidSts.setVid(v_id);
                } else {
                    vidSts.setVid(PlayParameter.PLAY_PARAM_VID);
                }
                vidSts.setAcId(accessKeyId);
                vidSts.setAkSceret(accessKeySecret);
                vidSts.setSecurityToken(securityToken);
                if (mAliyunVodPlayerView != null) {
                    mAliyunVodPlayerView.setVidSts(vidSts);
                }
//                downloadManager.prepareDownloadMedia(vidSts);
//                }
            }
        }

        /**
         * 查看视频是否被收藏和点赞
         */
        private void isAttentionAndPraise() {
                Map<String, String> reqBody = new ConcurrentSkipListMap<>();
                if (!TextUtils.isEmpty(usr_id)) {
                    reqBody.put("usrs_id", usr_id);
                }
                reqBody.put("video_id", videoId);
                LogUtils.e(TAG + "是否收藏和点赞参数:" + reqBody.toString());
                loadingDialog.show();
                netUtils.postDataAsynToNet(Constants.IsPraiseAttentionUrl, reqBody, new NetUtils.MyNetCall() {

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


        private void getStsToken() {

            Map<String, String> reqBody = new ConcurrentSkipListMap<>();
//            loadingDialog.show();
            netUtils.postDataAsynToNet(Constants.GetStsToken, reqBody, new NetUtils.MyNetCall() {

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

//            new AsyncTask<String, Integer, String>() {
//
//                @Override
//                protected String doInBackground(String... strings) {
//                    Map<String, String> reqBody = new ConcurrentSkipListMap<>();
//                   Response response =  netUtils.postDataSynToNet(Constants.GetStsToken, reqBody);
//                    String  str;
//                    try {
//                        str = response.body().string();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        str = e.getMessage();
//                    }
//                    return str;
//                }
//
//                @Override
//                protected void onPostExecute(String s) {
//                    GetStsTokenResponse getStsTokenResponse = gson.fromJson(s, GetStsTokenResponse.class);
//                    if (getStsTokenResponse != null) {
//                        if (getStsTokenResponse.getCode() == 0) {
//                            accessKeyId = getStsTokenResponse.getAccessKeyId();
//                            accessKeySecret = getStsTokenResponse.getAccessKeySecret();
//                            securityToken = getStsTokenResponse.getSecurityToken();
//                            setPlayInfoMessage();
//                        } else {
//                            LogUtils.e("获取sts失败:" + getStsTokenResponse.getMsg());
//                        }
//                    }
//                }
//            }.execute();

        }

        public void pausePlay() {
            if (mAliyunVodPlayerView != null && mAliyunVodPlayerView.isPlaying()) {
                mAliyunVodPlayerView.pause();
                iv_video.setVisibility(View.VISIBLE);
            }
        }

        private void getInfo() {
                Map<String, String> reqBody = new ConcurrentSkipListMap<>();
                if (!TextUtils.isEmpty(usr_id)) {
                    reqBody.put("usrs_id", usr_id);
                } else {
                    reqBody.put("usrs_id", "0");
                }
                reqBody.put("video_id", videoId);
                reqBody.put("play_source", String.valueOf(fromSource));
                LogUtils.e(TAG + "获取视频详情参数：" + reqBody.toString());
//            LogUtils.log2File(VideoPlayActivity3.this,"获取视频详情参数：" +  reqBody.toString());
                loadingDialog.show();
                netUtils.postDataAsynToNet(Constants.GetVideoInfoUrl, reqBody, new NetUtils.MyNetCall() {

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
//            new AsyncTask<String, Integer, String>() {
//                @Override
//                protected String doInBackground(String... strings) {
//                    Map<String, String> reqBody = new ConcurrentSkipListMap<>();
//                    if (!TextUtils.isEmpty(usr_id)) {
//                        reqBody.put("usrs_id", usr_id);
//                    } else {
//                        reqBody.put("usrs_id", "0");
//                    }
//                    reqBody.put("video_id", videoId);
//                    reqBody.put("play_source", String.valueOf(fromSource));
//                    LogUtils.e(TAG + "获取视频详情参数：" + reqBody.toString());
//                    LogUtils.log2File(VideoPlayActivity3.this,"videoId:"+"获取视频详情参数：" + reqBody.toString());
//                    Response response = netUtils.postDataSynToNet(Constants.GetVideoInfoUrl, reqBody);
//                    String str;
//                    try {
//                        str = response.body().string();
////                        LogUtils.log2File(VideoPlayActivity3.this,"videoId:"+"获取视频结果：" + str);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        str = e.getMessage();
//                    }
//                    return str;
//                }
//
//                @Override
//                protected void onPostExecute(String s) {
//                    GetVideoInfoResponse getVideoInfoResponse = gson.fromJson(s, GetVideoInfoResponse.class);
//                    if (getVideoInfoResponse != null) {
//                        if (getVideoInfoResponse.getCode() == 0) {
//                            videoBean = getVideoInfoResponse.getData();
//                            if (videoBean != null) {
//                                if (TextUtils.isEmpty(usr_id)) {
//                                    setPlayMessage();
//                                    if (type == 1) {
//                                        if (isAttentionOrPraise) {
//                                            isAttentionOrPraise = false;
//                                            setVideoShowMessage();
//                                        } else {
//                                            setVideoShowMessage();
////                                            setPlayInfoMessage();
//                                            getStsToken();
//                                        }
//                                    } else {
//                                        isAttentionOrPraise = false;
//                                        UIUtils.showToastCenter(VideoPlayActivity3.this, "您的今天的观看次数已用完，请登录后继续观看");
//                                        Intent intent = new Intent(VideoPlayActivity3.this, LoginActivity.class);
//                                        UIUtils.startActivityForResult(intent, GOTOLOGIN);
//                                        return;
//                                    }
//                                } else {
//                                    if (isAttentionOrPraise) {
//                                        isAttentionOrPraise = false;
//                                        setVideoShowMessage();
//                                    } else {
//                                        setVideoShowMessage();
////                                        setPlayInfoMessage();
//                                        getStsToken();
//                                    }
//                                }
//                            }
//                        } else if (getVideoInfoResponse.getCode() == -2) {
//                            isAttentionOrPraise = false;
//                            buyDialog = DialogUtils.showBuyDialog(VideoPlayActivity3.this, MyAdapter.this);
//                            return;
//                        } else {
//                            isAttentionOrPraise = false;
////                            UIUtils.showToastCenter(VideoPlayActivity.this,getVideoInfoResponse.getMsg());
//                        }
//                    }
//                }
//            }.execute();
//            task.execute();
        }

        /**
         * 收藏视频
         */
        private void attentionVideo() {
            Map<String, String> reqBody = new ConcurrentSkipListMap<>();
            reqBody.put("usrs_id", usr_id);
            reqBody.put("video_id", videoId);
            if (isAttentioned) {
                reqBody.put("operate_type", "1");
            } else {
                reqBody.put("operate_type", "0");

            }
            loadingDialog.show();
            netUtils.postDataAsynToNet(Constants.AttentionVideoUrl, reqBody, new NetUtils.MyNetCall() {

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
            reqBody.put("video_id", videoId);
            if (isPraised) {
                reqBody.put("operate_type", "1");
            } else {
                reqBody.put("operate_type", "0");
            }
            loadingDialog.show();
            netUtils.postDataAsynToNet(Constants.PraisedVideoUrl, reqBody, new NetUtils.MyNetCall() {

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

        public MyAdapter(Context context, List<VideoBean> videoBeanList) {
            this.videoBeanList = videoBeanList;
            this.mContext = context;
        }

        private void subscribClass() {
            Map<String, String> reqBody = new ConcurrentSkipListMap<>();
            reqBody.put("class_id_list", selectClassIds);
            reqBody.put("usrs_id", usr_id);
            LogUtils.e(TAG + "订阅参数：" + reqBody.toString());
            netUtils.postDataAsynToNet(Constants.SubscribeClassUrl, reqBody, new NetUtils.MyNetCall() {
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

        private void pay() {
            loadingDialog.show();
            Map<String, String> reqBody = new ConcurrentSkipListMap<>();
            reqBody.put("usrs_id", usr_id);
//        reqBody.put("bay_money", "1");
            reqBody.put("bay_money", "0.01");
            reqBody.put("bay_source", "1");
            reqBody.put("video_id", videoId);
            reqBody.put("self_media_id", "0");
            netUtils.postDataAsynToNet(Constants.WeiXinPayUrl, reqBody, new NetUtils.MyNetCall() {

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

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_play_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
//            holder.img_thumb.setImageResource(imgs[position%2]);
//            holder.mAliyunVodPlayerView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+ videos[position%2]));
            if(isQuestInfoSuccess){
            this.mAliyunVodPlayerView = holder.mAliyunVodPlayerView;
            this.iv_back = holder.iv_back;
            this.iv_video = holder.iv_video;
            this.iv_haed = holder.iv_haed;
            this.iv_love = holder.iv_love;
            this.tv_love_num = holder.tv_love_num;
            this.iv_praise = holder.iv_praise;
            this.tv_praise = holder.tv_praise;
            this.tv_describe = holder.tv_describe;
            this.tv_aite = holder.tv_aite;
            this.tv_nickname = holder.tv_nickname;
//            this.img_thumb = holder.img_thumb;

            mAliyunVodPlayerView.setOnClickListener(this);
            iv_video.setOnClickListener(this);
            iv_haed.setOnClickListener(this);
            iv_love.setOnClickListener(this);
            iv_praise.setOnClickListener(this);
            iv_back.setOnClickListener(this);


                isQuestInfoSuccess = false;
                position = (position + selectPostion) % videoBeanList.size();
                videoId = videoBeanList.get(position).getVideo_id();
                LogUtils.e(TAG + "------------videoId:"+videoId);
//            UIUtils.showToastCenter(VideoPlayActivity3.this,"videoid:"+videoId);
                String pic = videoBeanList.get(position).getVideo_pic();
                if (!TextUtils.isEmpty(pic)) {
                    Picasso.with(VideoPlayActivity3.this).load(pic).into(holder.img_thumb);
                }
                getInfo();
                if (!TextUtils.isEmpty(usr_id)) {
                    isAttentionAndPraise();
                }
            }
        }

        @Override
        public int getItemCount() {
            return videoBeanList.size();
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_back:
                    if (isPlaying) {
                        isPlaying = false;
                        mAliyunVodPlayerView.pause();
                        iv_video.setVisibility(View.VISIBLE);
                    }
                    VideoPlayActivity3.this.finish();
                    break;
                case R.id.iv_haed:
                    pausePlay();
                    if (videoBean != null && !TextUtils.isEmpty(usr_id)) {
                        Intent intent = new Intent(VideoPlayActivity3.this, SelfMediaDetailActivity.class);
                        intent.putExtra("self_media_id", videoBean.getSelf_media_id());
                        intent.putExtra("fromSource", fromSource);
                        UIUtils.startActivity(intent);
                    }
                    break;
                case R.id.iv_love:
                    if (!TextUtils.isEmpty(videoId) && !TextUtils.isEmpty(usr_id)) {
                        attentionVideo();
                    }
                    break;
                case R.id.iv_praise:
                    if (!TextUtils.isEmpty(videoId) && !TextUtils.isEmpty(usr_id)) {
                        praiseVideo();
                    }
                    break;
                case R.id.btn_buy_vip:
                    Intent intent1 = new Intent(VideoPlayActivity3.this, BuyVIPActivity.class);
                    UIUtils.startActivityForResult(intent1, GOTOBUYVIP);
                    break;
                case R.id.btn_buy_one:
                    buyDialog.dismiss();
                    pay();
                    break;
                case R.id.iv_close:
                    buyDialog.dismiss();
                    finish();
                    break;
                case R.id.btn_pass:
                    recommendDialog.dismiss();
                    SharedPrefrenceUtils.setInt(VideoPlayActivity3.this, "is_first", 0);
                    getInfo();
                    break;
                case R.id.btn_submit:
                    selectClassIds = "";
                    if (classModelList != null && classModelList.size() > 0) {
                        for (ClassModel classModel : classModelList) {
                            if (classModel.isSelected()) {
                                selectClassIds = selectClassIds + "," + classModel.getClass_id();
                            }
                        }
                    }
                    if (selectClassIds.length() > 0) {
                        selectClassIds = selectClassIds.substring(1);
                        subscribClass();
                    } else {
                        recommendDialog.dismiss();
                    }
                    break;
                case R.id.video_view:
                    if (isPlaying) {
                        isPlaying = false;
                        mAliyunVodPlayerView.pause();
                        iv_video.setVisibility(View.VISIBLE);
                    } else {
                        isPlaying = true;
                        mAliyunVodPlayerView.start();
                        iv_video.setVisibility(View.GONE);
                    }
                    break;
                case R.id.iv_video:
                    mAliyunVodPlayerView.start();
                    iv_video.setVisibility(View.GONE);
                    break;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            AliyunVodPlayerView mAliyunVodPlayerView;
            ImageView iv_back;
            ImageView iv_video;
            ImageView iv_haed;
            ImageView iv_love;
            TextView tv_love_num;
            ImageView iv_praise;
            TextView tv_praise;
            TextView tv_describe;
            TextView tv_aite;
            TextView tv_nickname;
            ImageView img_thumb;

            public ViewHolder(View itemView) {
                super(itemView);
                mAliyunVodPlayerView = itemView.findViewById(R.id.video_view);
                iv_back = itemView.findViewById(R.id.iv_back);
                iv_video = itemView.findViewById(R.id.iv_video);
                iv_haed = itemView.findViewById(R.id.iv_haed);
                iv_love = itemView.findViewById(R.id.iv_love);
                tv_love_num = itemView.findViewById(R.id.tv_love_num);
                iv_praise = itemView.findViewById(R.id.iv_praise);
                tv_praise = itemView.findViewById(R.id.tv_praise);
                tv_describe = itemView.findViewById(R.id.tv_describe);
                tv_aite = itemView.findViewById(R.id.tv_aite);
                tv_nickname = itemView.findViewById(R.id.tv_nickname);
                img_thumb = itemView.findViewById(R.id.img_thumb);

                mAliyunVodPlayerView.setKeepScreenOn(true);
                PlayParameter.PLAY_PARAM_URL = DEFAULT_URL;
                String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
                mAliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
                mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
                //mAliyunVodPlayerView.setCirclePlay(true);
                mAliyunVodPlayerView.setAutoPlay(true);
                iv_video.setVisibility(View.GONE);
//        mAliyunVodPlayerView.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                mAliyunVodPlayerView.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            }
        }
    }
}
