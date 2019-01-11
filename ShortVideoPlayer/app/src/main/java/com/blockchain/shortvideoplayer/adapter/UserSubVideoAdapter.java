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
import android.widget.TextView;

import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.blockchain.shortvideoplayer.R;
import com.blockchain.shortvideoplayer.model.ClassModel;
import com.blockchain.shortvideoplayer.model.UserSubVideoBean;
import com.blockchain.shortvideoplayer.model.VideoBean;
import com.blockchain.shortvideoplayer.response.GetStsTokenResponse;
import com.blockchain.shortvideoplayer.response.GetVideoInfoResponse;
import com.blockchain.shortvideoplayer.utils.LogUtils;
import com.blockchain.shortvideoplayer.utils.RetrofitUtils;
import com.blockchain.shortvideoplayer.utils.SharedPrefrenceUtils;
import com.blockchain.shortvideoplayer.utils.UIUtils;
import com.blockchain.shortvideoplayer.widget.CircleImageView;
import com.blockchain.shortvideoplayer.widget.MyImageView;
import com.blockchain.shortvideoplayer.widget.XCRoundRectImageView;
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
public class UserSubVideoAdapter extends BaseAdapter {

    private String TAG = UserSubVideoAdapter.class.getSimpleName();
    private List<UserSubVideoBean> userSubVideoBeanList;
    private Context mContext;
    private int screenWidth;

    public UserSubVideoAdapter(Context context, List<UserSubVideoBean> userSubVideoBeanList,int screenWidth) {
        mContext = context;
        this.userSubVideoBeanList = userSubVideoBeanList;
        this.screenWidth = screenWidth;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return userSubVideoBeanList.get(arg0);
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
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.user_sub_video_item, null);
            vh = new ViewHolder();
            vh.iv_head = view.findViewById(R.id.iv_head);
            vh.tv_nickname = view.findViewById(R.id.tv_nickname);
            vh.tv_time = view.findViewById(R.id.tv_time);
            vh.tv_desc = view.findViewById(R.id.tv_desc);
            vh.mAliyunVodPlayerView = view.findViewById(R.id.video_view);
            vh.iv_thumbnail = view.findViewById(R.id.iv_thumbnail);
            vh.iv_play = view.findViewById(R.id.iv_play);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }

        final UserSubVideoBean userSubVideoBean = userSubVideoBeanList.get(pos);
        if(!TextUtils.isEmpty(userSubVideoBean.getNickname())){
            vh.tv_nickname.setText(userSubVideoBean.getNickname());
        }
        if(!TextUtils.isEmpty(userSubVideoBean.getVideo_dec())){
            vh.tv_desc.setText(userSubVideoBean.getVideo_dec());
        }
        if(!TextUtils.isEmpty(userSubVideoBean.getRelease_time())){
            vh.tv_time.setText(userSubVideoBean.getRelease_time());
        }
        if(!TextUtils.isEmpty(userSubVideoBean.getHead_pic())){
            Picasso.with(mContext).load(userSubVideoBean.getHead_pic()).resize(UIUtils.dip2px(28),UIUtils.dip2px(28)).centerCrop().into(vh.iv_head);
        }
        int width = screenWidth - UIUtils.dip2px(60);
        if(!TextUtils.isEmpty(userSubVideoBean.getVideo_pic())){
            Picasso.with(mContext).load(userSubVideoBean.getVideo_pic()).resize(width,UIUtils.dip2px(200)).centerCrop().into(vh.iv_thumbnail);
        }

        vh.mAliyunVodPlayerView.setKeepScreenOn(true);
