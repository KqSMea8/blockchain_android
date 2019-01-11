package com.blochchain.shortvideorecord.activity;

import android.app.Dialog;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.blochchain.shortvideorecord.adapter.FansPlaceAdapter;
import com.blochchain.shortvideorecord.constants.Constants;
import com.blochchain.shortvideorecord.model.FansPlaceModel;
import com.blochchain.shortvideorecord.model.PieDataEntity;
import com.blochchain.shortvideorecord.response.GetFansInfoResponse;
import com.blochchain.shortvideorecord.response.GetFirstInfoResponse;
import com.blochchain.shortvideorecord.tabpager.ViewTabBasePager;
import com.blochchain.shortvideorecord.utils.DialogUtils;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blochchain.shortvideorecord.utils.NetUtils;
import com.blochchain.shortvideorecord.utils.SharedPrefrenceUtils;
import com.blochchain.shortvideorecord.utils.UIUtils;
import com.blochchain.shortvideorecord.widget.PieChart;
import com.blockchain.shortvideorecord.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import okhttp3.Call;
import okhttp3.Response;

public class FansPortraitActivity extends BaseActivity {
    private static final String TAG = FansPortraitActivity.class.getSimpleName();
    private PieChart pc_sex;
    private PieChart pc_source;
//    private int[] mColors = {0xFFCCFF00, 0xFF6495ED};
    private int[] mColors = {0xFF2E9FFF, 0xFFD869DC};  //粉丝性别
    private int[] mColors1 = {0xFF41C0C9, 0xFFE4BB58,0xFF68C868};  //粉丝来源
    private ListView lv_place;
    private static final int GETMAININFOSUCCESS = 0;
    private static final int GETMAININFOFAILED = 1;
    private NetUtils netUtils;
    private ImageView iv_back;

