package com.blockchain.shortvideoplayer.utils;


import com.blockchain.shortvideoplayer.constants.Constants;
import com.blockchain.shortvideoplayer.response.BaseResponse;
import com.blockchain.shortvideoplayer.response.GetClassListResponse;
import com.blockchain.shortvideoplayer.response.GetHotSearchResponse;
import com.blockchain.shortvideoplayer.response.GetRecommendVideoListResponse;
import com.blockchain.shortvideoplayer.response.GetStsTokenResponse;
import com.blockchain.shortvideoplayer.response.GetSubscribSelfMediaListResponse;
import com.blockchain.shortvideoplayer.response.GetUserInfoResponse;
import com.blockchain.shortvideoplayer.response.GetUserSubVideoListResponse;
import com.blockchain.shortvideoplayer.response.GetVideoInfoResponse;
import com.blockchain.shortvideoplayer.response.SearchListResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * =====作者=====
 * 许英俊
 * =====时间=====
 * 2016/11/3.
 */
public class RetrofitUtils {
    private static Retrofit retrofit = null;
    private static IRetrofitServer iServer;

    public static IRetrofitServer getInstance() {
        if (retrofit == null) {
            synchronized (RetrofitUtils.class) {
                if (retrofit == null) {
                            retrofit = new Retrofit.Builder()
                                    .client(getOkHttpClient())//获取后的okhttp头部
                                    .baseUrl(Constants.BaseUrl)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                    iServer = retrofit.create(IRetrofitServer.class);
                }
            }
        }
        return iServer;
    }

    /**
     *  构造okhttp头部
     *
     * */
    private static OkHttpClient getOkHttpClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request()
                                    .newBuilder()
//                                    .removeHeader("User-Agent")//移除旧的
//                                    .addHeader("User-Agent", UIUtils.getUserAgent())//添加真正的头部
                                    .build();
                            return chain.proceed(request);
                        }
                }).build();
        return httpClient;
    }


    public interface IRetrofitServer {
        /**
         * 获取订阅的类目
         * @return
         */
        @POST(Constants.GetSubscribeListUrl)
        Call<GetClassListResponse> getSubscribeList(@Query("usrs_id") String usrs_id);

        /**
         * 获取推荐列表
         * @return
         */
        @POST(Constants.GetRecommendVideoListUrl)
        Call<GetRecommendVideoListResponse> getRecommendVideoList(@Query("usrs_id") String usrs_id);

        /**
         * 获取视频详情
         * @return
         */
        @POST(Constants.GetVideoInfoUrl)
        Call<GetVideoInfoResponse> getVideoInfoUrl(@Query("usrs_id") String usrs_id,@Query("video_id") String video_id,@Query("play_source") int play_source);

        /**
         * 不收贝币获取视频详情
         * @return
         */
        @POST(Constants.GetVideoInfoTempUrl)
        Call<GetVideoInfoResponse> getVideoInfoTempUrl(@Query("usrs_id") String usrs_id,@Query("video_id") String video_id,@Query("play_source") int play_source);

        /**
         * 获取stsToken
         * @return
         */
        @POST(Constants.GetStsToken)
        Call<GetStsTokenResponse> getStsToken();

        /**
         * 获取热门搜索
         * @return
         */
        @POST(Constants.HotSearchUrl)
        Call<GetHotSearchResponse> getHotSearch();

        /**
         * 搜索视频
         * @return
         */
        @POST(Constants.SearchVideoListUrl)
        Call<SearchListResponse> searchVideoList(@Query("usrs_id") String usrs_id,@Query("keyword_str") String keyword_str,@Query("search_type") int search_type);

        /**
         * 订阅自媒体列表
         * @return
         */
        @POST(Constants.SubscribeSelfListUrl)
        Call<GetSubscribSelfMediaListResponse> getSubscribeSelfList(@Query("usrs_id") String usrs_id);

        /**
         * 获取用户订阅视频
         * @return
         */
        @POST(Constants.GetUserSubVideoListUrl)
        Call<GetUserSubVideoListResponse> getUserSubVideoList(@Query("usrs_id") String usrs_id, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize );

        /**
         * 获取用户信息
         * @return
         */
        @POST(Constants.GetUserInfoUrl)
        Call<GetUserInfoResponse> getUserInfo(@Query("usrs_id") String usrs_id);

        /**
         * 订阅该类目
         * @return
         */
        @POST(Constants.SubscribeClassUrl)
        Call<BaseResponse> subscribeClass(@Query("usrs_id") String usrs_id,@Query("class_id_list") String class_id_list);
    }
}
