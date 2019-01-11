package com.blockchain.shortvideoplayer.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.GetStsTokenResponse;
import com.blockchain.shortvideoplayer.response.GetVideoInfoResponse;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.RetrofitUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.CircleImageView;
import com.blockchain.shortvideoplayer.widget.MyImageView;
import com.blockchain.vodplayerview.widget.AliyunVodPlayerView;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 类描述：
 * 创建人：刘敏
 * 创建时间：2016/04/14 10:49
 * 修改备注：
 */
public class VideoRecommendAdapter extends BaseAdapter {

    private String TAG=VideoRecommendAdapter.class.getSimpleName();
    private List<VideoBean> videoBeanList;
    private Context mContext;
    private int screenWidth;
    public VideoRecommendAdapter(Context context, List<VideoBean> videoBeanList,int screenWidth){
        mContext=context;
        this.videoBeanList=videoBeanList;
        this.screenWidth = screenWidth;
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return videoBeanList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        final ViewHolder vh;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.video_recommend_item, null);
            vh=new ViewHolder();
            vh.iv_thumbnail=view.findViewById(R.id.iv_thumbnail);
            vh.iv_head=view.findViewById(R.id.iv_head);
            vh.tv_nickname=view.findViewById(R.id.tv_nickname);
            vh.iv_eye=view.findViewById(R.id.iv_eye);
            vh.tv_number=view.findViewById(R.id.tv_number);
            vh.tv_instroction=view.findViewById(R.id.tv_instroction);
            vh.tv_praise=view.findViewById(R.id.tv_praise);
            vh.mAliyunVodPlayerView =view.findViewById(R.id.video_view);
            vh.iv_play =view.findViewById(R.id.iv_play);
            vh.ll_all =view.findViewById(R.id.ll_all);

            vh.mAliyunVodPlayerView.setKeepScreenOn(true);
//        PlayParameter.PLAY_PARAM_URL = DEFAULT_URL;
            String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
            vh.mAliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
            vh.mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
            vh.mAliyunVodPlayerView.setAutoPlay(true);
            vh.mAliyunVodPlayerView.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

        int screenHeight = screenWidth * 175/375;
        final VideoBean videoBean = videoBeanList.get(pos);
        if(!TextUtils.isEmpty(videoBean.getVideo_pic())){
            Picasso.with(mContext)
                    .load(videoBean.getVideo_pic())
                    .resize(screenWidth,screenHeight)
                    .config(Bitmap.Config.RGB_565)
                    .centerCrop().into(vh.iv_thumbnail);
        }
        if(!TextUtils.isEmpty(videoBean.getHead_pic())){
            Picasso.with(mContext)
                    .load(videoBean.getHead_pic())
                    .resize(UIUtils.dip2px(18),UIUtils.dip2px(18))
                    .config(Bitmap.Config.RGB_565)
                    .centerCrop().into(vh.iv_head);
        }
        if(!TextUtils.isEmpty(videoBean.getVideo_dec())){
            vh.tv_instroction.setText(videoBean.getVideo_dec());
        }
//        if(!TextUtils.isEmpty(videoBean.getNickname())){
//            vh.tv_nickname.setText(videoBean.getNickname());
//        }
//        if(!TextUtils.isEmpty(videoBean.getPlay_amount())){
//            vh.tv_number.setText(videoBean.getPlay_amount());
//        }
//        if(!TextUtils.isEmpty(videoBean.getPraised_amount())){
//            vh.tv_praise.setText(videoBean.getPraised_amount());
//        }
        vh.ll_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(vh.mAliyunVodPlayerView.isPlaying()){
//                    vh.mAliyunVodPlayerView.pause();
//                }else {
//                    vh.mAliyunVodPlayerView.start();
//                    vh.iv_thumbnail.setVisibility(View.GONE);
//                }
                getInfo(videoBean.getVideo_id(),vh.mAliyunVodPlayerView,vh.tv_number,vh.tv_praise,vh.tv_nickname);
            }
        });

