package com.blochchain.shortvideorecord.activity;

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

import com.blochchain.shortvideorecord.adapter.ClassAdapter;
import com.blochchain.shortvideorecord.adapter.ClassFirstAdapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.model.ClassFirstModel;
import com.blochchain.shortvideorecord.model.ClassItemModel;
import com.blochchain.shortvideorecord.model.ClassModel;
import com.blochchain.shortvideorecord.response.BaseResponse;
import com.blochchain.shortvideorecord.response.GetClassFirstListResponse;
import com.blochchain.shortvideorecord.response.UploadImageResponse;
import com.blochchain.shortvideorecord.utils.BitmapUtils;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.MobileUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blochchain.shortvideorecord.widget.CircleImageView;
import com.blockchain.shortvideorecord.R;
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
import java.util.List;
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
    private static final int GOTO_BANK = 10;  //跳转到我的银行卡
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private String path;


    private LinearLayout ll_bank_name;  //银行卡
    private TextView tv_bank_no;
    private EditText et_nickname;  //昵称
    private EditText et_describe;  //自我介绍
    private LinearLayout ll_class; //类目
    private TextView tv_class;
    private PopupWindow dictonaryWindow;
    private View dictonaryRoot;
    private ListView lv_choose;
    private Button btn_cancel;
    private TextView tv_title;
    private TextView tv_complete;
    private int oldPosition;
    private String dictionaryTitle;
    private String nick_name;
    private String head_pic;
    private String self_intr;
    private String bank_card;
    private String class_id;
    private String class_name;
    private ImageView iv_back;
    private AlertDialog saveDialog;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private NetUtils netUtils;

    private static final int EDITESELFSUCESS = 0;
    private static final int EDITESELFFAILD = 1;
    private static final int GETLISTSUCCESS = 2;
    private static final int GETLISTFAILED = 3;
    private static final int UPLOADPICTURESUCCESS = 10;
    private static final int UPLOADPICTUREFAILED = 11;
    private static final String TAG = MyInfromationActivity.class.getSimpleName();
    private List<ClassFirstModel> classFirstModelList;
    private List<ClassModel> classModelList;
    private ClassFirstAdapter classAdapter;
    private List<ClassItemModel> classItemModelList;
    private ClassItemModel classItemModel;
    private String bank_name;
    private String suoName;
    private String self_media_id;
    // 图片储存成功后
    protected static final int INTERCEPT = 6;
    private boolean fromLogin;
    private static final int TAKE_PHOTO_REQUEST_CODE = 10;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INTERCEPT:
                    uploadImage();
