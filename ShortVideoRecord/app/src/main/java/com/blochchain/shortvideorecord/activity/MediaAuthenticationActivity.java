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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.response.BaseResponse;
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

public class MediaAuthenticationActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MediaAuthenticationActivity.class.getSimpleName();
    private ImageView iv_back;
    private TextView tv_edite;  //编辑
    private EditText et_name;  //姓名
    private EditText et_card;  //身份证号
    private TextView tv_take_card;  //拍摄身份证
    private ImageView iv_card;  //身份证照片
    private TextView tv_take_hand_card; //拍摄手持身份证照
    private ImageView iv_hand_card;  //手持身份证照

    private String image_card_url;  //身份证照片url
    private String image_hand_card_url;  //手持身份证照url
    private String real_name;
    private String id_card;
    /**
     * 照相选择界面
     */
    private PopupWindow pWindow;
    private View root;
    private LayoutInflater mInflater;
    private String timepath;
    private LinearLayout ll_layout;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;
    private static final int UPLOADPICTURESUCCESS = 1;
    private static final int UPLOADPICTUREFAILED = 2;
    private static final int EDITESELFSUCESS = 3;
    private static final int EDITESELFFAILD = 4;
    /**
     * 照片参数
     */
    private static final int PHOTO_GRAPH = 4;// 拍照
    private static final int PHOTO_ZOOM = 5; // 缩放
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String path;
    private String suoName;
    private String self_media_id;

    // 图片储存成功后
    protected static final int INTERCEPT = 6;
    private static final int TAKE_PHOTO_REQUEST_CODE = 10;
    private int type; //0 拍摄身份证 ，   1 拍摄手持身份证
    private boolean isEdite = true;  //true 编辑    false 保存
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            File file = new File(path);
            switch (msg.what) {
                case INTERCEPT:
                    uploadImage();
//                    switch (type){
//                        case 0: //拍摄身份证
//                            Picasso.with(MediaAuthenticationActivity.this).load(file).into(iv_card);
//                            break;
//                        case 1: //拍摄手持身份证
//                            Picasso.with(MediaAuthenticationActivity.this).load(file).into(iv_hand_card);
//                            break;
//                    }
                    break;
                case UPLOADPICTURESUCCESS: //上传图片成功
                    String string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "上传图片成功：" + string);
                    UploadImageResponse uploadImageResponse = gson.fromJson(string, UploadImageResponse.class);
                    if (uploadImageResponse != null) {
                        if (uploadImageResponse.getCode() == 0) {
                            switch (type) {
                                case 0: //拍摄身份证
                                    image_card_url = uploadImageResponse.getPic_path();
                                    Picasso.with(MediaAuthenticationActivity.this).load(image_card_url).into(iv_card);
                                    break;
                                case 1: //拍摄手持身份证
                                    image_hand_card_url = uploadImageResponse.getPic_path();
                                    Picasso.with(MediaAuthenticationActivity.this).load(image_hand_card_url).into(iv_hand_card);
                                    break;
                            }
                        } else {
                            UIUtils.showToastCenter(MediaAuthenticationActivity.this, uploadImageResponse.getMsg());
                        }
                    }
                    break;
                case UPLOADPICTUREFAILED:  //上传图片失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "上传图片失败：" + e1.getMessage());
                    break;
                case EDITESELFSUCESS: //编辑自媒体信息
                    String string1 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "编辑自媒体信息成功：" + string1);
                    BaseResponse baseResponse = gson.fromJson(string1, BaseResponse.class);
                    if (baseResponse != null) {
                        if (baseResponse.getCode() == 0) {
//                            isEdite = true;
//                            tv_edite.setText("编辑");
//                            tv_take_card.setText("拍摄");
//                            tv_take_hand_card.setText("拍摄");
//                            et_card.setEnabled(false);
//                            et_name.setEnabled(false);
                            setResult(200);
                            finish();
                        } else {
                            UIUtils.showToastCenter(MediaAuthenticationActivity.this, baseResponse.getMsg());
                        }
                    }
                    break;
                case EDITESELFFAILD:
                    IOException e2 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "编辑自媒体信息失败：" + e2.getMessage());
                    break;
            }
        }
    };


    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_media_authentication, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        ll_layout = findViewById(R.id.ll_layout);
        tv_edite = findViewById(R.id.tv_edite);
        et_name = findViewById(R.id.et_name);
        et_card = findViewById(R.id.et_card);
        tv_take_card = findViewById(R.id.tv_take_card);
        iv_card = findViewById(R.id.iv_card);
        tv_take_hand_card = findViewById(R.id.tv_take_hand_card);
        iv_hand_card = findViewById(R.id.iv_hand_card);

        iv_back.setOnClickListener(this);
        tv_edite.setOnClickListener(this);
        tv_take_hand_card.setOnClickListener(this);
        tv_take_card.setOnClickListener(this);
        initData();
        return rootView;
    }

    private void uploadImage() {
        loadingDialog.show();
        Map<String, String> hs = new ConcurrentSkipListMap();
        File file = new File(path);
        netUtils.uploadFile(Constants.UploadPictureUrl, file, suoName + ".jpg", "pic_string", hs, new Callback() {
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

    private void initData() {
        Intent intent = getIntent();
        real_name = intent.getStringExtra("real_name");
        id_card = intent.getStringExtra("id_card");
        image_card_url = intent.getStringExtra("id_card_pic");
        image_hand_card_url = intent.getStringExtra("id_card_hold_pic");
        if (!TextUtils.isEmpty(real_name)) {
            et_name.setText(real_name);
        }
        if (!TextUtils.isEmpty(id_card)) {
            et_card.setText(id_card);
        }
        if (!TextUtils.isEmpty(image_card_url)) {
            Picasso.with(this).load(image_card_url).into(iv_card);
        }
        if (!TextUtils.isEmpty(image_hand_card_url)) {
            Picasso.with(this).load(image_hand_card_url).into(iv_hand_card);
        }
        et_name.setEnabled(false);
        et_card.setEnabled(false);

        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);

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
                        uri = BitmapUtils.getPictureUri(data, MediaAuthenticationActivity.this);
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

                    if (ContextCompat.checkSelfPermission(MediaAuthenticationActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) MediaAuthenticationActivity.this,
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
                    UIUtils.showToastCenter(MediaAuthenticationActivity.this,"请开启应用拍照权限");
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_edite:
                if (isEdite) {
                    isEdite = false;
                    tv_edite.setText("保存");
                    tv_take_card.setText("重拍");
                    tv_take_hand_card.setText("重拍");
                    et_card.setEnabled(true);
                    et_name.setEnabled(true);
                } else {
//                    isEdite = true;
//                    tv_edite.setText("编辑");
//                    tv_take_card.setText("拍摄");
//                    tv_take_hand_card.setText("拍摄");
//                    et_card.setEnabled(false);
//                    et_name.setEnabled(false);
                    real_name = et_name.getText().toString();
                    if(TextUtils.isEmpty(real_name)){
                        UIUtils.showToastCenter(MediaAuthenticationActivity.this,"请输入真实姓名");
                        return;
                    }
                    id_card = et_card.getText().toString();
                    if(TextUtils.isEmpty(id_card)){
                        UIUtils.showToastCenter(MediaAuthenticationActivity.this,"请输入身份证号");
                        return;
                    }
                    if(TextUtils.isEmpty(image_card_url)){
                        UIUtils.showToastCenter(MediaAuthenticationActivity.this,"请拍摄身份证照片");
                        return;
                    }
                    if(TextUtils.isEmpty(image_hand_card_url)){
                        UIUtils.showToastCenter(MediaAuthenticationActivity.this,"请拍摄手持身份证照片");
                        return;
                    }
                    saveMessage();
                }
                break;
            case R.id.tv_take_card:  //拍摄身份证
                type = 0;
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(ll_layout,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_take_hand_card: //拍摄手持身份证
                type = 1;
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(ll_layout,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
        }
    }

    private void saveMessage() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("self_media_id", self_media_id);
        reqBody.put("real_name", real_name);
        reqBody.put("id_card", id_card);
        reqBody.put("id_card_pic", image_card_url);
        reqBody.put("id_card_hold_pic", image_hand_card_url);
        netUtils.postDataAsynToNet(Constants.EditSelfMediaInfUrl, reqBody, new NetUtils.MyNetCall() {

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = EDITESELFSUCESS;
                message.obj = str;
                handler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = EDITESELFFAILD;
                message.obj = e;
                handler.sendMessage(message);
            }
        });
    }
}
