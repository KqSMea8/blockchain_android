package com.blochchain.shortvideorecord.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;
import com.aliyun.common.global.AliyunTag;
import com.aliyun.common.utils.ToastUtil;
import com.aliyun.demo.recorder.VideoPlayActivity;
import com.aliyun.qupaiokhttp.HttpRequest;
import com.aliyun.qupaiokhttp.StringHttpRequestCallback;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.response.BaseResponse;
import com.blochchain.shortvideorecord.response.GetStsTokenResponse;
import com.blochchain.shortvideorecord.response.UploadImageResponse;
import com.blochchain.shortvideorecord.utils.BitmapUtils;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.MobileUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UploadActivity extends BaseActivity {
    private static final String TAG = UploadActivity.class.getSimpleName();
    private TextView mProgressText;
    private VODSVideoUploadClient vodsVideoUploadClient;
    private VodHttpClientConfig vodHttpClientConfig;
    private long progress;
    private String videoPath;
    private Gson gson;
    //接口请求菊花
    private Dialog loadingDialog;
    private EditText et_title;
    private EditText et_describe;
    private String title;
    private String describe;

    /**
     * 照相选择界面
     */
    private PopupWindow pWindow;
    private View root;
    private LayoutInflater mInflater;
    private String timepath;
    private LinearLayout ll_layout;
    /**
     * 照片参数
     */
    private static final int PHOTO_GRAPH = 4;// 拍照
    private static final int PHOTO_ZOOM = 5; // 缩放
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String path;
    private String suoName;
    private ImageView iv_thumbnail;
    private TextView tv_upload;
    private NetUtils netUtils;
    private static final int UPLOADTOSERVERSUCCESS = 1;
    private static final int UPLOADTOSERVERFAILED = 2;
    private static final int UPLOADPICTURESUCCESS = 3;
    private static final int UPLOADPICTUREFAILED = 4;

    private static final int UPLOADTOALISUCCESS = 100;  //上传到阿里云成功
    private String thumbnail;  //封面图
    private String video_id;  //上传视频到阿里云返回视频ID
    private String video_url;  //上传视频到阿里云返回视频url
    private String self_media_id;
    private static final int TAKE_PHOTO_REQUEST_CODE = 10;

    // 图片储存成功后
    protected static final int INTERCEPT = 6;
    private BaseActivity.MyHandler handler = new BaseActivity.MyHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(progress != 100){
                        mProgressText.setText("正在上传 " + progress + "%");
                        mProgressText.setVisibility(View.VISIBLE);
                    }else {
                        mProgressText.setVisibility(View.GONE);
                    }
                    break;
                case INTERCEPT:
//                    uploadImage();
                    File file = new File(path);
                    Picasso.with(UploadActivity.this).load(file).into(iv_thumbnail);
                    break;
                case UPLOADTOSERVERSUCCESS: //上传作品
                    String string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "上传作品成功：" + string);
                    BaseResponse baseResponse = gson.fromJson(string,BaseResponse.class);
                    if(baseResponse != null){
                        if(baseResponse.getCode() == 0){
                            finish();
                        }else {
                            UIUtils.showToastCenter(UploadActivity.this,baseResponse.getMsg());
                        }
                    }
                    break;
                case UPLOADTOSERVERFAILED:  //上传作品失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "上传作品失败：" + e1.getMessage());
                    UIUtils.showToastCenter(UploadActivity.this, e1.getMessage());
                    break;
                case UPLOADTOALISUCCESS:
                    uploadToServer();
                    break;
                case UPLOADPICTURESUCCESS: //上传图片成功
                    String string1 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"上传图片成功："+string1);
                    UploadImageResponse uploadImageResponse = gson.fromJson(string1,UploadImageResponse.class);
                    if(uploadImageResponse != null){
                        if(uploadImageResponse.getCode() == 0){
                            thumbnail = uploadImageResponse.getPic_path();
                            if(!TextUtils.isEmpty(thumbnail)){
                                Picasso.with(UploadActivity.this).load(thumbnail).into(iv_thumbnail);
                            }
                        }else {
                            UIUtils.showToastCenter(UploadActivity.this,uploadImageResponse.getMsg());
                        }
                    }
                    break;
                case UPLOADPICTUREFAILED:  //上传图片失败
                    IOException e2 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"上传图片失败："+e2.getMessage());
                    UIUtils.showToastCenter(UploadActivity.this,e2.getMessage());
                    break;
            }

        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_upload, null);
        setContentView(rootView);

        ll_layout = findViewById(R.id.ll_layout);
        et_title = findViewById(R.id.et_title);
        et_describe = findViewById(R.id.et_title);
        mProgressText = findViewById(R.id.progress_text);
        iv_thumbnail = findViewById(R.id.iv_thumbnail);
        tv_upload = findViewById(R.id.tv_upload);
        initData();
        return rootView;
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");
        videoPath = getIntent().getStringExtra(VideoPlayActivity.VIDEO_PATH);
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        vodsVideoUploadClient = new VODSVideoUploadClientImpl(this.getApplicationContext());
        vodsVideoUploadClient.init();
        vodHttpClientConfig = new VodHttpClientConfig.Builder()
                .setMaxRetryCount(2)//重试次数
                .setConnectionTimeout(15 * 1000)//连接超时
                .setSocketTimeout(15 * 1000)//socket超时
                .build();

        if (mInflater == null) {
            mInflater = (LayoutInflater) UIUtils.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }
        root = mInflater.inflate(R.layout.alert_dialog, null);
        pWindow = new PopupWindow(root, ActionBar.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.FILL_PARENT);

        root.findViewById(R.id.btn_Phone).setOnClickListener(itemsOnClick);
        root.findViewById(R.id.btn_TakePicture)
                .setOnClickListener(itemsOnClick);
        root.findViewById(R.id.bg_photo).getBackground().setAlpha(100);
        root.findViewById(R.id.btn_cancel).setOnClickListener(itemsOnClick);

        tv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(path)) {
                    UIUtils.showToastCenter(UploadActivity.this, "请先拍摄封面");
                    return;
                }
                title = et_title.getText().toString();
                if(TextUtils.isEmpty(title)){
                    UIUtils.showToastCenter(UploadActivity.this,"请输入标题");
                    return;
                }
                describe = et_describe.getText().toString();
                if(TextUtils.isEmpty(describe)){
                    UIUtils.showToastCenter(UploadActivity.this,"请输入简介");
                    return;
                }
                getStsToken();
            }
        });

        iv_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(ll_layout,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_GRAPH) {
            // 设置文件保存路径
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            path = dir + "/" + timepath + ".jpg";
            File file = new File(path);
            if (file.exists()) {
                try {
                    Bitmap photo = BitmapUtils.getSmallBitmap(path);
                    if (photo != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        Bitmap new_photo = BitmapUtils.rotateBitmapByDegree(photo, BitmapUtils.getBitmapDegree(path));
                        suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                .format(new Date());
                        path = BitmapUtils.saveMyBitmap(suoName, new_photo);
                        handler.sendEmptyMessage(INTERCEPT);

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            //通知相册刷新
            Uri uriData = Uri.parse("file://" + path);
            UIUtils.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriData));
        }

        // 读取相册缩放图片
        if (requestCode == PHOTO_ZOOM && data != null) {
            if (data.getData() != null) {
                // 图片信息需包含在返回数据中
                String[] proj = {MediaStore.Images.Media.DATA};
                // 获取包含所需数据的Cursor对象
                @SuppressWarnings("deprecation")
                Uri uri = data.getData();
                // 获取包含所需数据的Cursor对象
                @SuppressWarnings("deprecation")
                Cursor cursor = null;
                try {
                    cursor = managedQuery(uri, proj, null, null, null);
                    if (cursor == null) {
                        uri = BitmapUtils.getPictureUri(data, UploadActivity.this);
                        cursor = managedQuery(uri, proj, null, null, null);
                    }
                    if (cursor != null) {
                        // 获取索引
                        int photocolumn = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        // 将光标一直开头
                        cursor.moveToFirst();
                        // 根据索引值获取图片路径
                        path = cursor.getString(photocolumn);
                    } else {
                        path = uri.getPath();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        if (MobileUtils.getSDKVersionNumber() < 14) {
                            cursor.close();
                        }
                    }
                }

                if (!TextUtils.isEmpty(path)) {
                    new Thread() {
                        public void run() {
                            try {
                                Bitmap photo = BitmapUtils.getSmallBitmap(path);
                                if (photo != null) {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    Bitmap new_photo = BitmapUtils.rotateBitmapByDegree(photo, BitmapUtils.getBitmapDegree(path));
                                    suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                                            .format(new Date());
                                    path = BitmapUtils.saveMyBitmap(suoName, new_photo);
                                    handler.sendEmptyMessage(INTERCEPT);

                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        ;
                    }.start();

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getStsToken() {
        loadingDialog.show();
        HttpRequest.post(Constants.GetStsToken, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);
                LogUtils.e(TAG + "-getSts:" + s);
                GetStsTokenResponse getStsTokenResponse = gson.fromJson(s, GetStsTokenResponse.class);
                if (getStsTokenResponse != null) {
                    if (getStsTokenResponse.getCode() == 0) {
                        uploadVideo(getStsTokenResponse);
                    } else {
                        LogUtils.e("获取sts失败:" + getStsTokenResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                loadingDialog.dismiss();
                super.onFailure(errorCode, msg);
                Log.e(AliyunTag.TAG, "Get token info failed, errorCode:" + errorCode + ", msg:" + msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(UploadActivity.this, "Get STS token failed");
                    }
                });
            }
        });

    }

    private void uploadVideo(GetStsTokenResponse getStsTokenResponse) {
        final String accessKeyId = getStsTokenResponse.getAccessKeyId();
        final String accessKeySecret = getStsTokenResponse.getAccessKeySecret();
        final String securityToken = getStsTokenResponse.getSecurityToken();
        final String expriedTime = getStsTokenResponse.getExpiration();
        LogUtils.e(TAG + ",videoPath" + videoPath);
        LogUtils.e(TAG + "isFileExit:" + new File(videoPath).exists());
        SvideoInfo svideoInfo = new SvideoInfo();
        svideoInfo.setTitle(new File(videoPath).getName());
        svideoInfo.setDesc("");
        svideoInfo.setCateId(1);
        //构建点播上传参数(重要)
        VodSessionCreateInfo vodSessionCreateInfo = new VodSessionCreateInfo.Builder()
                .setImagePath(path)//图片地址
                .setVideoPath(videoPath)//视频地址
                .setAccessKeyId(accessKeyId)//临时accessKeyId
                .setAccessKeySecret(accessKeySecret)//临时accessKeySecret
                .setSecurityToken(securityToken)//securityToken
                .setExpriedTime(expriedTime)//STStoken过期时间
//                .setRequestID(requestID)//requestID，开发者可以传将获取STS返回的requestID设置也可以不设.
                .setIsTranscode(true)//是否转码.如开启转码请AppSever务必监听服务端转码成功的通知
                .setSvideoInfo(svideoInfo)//短视频视频信息
                .setVodHttpClientConfig(vodHttpClientConfig)//网络参数
                .build();

        vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
            @Override
            public void onUploadSucceed(String videoId, String imageUrl) {
//上传成功返回视频ID和图片URL.
                LogUtils.e(TAG + "-onUploadSucceed-" + "videoId:" + videoId + "-imageUrl:" + imageUrl);
                video_id = videoId;
                thumbnail = imageUrl;
                handler.sendEmptyMessage(UPLOADTOALISUCCESS);
            }

            @Override
            public void onUploadFailed(String code, String message) {
                loadingDialog.dismiss();
                //上传失败返回错误码和message.错误码有详细的错误信息请开发者仔细阅读
                LogUtils.e(TAG + "---onUploadFailed-" + "code:" + code + ",message:" + message);
            }

            @Override
            public void onUploadProgress(long uploadedSize, long totalSize) {
                //上传的进度回调,非UI线程
                LogUtils.e(TAG + "onUploadProgress" + uploadedSize * 100 / totalSize);
                progress = uploadedSize * 100 / totalSize;
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onSTSTokenExpried() {
                LogUtils.e(TAG + "onSTSTokenExpried");
                //STS token过期之后刷新STStoken，如正在上传将会断点续传
                vodsVideoUploadClient.refreshSTSToken(accessKeyId, accessKeySecret, securityToken, expriedTime);
            }

            @Override
            public void onUploadRetry(String code, String message) {
                //上传重试的提醒
                LogUtils.e(TAG + "onUploadRetry" + "code" + code + "message" + message);
            }

            @Override
            public void onUploadRetryResume() {
                //上传重试成功的回调.告知用户重试成功
                LogUtils.e(TAG + "onUploadRetryResume");
            }
        });
    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            pWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_TakePicture: {
//                    timepath = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                        if (!dir.exists()) {
//                            dir.mkdir();
//                        }
//                        File file = new File(dir, timepath + ".jpg");
//                        UIUtils.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
//                                MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)), PHOTO_GRAPH);
//                    }
                    if (ContextCompat.checkSelfPermission(UploadActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) UploadActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                TAKE_PHOTO_REQUEST_CODE);
                    }else {

                        takePhoto();
                    }
                    break;
                }
                case R.id.btn_Phone: {
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            IMAGE_UNSPECIFIED);
                    UIUtils.startActivityForResult(intent, PHOTO_ZOOM);
                    break;
                }
                case R.id.btn_cancel: {
                    pWindow.dismiss();
                    break;
                }
                default:
                    break;
            }

        }

    };

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case TAKE_PHOTO_REQUEST_CODE:
                boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted){
                    //授权成功之后，调用系统相机进行拍照操作等
//                    Camera.startCameraUrl(context, filename, CAMERA);
                    takePhoto();
                }else{
                    //用户授权拒绝之后，友情提示一下就可以了
                    UIUtils.showToastCenter(UploadActivity.this,"请开启应用拍照权限");
                }
                break;
        }
    }

    private void takePhoto() {
        timepath = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, timepath + ".jpg");

            Uri photoURI;
            if (Build.VERSION.SDK_INT >= 24) {
                photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",file);
            } else {
                photoURI = Uri.fromFile(file);
            }

            UIUtils.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                    MediaStore.EXTRA_OUTPUT, photoURI), PHOTO_GRAPH);
