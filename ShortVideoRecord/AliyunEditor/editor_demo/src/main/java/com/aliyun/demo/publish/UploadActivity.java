package com.aliyun.demo.publish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;
import com.aliyun.common.global.AliyunTag;
import com.aliyun.common.utils.ToastUtil;
import com.aliyun.demo.editor.R;
import com.aliyun.qupai.editor.AliyunICompose;
import com.aliyun.qupaiokhttp.HttpRequest;
import com.aliyun.qupaiokhttp.RequestParams;
import com.aliyun.qupaiokhttp.StringHttpRequestCallback;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by macpro on 2017/11/8.
 */

public class UploadActivity extends Activity {

    private static final String TAG = UploadActivity.class.getSimpleName();
    public static final String KEY_UPLOAD_VIDEO = "video_path";
    public static final String KEY_UPLOAD_THUMBNAIL = "video_thumbnail";
    public static final String KEY_UPLOAD_DESC = "video_desc";

    private ImageView mIvLeft;
    private TextView mTitle;
    private TextureView mTextureView;
    private ProgressBar mProgress;
    private TextView mVideoDesc;
    private TextView mProgressText;
    private MediaPlayer mMediaPlayer;
    private String mVideoPath;
    private String mThumbnailPath;
    private String mDesc;
    private boolean mIsUpload;
    private AliyunICompose mComposeClient;
    private String BaseUrl = "http://47.104.156.153:8068";
//    private String BaseUrl = "http://47.107.32.235:8068";
//    private String BaseUrl = "http://videoservice.beishu.org.cn:8068";
    private String GetStsToken = BaseUrl + "/video_background/selfmedia/getStsToken";  //获取sts token
    private String AddVideoUrl = BaseUrl + "/video_background/selfmedia/addVideoInf";  //上传作品
    private VODSVideoUploadClient vodsVideoUploadClient;
    private VodHttpClientConfig vodHttpClientConfig;
    private String thumbnail;  //封面图
    private String video_id;  //上传视频到阿里云返回视频ID

    private static final int UPLOADTOSERVERSUCCESS = 1;
    private static final int UPLOADTOSERVERFAILED = 2;
    private static final int UPLOADTOALISUCCESS = 100;  //上传到阿里云成功
    private static final int UPLOADTOAL = 0; //正在上传到阿里
    private long progress;
    private NetUtils netUtils;
    private String self_media_id;
    private String title;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOADTOAL:
                    if(progress != 100){
                        mProgressText.setText("正在上传 " + progress + "%");
                        mProgressText.setVisibility(View.VISIBLE);
                    }else {
                        mProgressText.setVisibility(View.VISIBLE);
                        mProgressText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.aliyun_svideo_icon_composite_success, 0, 0, 0);
                        mProgressText.setText(R.string.upload_success);
                    }
                    break;
                case UPLOADTOSERVERSUCCESS: //上传作品
                    String string = (String) msg.obj;
                    Log.e(TAG ,"上传作品成功：" + string);
                    BaseResponse baseResponse = new Gson().fromJson(string,BaseResponse.class);
                    if(baseResponse != null){
                        if(baseResponse.getCode() == 0){
                            finish();
                        }else {
//                            UIUtils.showToastCenter(UploadActivity.this,baseResponse.getMsg());
                        }
                    }
                    break;
                case UPLOADTOSERVERFAILED:  //上传作品失败
                    IOException e1 = (IOException) msg.obj;
                    Log.e(TAG ,"上传作品失败：" + e1.getMessage());
