package com.blochchain.shortvideorecord.constants;

public interface Constants {
        String BaseUrl = "http://47.104.156.153:8068";
//    String BaseUrl = "http://47.107.32.235:8068";
//    String BaseUrl = "http://videoservice.beishu.org.cn:8068";

    String GetCodeUrl = BaseUrl + "/video_background/user/appSendMsg";  //获取手机验证码
    String LoginUrl = BaseUrl + "/video_background/selfmedia/applogin";  //登录
    String GetFirstInfUrl = BaseUrl + "/video_background/selfmedia/firstInf";  //获取首页信息
    String GetFansInfUrl = BaseUrl + "/video_background/selfmedia/fansInf";  //获取粉丝信息
    String GetPlayDataUrl = BaseUrl + "/video_background/selfmedia/playData";  //播放数据
    String GetVideoListUrl = BaseUrl + "/video_background/selfmedia/videoList";  //获取作品列表
    String GetVideoInfoUrl = BaseUrl + "/video_background/selfmedia/videoInf";  //获取作品详情
    String DelVideoInfoUrl = BaseUrl + "/video_background/selfmedia/delVideo";  //删除作品
    String EditVideoInfoUrl = BaseUrl + "/video_background/selfmedia/editVideoInf";  //修改作品
    String AddVideoUrl = BaseUrl + "/video_background/selfmedia/addVideoInf";  //上传作品
    String GetMyIncomeUrl = BaseUrl + "/video_background/selfmedia/myIncome";  //我的收益
    String GetMyIncomeInfoUrl = BaseUrl + "/video_background/selfmedia/myIncomeInfo";  //我的收益详情
    String GetWithdrawInfoUrl = BaseUrl + "/video_background/selfmedia/queryWithdrawInf";  // 提现查询
    String WithdrawUrl = BaseUrl + "/video_background/selfmedia/withdraw";  // 提现
    String GetWithdrawListUrl = BaseUrl + "/video_background/selfmedia/queryWithdrawList";  // 查询提现明细
    String GetWithdrawResultUrl = BaseUrl + "/video_background/selfmedia/queryWithdrawResult";  // 提现结果查询
    String GetSelfMediaInfUrl = BaseUrl + "/video_background/selfmedia/getSelfmediaInf";  // 获取自媒体个人信息
    String ChangePhoneUrl = BaseUrl + "/video_background/selfmedia/editSelfmediaTel";  //修改手机号
    String EditSelfMediaInfUrl = BaseUrl + "/video_background/selfmedia/editSelfmediaInf";  //修改自媒体信息
    String GetMsgRecordUrl = BaseUrl + "/video_background/selfmedia/msgRecord";  //获得消息列表

    String SendMegUrl = BaseUrl + "/video_background/user/sendMsg";  //留言
    String GetAllClassUrl = BaseUrl + "/video_background/user/allCassList";  //获得所有类目


    String UploadPictureUrl = BaseUrl + "/video_background/user/uploadPicture";  //上传图片
    String GetStsToken = BaseUrl + "/video_background/selfmedia/getStsToken";  //获取sts token
}