//                    File file = new File(path);
//                    Picasso.with(MyInfromationActivity.this).load(file).into(iv_head);
                    break;
                case EDITESELFSUCESS: //修改用户信息成功
                    String string1 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"成功："+string1);
                    BaseResponse baseResponse = gson.fromJson(string1,BaseResponse.class);
                    if(baseResponse != null){
                        if(baseResponse.getCode() == 0){
                            if(fromLogin){
                                Intent intent = new Intent(MyInfromationActivity.this,Main2Activity.class);
                                UIUtils.startActivity(intent);
                            }else {
                                setResult(200);
                            }
                            finish();
                        }else {
                            UIUtils.showToastCenter(MyInfromationActivity.this,baseResponse.getMsg());
                        }
                    }
                    break;
                case EDITESELFFAILD: //修改用户信息失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"失败："+e.getMessage());
                    break;
                case GETLISTSUCCESS:  //获取列表成功
                    String  string = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取列表成功："+string);
                    GetClassFirstListResponse getClassFirstListResponse = gson.fromJson(string,GetClassFirstListResponse.class);
                    if(getClassFirstListResponse != null){
                        if(getClassFirstListResponse.getCode() == 0){
                            classFirstModelList = getClassFirstListResponse.getClass_1_list();
                            if(classFirstModelList != null){
                                classItemModelList = new ArrayList<>();
                                for(ClassFirstModel classFirstModel : classFirstModelList){
                                    classModelList = classFirstModel.getClass_2_list();
                                    if(classModelList != null){
                                        for(ClassModel classModel : classModelList){
                                            ClassItemModel classItemModel = new ClassItemModel();
                                            classItemModel.setFirst_class_id(classFirstModel.getClass_id());
                                            classItemModel.setFirst_class_name(classFirstModel.getClass_name());
                                            classItemModel.setSecond_class_id(classModel.getClass_id());
                                            classItemModel.setSecond_class_name(classModel.getClass_name());
                                            classItemModelList.add(classItemModel);
                                        }
                                    }
                                }
                                classAdapter = new ClassFirstAdapter(MyInfromationActivity.this,classItemModelList);
                                lv_choose.setAdapter(classAdapter);
                                if (!TextUtils.isEmpty(dictionaryTitle)) {
                                    tv_title.setText(dictionaryTitle);
                                }
                                dictonaryWindow.showAtLocation((LinearLayout) findViewById(R.id.ll_layout),
                                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                            }
                        }else {
                            UIUtils.showToastCenter(MyInfromationActivity.this,getClassFirstListResponse.getMsg());
                        }
                    }
                    break;
                case GETLISTFAILED: //获取列表失败
                    IOException e1 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"获取列表失败："+e1.getMessage());
                    UIUtils.showToastCenter(MyInfromationActivity.this,e1.getMessage());
                    break;
                case UPLOADPICTURESUCCESS: //上传图片成功
                    String string3 = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"上传图片成功："+string3);
                    UploadImageResponse uploadImageResponse = gson.fromJson(string3,UploadImageResponse.class);
                    if(uploadImageResponse != null){
                        if(uploadImageResponse.getCode() == 0){
                            head_pic = uploadImageResponse.getPic_path();
                            if(!TextUtils.isEmpty(head_pic)){
                                Picasso.with(MyInfromationActivity.this).load(head_pic).into(iv_head);
                            }
                        }else {
                            UIUtils.showToastCenter(MyInfromationActivity.this,uploadImageResponse.getMsg());
                        }
                    }
                    break;
                case UPLOADPICTUREFAILED:  //上传图片失败
                    IOException e3 = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG+"上传图片失败："+e3.getMessage());
                    UIUtils.showToastCenter(MyInfromationActivity.this,e3.getMessage());
                    break;

            }
        }
    };


    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_my_infromation, null);
        setContentView(rootView);

        iv_back = findViewById(R.id.iv_back);
        iv_head = findViewById(R.id.iv_head);
        ll_layout = findViewById(R.id.ll_layout);
        ll_bank_name = findViewById(R.id.ll_bank_name);
        tv_bank_no = findViewById(R.id.tv_bank_no);
        et_nickname = findViewById(R.id.et_nickname);
        et_describe = findViewById(R.id.et_describe);
        ll_class = findViewById(R.id.ll_class);
        tv_class = findViewById(R.id.tv_class);

        initData();
        return null;
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

    private void initData() {
        self_media_id = SharedPrefrenceUtils.getString(this, "self_media_id");

        Intent intent = getIntent();
        nick_name = intent.getStringExtra("nick_name");
        head_pic = intent.getStringExtra("head_pic");
        self_intr = intent.getStringExtra("self_intr");
        bank_card = intent.getStringExtra("bank_card");
        class_id = intent.getStringExtra("class_id");
        class_name = intent.getStringExtra("class_name");
        bank_card = intent.getStringExtra("bank_no");
        bank_name = intent.getStringExtra("bank_name");
        fromLogin = intent.getBooleanExtra("fromLogin",false);
        if(!TextUtils.isEmpty(nick_name)){
            et_nickname.setText(nick_name);
        }
        if(!TextUtils.isEmpty(head_pic)){
            Picasso.with(this).load(head_pic).into(iv_head);
        }
        if(!TextUtils.isEmpty(self_intr)){
            et_describe.setText(self_intr);
        }
        if(!TextUtils.isEmpty(bank_card)){
            tv_bank_no.setText(bank_card);
        }
        if(!TextUtils.isEmpty(class_name)){
            tv_class.setText(class_name);
        }

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
        ll_class.setOnClickListener(this);
        ll_bank_name.setOnClickListener(this);

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
                        classItemModel = classItemModelList.get(position);
                        class_id = classItemModel.getSecond_class_id();
                        tv_class.setText(classItemModel.getFirst_class_name() + " "+ classItemModel.getSecond_class_name());
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

        if(requestCode == GOTO_BANK){
            if(resultCode == 200){
                bank_card = data.getStringExtra("bank_no");
                bank_name = data.getStringExtra("bank_name");
                tv_bank_no.setText(bank_card);
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
            case R.id.ll_bank_name:
                Intent intent = new Intent(MyInfromationActivity.this,MyBankCardActivity.class);
                intent.putExtra("bank_card",bank_card);
                intent.putExtra("bank_name",bank_name);
                UIUtils.startActivityForResult(intent,GOTO_BANK);
                break;
            case R.id.ll_class:
                dictionaryTitle = "类目";
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
                nick_name = et_nickname.getText().toString();
                if(TextUtils.isEmpty(nick_name)){
                    UIUtils.showToastCenter(MyInfromationActivity.this,"请输入昵称");
                    return;
                }
                self_intr = et_describe.getText().toString();
                if(TextUtils.isEmpty(self_intr)){
                    UIUtils.showToastCenter(MyInfromationActivity.this,"请输入自我介绍");
                }
                saveMessage();
                break;
        }

    }

    private void getDictonary() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        netUtils.postDataAsynToNet(Constants.GetAllClassUrl,reqBody,new NetUtils.MyNetCall(){

            @Override
            public void success(Call call, Response response) throws IOException {
                String str = response.body().string();
                Message message = new Message();
                message.what = GETLISTSUCCESS;
                message.obj = str;
                handler.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETLISTFAILED;
                message.obj = e;
                handler.sendMessage(message);
            }
        });

    }

    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            pWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_TakePicture: {
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

    private void saveMessage() {
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(head_pic)){
            reqBody.put("head_pic", head_pic);
        }
        if(!TextUtils.isEmpty(nick_name)){
            reqBody.put("nickname", nick_name);
        }
        if(!TextUtils.isEmpty(self_intr)){
            reqBody.put("self_intr", self_intr);
        }
        if(!TextUtils.isEmpty(bank_card)){
            reqBody.put("bank_card", bank_card);
        }
        if(!TextUtils.isEmpty(class_id)){
            reqBody.put("class_id", class_id);
        }
        reqBody.put("self_media_id", self_media_id);
        if(!TextUtils.isEmpty(bank_name)){
            reqBody.put("bank_name", bank_name);
        }
        LogUtils.e(TAG + ",修改自媒体信息参数："+reqBody.toString());
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

    private void requestPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

    }

}