//            UIUtils.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
//                    MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)), PHOTO_GRAPH);
        }
    }

    /**
     * 上传作品到我们自己的服务器
     */
    private void uploadToServer() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if (!TextUtils.isEmpty(self_media_id)) {
            reqBody.put("self_media_id", self_media_id);
        }
        if (!TextUtils.isEmpty(title)) {
            reqBody.put("video_title", title);
        }
        if (!TextUtils.isEmpty(describe)) {
            reqBody.put("video_dec", describe);
        }
        if (!TextUtils.isEmpty(thumbnail)) {
            reqBody.put("video_pic", thumbnail);
        }
        if (!TextUtils.isEmpty(video_id)) {
            reqBody.put("v_id", video_id);
        }
        LogUtils.e(TAG+"上传作品到服务器参数："+reqBody.toString());
        netUtils.postDataAsynToNet(Constants.AddVideoUrl, reqBody, new NetUtils.MyNetCall() {
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

    private void uploadImage() {
        loadingDialog.show();
        Map<String,String> hs = new ConcurrentSkipListMap();
        File file = new File(path);
        netUtils.uploadFile(Constants.UploadPictureUrl, file, suoName+".jpg","pic_string", hs, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = UPLOADPICTUREFAILED;
                message.obj = e;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = UPLOADPICTURESUCCESS;
                message.obj = str;
                handler.sendMessage(message);
            }
        });
    }
}