//        getInfo(videoBean.getVideo_id(),vh.mAliyunVodPlayerView,vh.tv_number,vh.tv_praise,vh.tv_nickname);
        return view;
    }

    private void getInfo(String videoId, final AliyunVodPlayerView mAliyunVodPlayerView, final TextView tv_number, final TextView tv_praise, final TextView tv_nickname) {
        String user_id = SharedPrefrenceUtils.getString(mContext,"usrs_id");
        String userId;
        if(TextUtils.isEmpty(user_id)){
            userId = "0";
        }else {
            userId = user_id;
        }
        Call<GetVideoInfoResponse> call = RetrofitUtils.getInstance().getVideoInfoUrl(userId,videoId,0);
        call.enqueue(new Callback<GetVideoInfoResponse>() {

            @Override
            public void onResponse(Call<GetVideoInfoResponse> call, Response<GetVideoInfoResponse> response) {
                LogUtils.e(TAG +"--获取视频详情:"+ response.body());
                GetVideoInfoResponse getVideoInfoResponse = response.body();
                if(getVideoInfoResponse != null){
                    if(getVideoInfoResponse.getCode() == 0){
                        VideoBean videoBean = getVideoInfoResponse.getData();
                        if(videoBean != null){
                            if(!TextUtils.isEmpty(videoBean.getNickname())){
                                tv_nickname.setText(videoBean.getNickname());
                            }
                            if(!TextUtils.isEmpty(videoBean.getPraised_amount())){
                                tv_praise.setText(videoBean.getPraised_amount());
                            }
                            if(!TextUtils.isEmpty(videoBean.getAttention_amount())){
                                tv_number.setText(videoBean.getAttention_amount());
                            }
                            getStsToken(mAliyunVodPlayerView,videoBean.getV_id());
                        }
                    }else {
                        LogUtils.e(TAG + getVideoInfoResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetVideoInfoResponse> call, Throwable t) {
                LogUtils.e(TAG +"--获取视频详情报错"+ t.getMessage());
            }
        });
    }

    private void getStsToken(final AliyunVodPlayerView mAliyunVodPlayerView, final String v_id) {
        Call<GetStsTokenResponse> call = RetrofitUtils.getInstance().getStsToken();
        call.enqueue(new Callback<GetStsTokenResponse>() {

            @Override
            public void onResponse(Call<GetStsTokenResponse> call, Response<GetStsTokenResponse> response) {
                LogUtils.e(TAG +"--获取stsToken:"+ response.body());
                GetStsTokenResponse getStsTokenResponse = response.body();
                if(getStsTokenResponse != null){
                    if(getStsTokenResponse.getCode() == 0){
                        String accessKeyId = getStsTokenResponse.getAccessKeyId();
                        String accessKeySecret = getStsTokenResponse.getAccessKeySecret();
                        String securityToken = getStsTokenResponse.getSecurityToken();

                        AliyunVidSts vidSts = new AliyunVidSts();
                        vidSts.setVid(v_id);
                        vidSts.setAcId(accessKeyId);
                        vidSts.setAkSceret(accessKeySecret);
                        vidSts.setSecurityToken(securityToken);
                        if (mAliyunVodPlayerView != null) {
                            mAliyunVodPlayerView.setVidSts(vidSts);
                        }
                    }else {
                        LogUtils.e(TAG + getStsTokenResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetStsTokenResponse> call, Throwable t) {
                LogUtils.e(TAG +"--获取stsToken报错"+ t.getMessage());
            }
        });
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return videoBeanList.size();
    }

    class ViewHolder{
        public MyImageView iv_thumbnail;
        public CircleImageView iv_head;
        public TextView tv_nickname;
        public ImageView iv_eye;
        public TextView tv_number;
        public TextView tv_instroction;
        public TextView tv_praise;
        public AliyunVodPlayerView mAliyunVodPlayerView;
        public ImageView iv_play;
        public LinearLayout ll_all;
    }
}
