package com.blochchain.shortvideorecord.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blochchain.shortvideorecord.adapter.ClassAdapter;
import com.blochchain.shortvideorecord.adapter.DictionaryAdapter;
import com.blochchain.shortvideorecord.model.ClassModel;
import com.blochchain.shortvideorecord.model.DictionaryBean;
import com.blochchain.shortvideorecord.utils.BitmapUtils;
import com.blochchain.shortvideorecord.utils.MobileUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blockchain.shortvideorecord.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PerfectInformationActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_back;
    private ImageView iv_logo;
    private EditText et_describe;  //自媒体简介
    private LinearLayout ll_class;  //所属类目
    private TextView tv_class; //显示类目
    private EditText et_name;  //姓名
    private EditText et_idcard;  //身份证号码
    private TextView tv_take_card;  //拍摄身份证
    private ImageView iv_card;  //身份证照片
    private TextView tv_take_hand_card;  //拍摄手持身份证照片
    private ImageView iv_hand_card;  //手持身份证照片
    private TextView tv_submit;  //提交认证

    private ArrayList<ClassModel> classModelArrayList;
    private PopupWindow dictonaryWindow;
    private View dictonaryRoot;
    private ListView lv_choose;
    private Button btn_cancel;
    private TextView tv_title;
    private TextView tv_complete;
    private ClassModel classModel;
    private int oldPosition;
    private String dictionaryTitle;



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
    // 图片储存成功后
    protected static final int INTERCEPT = 6;
    private int type ; //0 拍摄身份证 ，   1 拍摄手持身份证 ,  2拍摄logo
    private boolean isEdite = true;  //true 编辑    false 保存
    private static final int TAKE_PHOTO_REQUEST_CODE = 10;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            File file = new File(path);
            switch (msg.what) {
                case INTERCEPT:
//                    uploadImage(path);
                    switch (type){
                        case 0: //拍摄身份证
                            Picasso.with(PerfectInformationActivity.this).load(file).into(iv_card);
                            break;
                        case 1: //拍摄手持身份证
                            Picasso.with(PerfectInformationActivity.this).load(file).into(iv_hand_card);
                            break;
                        case 2: //拍摄logo
                            Picasso.with(PerfectInformationActivity.this).load(file).into(iv_logo);
                            break;
                    }
                    break;
            }
        }
    };
    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_perfect_information, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        ll_layout = findViewById(R.id.ll_layout);
        iv_logo = findViewById(R.id.iv_logo);
        et_describe = findViewById(R.id.et_describe);
        ll_class = findViewById(R.id.ll_class);
        tv_class = findViewById(R.id.tv_class);
        et_name = findViewById(R.id.et_name);
        et_idcard = findViewById(R.id.et_idcard);
        tv_take_card = findViewById(R.id.tv_take_card);
        iv_card = findViewById(R.id.iv_card);
        tv_take_hand_card = findViewById(R.id.tv_take_hand_card);
        iv_hand_card = findViewById(R.id.iv_hand_card);
        tv_submit = findViewById(R.id.tv_submit);

        iv_back.setOnClickListener(this);
        iv_logo.setOnClickListener(this);
        ll_class.setOnClickListener(this);
        tv_take_card.setOnClickListener(this);
        tv_take_hand_card.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        initData();
        return rootView;
    }

    private void initData() {
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

        dictonaryRoot = mInflater.inflate(R.layout.choose_dialog, null);
        dictonaryWindow = new PopupWindow(dictonaryRoot, ActionBar.LayoutParams.FILL_PARENT,
                ActionBar.LayoutParams.FILL_PARENT);
        dictonaryWindow.setFocusable(true);
        lv_choose = (ListView) dictonaryRoot.findViewById(R.id.lv_choose);
        btn_cancel = (Button) dictonaryRoot.findViewById(R.id.btn_cancel);
        tv_title = (TextView) dictonaryRoot.findViewById(R.id.tv_title);
        tv_complete = (TextView) dictonaryRoot.findViewById(R.id.tv_complete);

        tv_complete.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        lv_choose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        classModel = classModelArrayList.get(position);
                        tv_class.setText(classModel.getClass_name());
                        oldPosition = lv_choose.getFirstVisiblePosition();
                dictonaryWindow.dismiss();
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
                    if(photo != null){
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        Bitmap new_photo = BitmapUtils.rotateBitmapByDegree(photo, BitmapUtils.getBitmapDegree(path));
                        String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
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
            if(data.getData()!=null){
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
                        uri = BitmapUtils.getPictureUri(data, PerfectInformationActivity.this);
                        cursor = managedQuery(uri, proj, null, null, null);
                    }
                    if(cursor != null){
                        // 获取索引
                        int photocolumn = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        // 将光标一直开头
                        cursor.moveToFirst();
                        // 根据索引值获取图片路径
                        path = cursor.getString(photocolumn);
                    }else {
                        path = uri.getPath();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (cursor != null) {
                        if(MobileUtils.getSDKVersionNumber() < 14) {
                            cursor.close();
                        }
                    }
                }

                if (!TextUtils.isEmpty(path)) {
                    new Thread() {
                        public void run() {
                            try {
                                Bitmap photo = BitmapUtils.getSmallBitmap(path);
                                if(photo != null){
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    Bitmap new_photo = BitmapUtils.rotateBitmapByDegree(photo, BitmapUtils.getBitmapDegree(path));
                                    String suoName = new SimpleDateFormat("yyyyMMdd_HHmmss")
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
                    if (ContextCompat.checkSelfPermission(PerfectInformationActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) PerfectInformationActivity.this,
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
                    UIUtils.showToastCenter(PerfectInformationActivity.this,"请开启应用拍照权限");
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
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_logo:
                type = 2;
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(ll_layout,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.ll_class:
                dictionaryTitle = "类目";
                getDictonary();
                break;
            case R.id.tv_take_card:
                type = 0;
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(ll_layout,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_take_hand_card:
                type = 1;
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(ll_layout,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.tv_submit:
                break;
            case R.id.btn_cancel:
            case R.id.tv_complete:
                dictonaryWindow.dismiss();
                break;
        }
    }

    private void getDictonary() {
        classModelArrayList = new ArrayList<>();
               ClassModel classModel = new ClassModel();
               classModel.setClass_id("1");
               classModel.setClass_name("农家野食");
               classModelArrayList.add(classModel);
               ClassModel classModel1 = new ClassModel();
               classModel1.setClass_id("2");
               classModel1.setClass_name("吃货大神");
               classModelArrayList.add(classModel1);
        ClassAdapter classAdapter = new ClassAdapter(PerfectInformationActivity.this, classModelArrayList);
        lv_choose.setAdapter(classAdapter);
        if (!TextUtils.isEmpty(dictionaryTitle)) {
            tv_title.setText(dictionaryTitle);
        }
        dictonaryWindow.showAtLocation((LinearLayout) findViewById(R.id.ll_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
}