//                    UIUtils.showToastCenter(UploadActivity.this, e1.getMessage());
                    break;
                case UPLOADTOALISUCCESS:
                    uploadToServer();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aliyun_svideo_activity_upload);

        initView();
        mVideoPath = getIntent().getStringExtra(KEY_UPLOAD_VIDEO);
        mThumbnailPath = getIntent().getStringExtra(KEY_UPLOAD_THUMBNAIL);
        mDesc = getIntent().getStringExtra(KEY_UPLOAD_DESC);
        mVideoDesc.setText(mDesc);

        mTextureView.setSurfaceTextureListener(mlistener);
        mComposeClient = ComposeFactory.INSTANCE.getInstance();

        vodsVideoUploadClient = new VODSVideoUploadClientImpl(this.getApplicationContext());
        vodsVideoUploadClient.init();
        vodHttpClientConfig = new VodHttpClientConfig.Builder()
                .setMaxRetryCount(2)//重试次数
                .setConnectionTimeout(15 * 1000)//连接超时
                .setSocketTimeout(15 * 1000)//socket超时
                .build();

        netUtils = NetUtils.getInstance();
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");

        getStsToken();
//        startUpload();
    }

    /**
     * 上传作品到我们自己的服务器
     */
    private void uploadToServer() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        if (!TextUtils.isEmpty(title)) {
            reqBody.put("video_title", title);
        }
        if (!TextUtils.isEmpty(mDesc)) {
            reqBody.put("video_dec", mDesc);
        }
        if (!TextUtils.isEmpty(thumbnail)) {
            reqBody.put("video_pic", thumbnail);
        }
        if (!TextUtils.isEmpty(video_id)) {
            reqBody.put("v_id", video_id);
        }
        Log.e(TAG,"上传作品到服务器参数："+reqBody.toString());
        netUtils.postDataAsynToNet(AddVideoUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = UPLOADTOSERVERSUCCESS;
                message.obj = string;
                handler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = UPLOADTOSERVERFAILED;
                message.obj = e;
                handler.sendMessage(message);
            }
        });
    }

    private void initView(){
        mTitle = (TextView) findViewById(R.id.tv_center);
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(R.string.my_video);
        mVideoDesc = (TextView) findViewById(R.id.video_desc);
        mTextureView = (TextureView) findViewById(R.id.texture);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvLeft.setVisibility(View.VISIBLE);
        mIvLeft.setImageResource(R.drawable.aliyun_svideo_icon_cancel);
        mProgress = (ProgressBar) findViewById(R.id.upload_progress);
        mIvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mProgressText = (TextView) findViewById(R.id.progress_text);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        //intent.setAction("com.duanqu.qupai.action.main");
        // 跳转到home页面, 此处需求可能存在问题
        intent.setAction("com.aliyun.alivcsolution.main");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mIsUpload){
            mComposeClient.resumeUpload();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mIsUpload){
            mComposeClient.pauseUpload();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMediaPlayer != null){
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mComposeClient.release();
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void getStsToken() {
        HttpRequest.post(GetStsToken, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);
                GetStsTokenResponse getStsTokenResponse = new Gson().fromJson(s, GetStsTokenResponse.class);
                if (getStsTokenResponse != null) {
                    if (getStsTokenResponse.getCode() == 0) {
                        uploadVideo(getStsTokenResponse);
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                Log.e(AliyunTag.TAG, "Get token info failed, errorCode:" + errorCode + ", msg:" + msg);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToast(UploadActivity.this, "Get STS token failed");
//                    }
//                });
            }
        });

    }

    private void uploadVideo(GetStsTokenResponse getStsTokenResponse) {
        final String accessKeyId = getStsTokenResponse.getAccessKeyId();
        final String accessKeySecret = getStsTokenResponse.getAccessKeySecret();
        final String securityToken = getStsTokenResponse.getSecurityToken();
        final String expriedTime = getStsTokenResponse.getExpiration();
        SvideoInfo svideoInfo = new SvideoInfo();
        svideoInfo.setTitle(mVideoPath);
        svideoInfo.setDesc("");
        svideoInfo.setCateId(1);
        //构建点播上传参数(重要)
        VodSessionCreateInfo vodSessionCreateInfo = new VodSessionCreateInfo.Builder()
                .setImagePath(mThumbnailPath)//图片地址
                .setVideoPath(mVideoPath)//视频地址
                .setAccessKeyId(accessKeyId)//临时accessKeyId
                .setAccessKeySecret(accessKeySecret)//临时accessKeySecret
                .setSecurityToken(securityToken)//securityToken
                .setExpriedTime(expriedTime)//STStoken过期时间
//                .setRequestID(requestID)//requestID，开发者可以传将获取STS返回的requestID设置也可以不设.
                .setIsTranscode(false)//是否转码.如开启转码请AppSever务必监听服务端转码成功的通知
                .setSvideoInfo(svideoInfo)//短视频视频信息
                .setVodHttpClientConfig(vodHttpClientConfig)//网络参数
                .build();

        vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
            @Override
            public void onUploadSucceed(String videoId, String imageUrl) {
//上传成功返回视频ID和图片URL.
                Log.e(TAG,"videoId:"+videoId+",imageUrl:"+imageUrl);
                video_id = videoId;
                thumbnail = imageUrl;
                handler.sendEmptyMessage(UPLOADTOALISUCCESS);
            }

            @Override
            public void onUploadFailed(String code, String message) {
                //上传失败返回错误码和message.错误码有详细的错误信息请开发者仔细阅读
                Log.e(TAG ,"---onUploadFailed-" + "code:" + code + ",message:" + message);
            }

            @Override
            public void onUploadProgress(long uploadedSize, long totalSize) {
                //上传的进度回调,非UI线程
                Log.e(TAG , "onUploadProgress" + uploadedSize * 100 / totalSize);
                progress = uploadedSize * 100 / totalSize;
                handler.sendEmptyMessage(UPLOADTOAL);
            }

            @Override
            public void onSTSTokenExpried() {
                Log.e(TAG ,"onSTSTokenExpried");
                //STS token过期之后刷新STStoken，如正在上传将会断点续传
                vodsVideoUploadClient.refreshSTSToken(accessKeyId, accessKeySecret, securityToken, expriedTime);
            }

            @Override
            public void onUploadRetry(String code, String message) {
                //上传重试的提醒
                Log.e(TAG , "onUploadRetry" + "code" + code + "message" + message);
            }

            @Override
            public void onUploadRetryResume() {
                //上传重试成功的回调.告知用户重试成功
                Log.e(TAG , "onUploadRetryResume");
            }
        });
    }

    private SecurityTokenInfo getTokenInfo(String json){
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        try{
            JsonElement jsonElement = parser.parse(json);
            JsonObject obj = jsonElement.getAsJsonObject();
            SecurityTokenInfo tokenInfo = gson.fromJson(obj.get("SecurityTokenInfo"), SecurityTokenInfo.class);
            Log.d(AliyunTag.TAG, tokenInfo.toString());
            return tokenInfo;
        }catch (Exception e) {
            Log.e(AliyunTag.TAG, "Get token info failed, json :"+json, e);
            return null;
        }
    }

    private void startUpload(){
        RequestParams params = new RequestParams();
        params.addFormDataPart("BusinessType", "vodai");
        params.addFormDataPart("TerminalType", "andorid");
        params.addFormDataPart("DeviceModel", Build.MODEL);
        params.addFormDataPart("UUID", Build.ID);
        params.addFormDataPart("AppVersion", "1.0.0");
//        HttpRequest.get("https://demo-vod.cn-shanghai.aliyuncs.com/voddemo/CreateSecurityToken",
        HttpRequest.get("https://demo-vod.cn-shanghai.aliyuncs.com/voddemo/CreateSecurityToken",
                params, new StringHttpRequestCallback(){
                    @Override
                    protected void onSuccess(String s) {
                        super.onSuccess(s);
                        Log.d(AliyunTag.TAG, s);
                        SecurityTokenInfo tokenInfo = getTokenInfo(s);
                        if(tokenInfo != null) {
                            String requestID = UUID.randomUUID().toString();
                            SvideoInfo info = new SvideoInfo();
                            info.setTitle("video");
                            info.setDesc(mDesc);
                            info.setCateId(1);
                            int rv = mComposeClient.uploadWithVideoAndImg(
                                    mThumbnailPath,
                                    tokenInfo.getAccessKeyId(),
                                    tokenInfo.getAccessKeySecret(),
                                    tokenInfo.getSecurityToken(),
                                    tokenInfo.getExpiration(),
                                    info, false,null, null,
                                    mUploadCallback);
                            if(rv < 0){
                                Log.d(AliyunTag.TAG, "上传参数错误 video path : " + mVideoPath + " thumbnailk : " + mThumbnailPath);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(UploadActivity.this, "上传参数错误");
                                    }
                                });
                            }else{
                                mIsUpload = true;
                            }

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(UploadActivity.this, "Get STS token failed");
                                }
                            });
                            Log.e(AliyunTag.TAG, "Get token info failed");
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String msg) {
                        super.onFailure(errorCode, msg);
                        Log.e(AliyunTag.TAG, "Get token info failed, errorCode:"+errorCode+", msg:"+msg);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(UploadActivity.this, "Get STS token failed");
                            }
                        });
                    }
                });
    }

    private final AliyunICompose.AliyunIUploadCallBack mUploadCallback = new AliyunICompose.AliyunIUploadCallBack() {
        @Override
        public void onUploadSucceed(String videoId, String imageUrl) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgress.setVisibility(View.GONE);
                    mProgressText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.aliyun_svideo_icon_composite_success, 0, 0, 0);
                    mProgressText.setText(R.string.upload_success);
                    mProgressText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressText.setVisibility(View.GONE);
                        }
                    }, 2000);
                }
            });

        }

        @Override
        public void onUploadFailed(String code, String message) {
            Log.e(AliyunTag.TAG, "onUploadFailed, errorCode:"+code+", msg:"+message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgress.setProgress(0);
                    mProgressText.setText(R.string.upload_failed);
                    mProgressText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressText.setVisibility(View.GONE);
                        }
                    }, 2000);
                }
            });

        }

        @Override
        public void onUploadProgress(final long uploadedSize, final long totalSize) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int progress = (int)((uploadedSize * 100)/totalSize);
                    mProgress.setProgress(progress);
                    mProgressText.setText("正在上传 " + progress + "%");
                }
            });

        }

        @Override
        public void onUploadRetry(String code, String message) {

        }

        @Override
        public void onUploadRetryResume() {

        }

        @Override
        public void onSTSTokenExpired() {
            HttpRequest.get("http://106.15.81.230/voddemo/CreateSecurityToken?BusinessType=vodai&TerminalType=pc&DeviceModel=iPhone9,2&UUID=67999yyuuuy&AppVersion=1.0.0",
                    new StringHttpRequestCallback(){
                        @Override
                        protected void onSuccess(String s) {
                            super.onSuccess(s);
                            SecurityTokenInfo info = getTokenInfo(s);
                            if(info != null) {
                                mComposeClient.refreshSTSToken(info.getAccessKeyId(),
                                        info.getAccessKeySecret(),
                                        info.getSecurityToken(),
                                        info.getExpiration());
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(UploadActivity.this, "Refresh STS token failed");
                                    }
                                });
                                Log.e(AliyunTag.TAG, "Get token info failed");
                            }
                        }
                    });
        }
    };

    private final TextureView.SurfaceTextureListener mlistener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            initVideoPlayer();
            try{
                mMediaPlayer.setDataSource(mVideoPath);
                mMediaPlayer.setSurface(new Surface(surface));
                mMediaPlayer.prepareAsync();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void initVideoPlayer(){
        if(mMediaPlayer != null){
            return ;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setScreenOnWhilePlaying(true);

        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setLooping(true);

    }

    private final MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            int vw = mp.getVideoWidth();
            int vh = mp.getVideoHeight();

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            mTextureView.getLayoutParams().width = screenWidth;
            mTextureView.getLayoutParams().height = (int)((float)vh * screenWidth / vw);
        }
    };

}
