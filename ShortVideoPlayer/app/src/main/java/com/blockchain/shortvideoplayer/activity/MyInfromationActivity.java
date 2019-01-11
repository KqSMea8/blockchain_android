package com.blockchain.shortvideoplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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

import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.adapter.DictionaryAdapter;
import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.model.DictionaryBean;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.UploadImageResponse;
import com.blockchain.shortvideoplayer.utils.BitmapUtils;
import com.blockchain.shortvideoplayer.utils.DialogUtils;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.MobileUtils;
import com.blockchain.shortvideoplayer.utils.NetUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.CircleImageView;
import com.blockchain.shortvideoplayer.widget.datapicker.CustomDatePicker;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.app.ActionBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyInfromationActivity extends BaseActivity implements View.OnClickListener{
    private CircleImageView iv_head;

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

    private CustomDatePicker customDatePicker1, customDatePicker2;

    private LinearLayout ll_birthday;  //生日
    private TextView tv_birthday;
    private EditText et_nickname;  //昵称
    private EditText et_describe;  //自我介绍
    private LinearLayout ll_sex; //性别
    private TextView tv_sex;
    private LinearLayout ll_education;  //学历
    private TextView tv_education;
    private int type;
    private ArrayList<DictionaryBean> dictionaryBeanList;
    private PopupWindow dictonaryWindow;
    private View dictonaryRoot;
    private ListView lv_choose;
    private Button btn_cancel;
    private TextView tv_title;
    private TextView tv_complete;
    private DictionaryBean sexDictionary;
    private DictionaryBean educationDictionary;
    private int oldPosition;
    private String dictionaryTitle;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final int EDITEUSERINFOSUCCESS = 0;
    private static final int EDITEUSERINFOFAILED = 1;
    private static final int UPLOADPICTURESUCCESS = 2;
    private static final int UPLOADPICTUREFAILED = 3;
    private static final String TAG = MyInfromationActivity.class.getSimpleName();
    private String user_id;
    private String suoName;

    private ImageView iv_back;
    private AlertDialog saveDialog;
    private String nickname;
    private String describe;
    private String headpic;
    private int sex;
    private String birthday;
    private int education_id;

    // 图片储存成功后
    protected static final int INTERCEPT = 6;
    private static final int TAKE_PHOTO_REQUEST_CODE = 10;
    private MyHandler handler = new MyHandler(this) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case INTERCEPT:
                    uploadImage();
//                    File file = new File(path);
//                    Picasso.with(MyInfromationActivity.this).load(file).into(iv_head);
                    break;
                case EDITEUSERINFOSUCCESS: //修改用户信息成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"修改用户信息成功："+str);
                    BaseResponse baseResponse = gson.fromJson(str,BaseResponse.class);
                    if(baseResponse != null ){
                        if(baseResponse.getCode() ==0){
                            setResult(200);
                            finish();
                        }else {
                            UIUtils.showToastCenter(MyInfromationActivity.this,baseResponse.getMsg());
                        }
                    }
                    break;
                case EDITEUSERINFOFAILED: //修改用户信息失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"修改用户信息失败："+e.getMessage());
                    UIUtils.showToastCenter(MyInfromationActivity.this,e.getMessage());
                    break;
                case UPLOADPICTURESUCCESS: //上传图片成功
                    String string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"上传图片成功："+string);
                    UploadImageResponse uploadImageResponse = gson.fromJson(string,UploadImageResponse.class);
                    if(uploadImageResponse != null){
                        if(uploadImageResponse.getCode() == 0){
                            headpic = uploadImageResponse.getPic_path();
                            if(!TextUtils.isEmpty(headpic)){
                                Picasso.with(MyInfromationActivity.this).load(headpic).into(iv_head);
                            }
                        }else {
                            UIUtils.showToastCenter(MyInfromationActivity.this,uploadImageResponse.getMsg());
                        }
                    }
                    break;
                case UPLOADPICTUREFAILED:  //上传图片失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"上传图片失败："+e1.getMessage());
                    UIUtils.showToastCenter(MyInfromationActivity.this,e1.getMessage());
                    break;

            }
        }
    };

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




    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_my_infromation, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        iv_head = findViewById(R.id.iv_head);
        ll_layout = findViewById(R.id.ll_layout);
        ll_birthday = findViewById(R.id.ll_birthday);
        tv_birthday = findViewById(R.id.tv_birthday);
        et_nickname = findViewById(R.id.et_nickname);
        et_describe = findViewById(R.id.et_describe);
        ll_sex = findViewById(R.id.ll_sex);
        tv_sex = findViewById(R.id.tv_sex);
        ll_education = findViewById(R.id.ll_education);
        tv_education = findViewById(R.id.tv_education);

        user_id = SharedPrefrenceUtils.getString(this,"usrs_id");
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

        iv_back.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        ll_birthday.setOnClickListener(this);
        ll_sex.setOnClickListener(this);
        ll_education.setOnClickListener(this);

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
                switch (type) {
                    case 0: //性别
                        sexDictionary = dictionaryBeanList.get(position);
                        tv_sex.setText(sexDictionary.getDictionary_value_name());
                        oldPosition = lv_choose.getFirstVisiblePosition();
                        break;
                    case 1://学历
                        educationDictionary = dictionaryBeanList.get(position);
                        tv_education.setText(educationDictionary.getDictionary_value_name());
                        oldPosition = lv_choose.getFirstVisiblePosition();
                        break;
                }
                dictonaryWindow.dismiss();
            }
        });

        initDatePicker();
        initData();
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        nickname = intent.getStringExtra("nickname");
        describe = intent.getStringExtra("describe");
        headpic = intent.getStringExtra("headpic");
        sex = intent.getIntExtra("sex",-1);
        birthday = intent.getStringExtra("birthday");
        education_id = intent.getIntExtra("education_id",-1);

        if(!TextUtils.isEmpty(nickname)){
            et_nickname.setText(nickname);
        }
        if(!TextUtils.isEmpty(describe)){
            et_describe.setText(describe);
        }
        if(!TextUtils.isEmpty(headpic)){
            Picasso.with(this).load(headpic).into(iv_head);
        }
        if(sex ==0){
            tv_sex.setText("男");
        }else if(sex ==1){
            tv_sex.setText("女");
        }
        if(!TextUtils.isEmpty(birthday)){
            tv_birthday.setText(birthday);
        }
        switch (education_id){
            case 1:
                tv_education.setText("初中");
                break;
            case 2:
                tv_education.setText("高中");
                break;
            case 3:
                tv_education.setText("专科");
                break;
            case 4:
                tv_education.setText("本科及以上");
                break;
        }

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
                        uri = BitmapUtils.getPictureUri(data, MyInfromationActivity.this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_head:
                pWindow.setAnimationStyle(R.style.AnimBottom);
                pWindow.showAtLocation(ll_layout,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.ll_birthday:
                customDatePicker1.show(tv_birthday.getText().toString());
                break;
            case R.id.ll_sex:
                dictionaryTitle = "性别";
                type=0;
                getDictonary();
                break;
            case R.id.ll_education:
                dictionaryTitle = "学历";
                type=1;
                getDictonary();
                break;
            case R.id.tv_complete:
            case R.id.btn_cancel:
                dictonaryWindow.dismiss();
                break;
            case R.id.iv_back:
                saveDialog = DialogUtils.showAlertDoubleBtnDialog(MyInfromationActivity.this,null,this);
                break;
            case R.id.tv_cancle:
                saveDialog.dismiss();
                finish();
                break;
            case R.id.tv_ensure:
                saveDialog.dismiss();
                editeUserInfo();
                break;
        }

    }

    private void getDictonary() {
        dictionaryBeanList = new ArrayList<>();
        switch (type){
            case 0:
                DictionaryBean dictionaryBean = new DictionaryBean();
                dictionaryBean.setDictionary_value_key(0);
                dictionaryBean.setDictionary_value_name("男");
                dictionaryBeanList.add(dictionaryBean);
                DictionaryBean dictionaryBean1 = new DictionaryBean();
                dictionaryBean1.setDictionary_value_name("女");
                dictionaryBean1.setDictionary_value_key(1);
                dictionaryBeanList.add(dictionaryBean1);
                break;
            case 1:
                DictionaryBean dictionaryBean2 = new DictionaryBean();
                dictionaryBean2.setDictionary_value_key(1);
                dictionaryBean2.setDictionary_value_name("初中");
                dictionaryBeanList.add(dictionaryBean2);
                DictionaryBean dictionaryBean3 = new DictionaryBean();
                dictionaryBean3.setDictionary_value_name("高中");
                dictionaryBean3.setDictionary_value_key(2);
                dictionaryBeanList.add(dictionaryBean3);
                DictionaryBean dictionaryBean4 = new DictionaryBean();
                dictionaryBean4.setDictionary_value_name("大专");
                dictionaryBean4.setDictionary_value_key(3);
                dictionaryBeanList.add(dictionaryBean4);
                DictionaryBean dictionaryBean5 = new DictionaryBean();
                dictionaryBean5.setDictionary_value_name("本科及以上");
                dictionaryBean5.setDictionary_value_key(4);
                dictionaryBeanList.add(dictionaryBean5);
                break;
        }
        DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(MyInfromationActivity.this, dictionaryBeanList);
        lv_choose.setAdapter(dictionaryAdapter);
        if (!TextUtils.isEmpty(dictionaryTitle)) {
            tv_title.setText(dictionaryTitle);
        }
        dictonaryWindow.showAtLocation((LinearLayout) findViewById(R.id.ll_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                    if (ContextCompat.checkSelfPermission(MyInfromationActivity.this,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) MyInfromationActivity.this,
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
                    UIUtils.showToastCenter(MyInfromationActivity.this,"请开启应用拍照权限");
                }
                break;
        }
    }
    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        tv_birthday.setText(now.split(" ")[0]);
//        currentTime.setText(now);

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                tv_birthday.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

//        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
//            @Override
//            public void handle(String time) { // 回调接口，获得选中的时间
//                currentTime.setText(time);
//            }
//        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
//        customDatePicker2.showSpecificTime(true); // 显示时和分
//        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }


    private void editeUserInfo() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        reqBody.put("usrs_id", user_id);
        if(!TextUtils.isEmpty(et_nickname.getText().toString())){
            reqBody.put("nickname", et_nickname.getText().toString());
        }
        if(!TextUtils.isEmpty(et_describe.getText().toString())){
            reqBody.put("self_intr", et_describe.getText().toString());
        }
        if(!TextUtils.isEmpty(headpic)){
            reqBody.put("head_pic",headpic);
        }
        if(sexDictionary != null){
            reqBody.put("sex", String.valueOf(sexDictionary.getDictionary_value_key()));
        }
        if(!TextUtils.isEmpty(tv_birthday.getText().toString())){
            reqBody.put("birthday", tv_birthday.getText().toString());
        }
        if(educationDictionary != null){
            reqBody.put("education_id", String.valueOf(educationDictionary.getDictionary_value_key()));
        }
        LogUtils.e(TAG+"修改个人资料参数："+reqBody.toString());
        netUtils.postDataAsynToNet(Constants.EditeUserInfoUrl,reqBody ,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
               String str = response.body().string();
                Message message = new Message();
                message.what = EDITEUSERINFOSUCCESS;
                message.obj = str;
                handler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = EDITEUSERINFOFAILED;
                message.obj = e;
                handler.sendMessage(message);
            }
        });
    }
}
