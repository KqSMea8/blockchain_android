package com.blockchain.shortvideoplayer.constants;

public interface Constants {
    String BaseUrl = "http://47.104.156.153:8068";
//    String BaseUrl = "http://47.107.32.235:8068";
//    String BaseUrl = "http://videoservice.beishu.org.cn:8068";

    String GetCodeUrl = BaseUrl + "/video_background/user/appSendMsg";  //获取手机验证码
    String LoginUrl = BaseUrl + "/video_background/user/applogin";  //登录
    String GetRecommendVideoListUrl = BaseUrl + "/video_background/user/recommendVideoList";  //首页推荐视频
    String GetRecommendHotVideoListUrl = BaseUrl + "/video_background/user/hotVideoList";  //首页热门视频
    String SearchVideoListUrl = BaseUrl + "/video_background/user/searchList";  //搜索视频
    String GetVideoInfoUrl = BaseUrl + "/video_background/user/videoInfo";  //视频详情
    String GetVideoInfoTempUrl = BaseUrl + "/video_background/user/videoInfoTemp";  //不收贝比的视频详情
    String AttentionVideoUrl = BaseUrl + "/video_background/user/attentionVideo";  //收藏视频
    String PraisedVideoUrl = BaseUrl + "/video_background/user/praisedVideo";  //点赞视频
    String SubscribeSelfListUrl = BaseUrl + "/video_background/user/subscribeSelf_mediaList";  //订阅自媒体列表
    String SelfMedioInfoUrl = BaseUrl + "/video_background/user/self_mediaInfo";  //自媒体详情
    String GetUserInfoUrl = BaseUrl + "/video_background/user/userInfo";  //获取用户信息
    String EditeUserInfoUrl = BaseUrl + "/video_background/user/editUserInfo";  //修改用户信息
    String GetSubscribeListUrl = BaseUrl + "/video_background/user/subscribeCassList";  //用户订阅列表
    String GetMsgRecordUrl = BaseUrl + "/video_background/user/msgRecord";  //获得消息列表
    String GetUsrbuyRecordUrl = BaseUrl + "/video_background/user/usrBayRecord";  //用户缴费账单查询
    String SubscribeSelfMediaUrl = BaseUrl + "/video_background/user/subscribeSelfmedia";  //自媒体订阅
    String ChangePhoneUrl = BaseUrl + "/video_background/user/editMobile";  //修改手机号
    String GetAllClassUrl = BaseUrl + "/video_background/user/allCassList";  //获得所有类目
    String GetClassVideoListUrl = BaseUrl + "/video_background/user/cassVideoList";  //根据类目获取视频列表
    String IsSubscribeClassUrl = BaseUrl + "/video_background/user/isSubscribeClass";  //查看用户是否订阅该类目
    String SubscribeClassUrl = BaseUrl + "/video_background/user/subscribeClass";  //订阅该类目
    String DelSubscribeClassUrl = BaseUrl + "/video_background/user/delSubscribeClass";  //取消订阅该类目
    String IsPraiseAttentionUrl = BaseUrl + "/video_background/user/isPraisedAttention";  //查看视频是否收藏和点赞
    String SendMegUrl = BaseUrl + "/video_background/user/sendMsg";  //留言
    String HotSearchUrl = BaseUrl + "/video_background/user/hotSearchList";  //获取热门搜索
    String IsSubscribeSelfmediaUrl = BaseUrl + "/video_background/user/isSubscribeSelfmedia";  //是否订阅自媒体
    String WeiXinPayUrl = BaseUrl + "/video_background/user/pay";  //支付

    String GetUserSubVideoListUrl = BaseUrl + "/video_background/user/getUserSubVideoList";  //获取用户订阅视频


    String UploadPictureUrl = BaseUrl + "/video_background/user/uploadPicture";  //上传图片
    String GetStsToken = BaseUrl + "/video_background/selfmedia/getStsToken";  //获取sts token

    int XutilTimeOut = 120000;
    long XutilCache = 5000;

    String  WeiXinAppId = "wx3f4afb6a07aa16dd";  //微信appid
}