    //接口请求菊花
    private Dialog loadingDialog;
    private Gson gson;
    private String self_media_id;
    private MyHandler mHandle = new MyHandler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETMAININFOSUCCESS:  //获取主页信息成功
                    String str = (String) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "成功：" + str);
                    GetFansInfoResponse getFansInfoResponse = gson.fromJson(str,GetFansInfoResponse.class);
                    if(getFansInfoResponse != null){
                        if(getFansInfoResponse.getCode() == 0){
                            setFansInfo(getFansInfoResponse);
                        }else {
                            UIUtils.showToastCenter(FansPortraitActivity.this,getFansInfoResponse.getMsg());
                        }
                    }
                    break;
                case GETMAININFOFAILED: //获取验证码失败
                    IOException e = (IOException) msg.obj;
                    loadingDialog.dismiss();
                    LogUtils.e(TAG + "失败：" + e.getMessage());
                    break;
            }
        }
    };

    private void setFansInfo(GetFansInfoResponse getFansInfoResponse) {
        String maleStr = getFansInfoResponse.getFans_man_ratio();
        String femaleStr = getFansInfoResponse.getFans_girl_ratio();
        float male = Float.valueOf(maleStr) * 100;
        float female = Float.valueOf(femaleStr) * 100;
        if(male == 0 && female ==0){
            UIUtils.showToastCenter(this,"没有性别比例数据");
            return;
        }
        LogUtils.e(TAG + ",male:"+male+ ",female:"+female);
        List<PieDataEntity> dataEntities = new ArrayList<>();
        PieDataEntity entity = new PieDataEntity("男性",male,mColors[0]);
        dataEntities.add(entity);
        PieDataEntity entity1 = new PieDataEntity("女性",female,mColors[1]);
        dataEntities.add(entity1);
        pc_sex.setDataList(dataEntities);
        pc_sex.startAnimation(2000);

        String playStr = getFansInfoResponse.getFans_source_play_ratio();
        String classStr = getFansInfoResponse.getFans_source_class_ratio();
        String searchStr = getFansInfoResponse.getFans_source_search_ratio();
        float play = Float.valueOf(playStr) * 100;
        float classs = Float.valueOf(classStr) * 100;
        float serch = Float.valueOf(searchStr) * 100;
        LogUtils.e(TAG + ",play:"+play+ ",classs:"+classs +",serch:"+serch);
        List<PieDataEntity> dataEntities1 = new ArrayList<>();
        if(play == 0 && classs == 0 && serch == 0){
            UIUtils.showToastCenter(this,"没有粉丝来源数据");
            return;
        }
        if(play == 0 && classs == 0){
            PieDataEntity entity2 = new PieDataEntity("视频播放,类目推荐",play,mColors1[0]);
            dataEntities1.add(entity2);
            PieDataEntity entity3 = new PieDataEntity("搜索引擎",serch,mColors1[1]);
            dataEntities1.add(entity3);
        }else if(play == 0 && serch == 0){
            PieDataEntity entity2 = new PieDataEntity("视频播放,搜索引擎",play,mColors1[0]);
            dataEntities1.add(entity2);
            PieDataEntity entity3 = new PieDataEntity("类目推荐",classs,mColors1[1]);
            dataEntities1.add(entity3);
        }else if(classs == 0 && serch ==0){
            PieDataEntity entity2 = new PieDataEntity("类目推荐,搜索引擎",classs,mColors1[0]);
            dataEntities1.add(entity2);
            PieDataEntity entity3 = new PieDataEntity("视频播放",play,mColors1[1]);
            dataEntities1.add(entity3);
        }else {
            PieDataEntity entity2 = new PieDataEntity("视频播放",play,mColors1[0]);
            dataEntities1.add(entity2);
            PieDataEntity entity3 = new PieDataEntity("类目推荐",classs,mColors1[1]);
            dataEntities1.add(entity3);
            PieDataEntity entity4 = new PieDataEntity("搜索引擎",serch,mColors1[2]);
            dataEntities1.add(entity4);
        }

        pc_source.setDataList(dataEntities1);
        pc_source.startAnimation(2000);
    }

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_fans_portrait, null);
        setContentView(rootView);

        pc_sex = findViewById(R.id.pc_sex);
        pc_source = findViewById(R.id.pc_source);
        lv_place = findViewById(R.id.lv_place);
        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initData();
        return rootView;
    }

    private void initData() {
        netUtils = NetUtils.getInstance();
        gson = new Gson();
        loadingDialog = DialogUtils.createLoadDialog(this, true);
        self_media_id = SharedPrefrenceUtils.getString(this,"self_media_id");
        
//        List<PieDataEntity> dataEntities = new ArrayList<>();
//        PieDataEntity entity = new PieDataEntity("男性",63,mColors[0]);
//        dataEntities.add(entity);
//        PieDataEntity entity1 = new PieDataEntity("女性",37,mColors[1]);
//        dataEntities.add(entity1);
//        pc_sex.setDataList(dataEntities);
//        pc_sex.startAnimation(2000);
//
//        List<PieDataEntity> dataEntities1 = new ArrayList<>();
//        PieDataEntity entity2 = new PieDataEntity("视频播放",33,mColors1[0]);
//        dataEntities1.add(entity2);
//        PieDataEntity entity3 = new PieDataEntity("类目推荐",34,mColors1[1]);
//        dataEntities1.add(entity3);
//        PieDataEntity entity4 = new PieDataEntity("搜索引擎",33,mColors1[2]);
//        dataEntities1.add(entity4);
//        pc_source.setDataList(dataEntities1);
//        pc_source.startAnimation(2000);
//
//        List<FansPlaceModel> fansPlaceModels = new ArrayList<>();
//        fansPlaceModels.add(new FansPlaceModel("北京","20%"));
//        fansPlaceModels.add(new FansPlaceModel("上海","30%"));
//        fansPlaceModels.add(new FansPlaceModel("广州","25%"));
//        fansPlaceModels.add(new FansPlaceModel("深圳","25%"));
//        FansPlaceAdapter fansPlaceAdapter = new FansPlaceAdapter(this,fansPlaceModels);
//        lv_place.setAdapter(fansPlaceAdapter);
        
        getFansInfo();
    }

    private void getFansInfo() {
        loadingDialog.show();
        Map<String, String> reqBody = new ConcurrentSkipListMap<>();
        if(!TextUtils.isEmpty(self_media_id)){
            reqBody.put("self_media_id",self_media_id);
        }
        netUtils.postDataAsynToNet(Constants.GetFansInfUrl, reqBody, new NetUtils.MyNetCall() {
            @Override
            public void success(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = GETMAININFOSUCCESS;
                message.obj = string;
                mHandle.sendMessage(message);
            }

            @Override
            public void failed(Call call, IOException e) {
                Message message = new Message();
                message.what = GETMAININFOFAILED;
                message.obj = e;
                mHandle.sendMessage(message);
            }
        });
    }


}