//        PlayParameter.PLAY_PARAM_URL = DEFAULT_URL;
        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
        vh.mAliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        vh.mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
        vh.mAliyunVodPlayerView.setAutoPlay(false);
        vh.mAliyunVodPlayerView.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        getInfoTemp(userSubVideoBean.getVideo_id(), vh.mAliyunVodPlayerView);
        vh.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo(userSubVideoBean.getVideo_id(), vh.mAliyunVodPlayerView, vh.iv_thumbnail);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return userSubVideoBeanList.size();
    }

    private void getInfo(String videoId, final AliyunVodPlayerView mAliyunVodPlayerView, final ImageView iv_thumbnail) {
        String user_id = SharedPrefrenceUtils.getString(mContext, "usrs_id");
        String userId;
        if (TextUtils.isEmpty(user_id)) {
            userId = "0";
        } else {
            userId = user_id;
        }
        Call<GetVideoInfoResponse> call = RetrofitUtils.getInstance().getVideoInfoUrl(userId, videoId, 0);
        call.enqueue(new Callback<GetVideoInfoResponse>() {

            @Override
            public void onResponse(Call<GetVideoInfoResponse> call, Response<GetVideoInfoResponse> response) {
                LogUtils.e(TAG + "--获取视频详情:" + response.body());
                GetVideoInfoResponse getVideoInfoResponse = response.body();
                if (getVideoInfoResponse != null) {
                    if (getVideoInfoResponse.getCode() == 0) {

                        if (mAliyunVodPlayerView.isPlaying()) {
                            mAliyunVodPlayerView.pause();
                        } else {
                            mAliyunVodPlayerView.start();
                            iv_thumbnail.setVisibility(View.GONE);
                        }
                    } else {
                        LogUtils.e(TAG + getVideoInfoResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetVideoInfoResponse> call, Throwable t) {
                LogUtils.e(TAG + "--获取视频详情报错" + t.getMessage());
            }
        });
    }

    private void getInfoTemp(String videoId, final AliyunVodPlayerView mAliyunVodPlayerView) {
        String user_id = SharedPrefrenceUtils.getString(mContext, "usrs_id");
        String userId;
        if (TextUtils.isEmpty(user_id)) {
            userId = "0";
        } else {
            userId = user_id;
        }
        Call<GetVideoInfoResponse> call = RetrofitUtils.getInstance().getVideoInfoTempUrl(userId, videoId, 0);
        call.enqueue(new Callback<GetVideoInfoResponse>() {

            @Override
            public void onResponse(Call<GetVideoInfoResponse> call, Response<GetVideoInfoResponse> response) {
                LogUtils.e(TAG + "--获取视频详情:" + response.body());
                GetVideoInfoResponse getVideoInfoResponse = response.body();
                if (getVideoInfoResponse != null) {
                    if (getVideoInfoResponse.getCode() == 0) {
                        VideoBean videoBean = getVideoInfoResponse.getData();
                        if (videoBean != null) {
                            getStsToken(mAliyunVodPlayerView, videoBean.getV_id());
                        }
                    } else {
                        LogUtils.e(TAG + getVideoInfoResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetVideoInfoResponse> call, Throwable t) {
                LogUtils.e(TAG + "--获取视频详情报错" + t.getMessage());
            }
        });
    }

    private void getStsToken(final AliyunVodPlayerView mAliyunVodPlayerView, final String v_id) {
        Call<GetStsTokenResponse> call = RetrofitUtils.getInstance().getStsToken();
        call.enqueue(new Callback<GetStsTokenResponse>() {

            @Override
            public void onResponse(Call<GetStsTokenResponse> call, Response<GetStsTokenResponse> response) {
                LogUtils.e(TAG + "--获取stsToken:" + response.body());
                GetStsTokenResponse getStsTokenResponse = response.body();
                if (getStsTokenResponse != null) {
                    if (getStsTokenResponse.getCode() == 0) {
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
                    } else {
                        LogUtils.e(TAG + getStsTokenResponse.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<GetStsTokenResponse> call, Throwable t) {
                LogUtils.e(TAG + "--获取stsToken报错" + t.getMessage());
            }
        });
    }

    class ViewHolder {
        public CircleImageView iv_head;
        public TextView tv_nickname;
        public TextView tv_time;
        public TextView tv_desc;
        public AliyunVodPlayerView mAliyunVodPlayerView;
        public MyImageView iv_thumbnail;
        public ImageView iv_play;
    }
}
