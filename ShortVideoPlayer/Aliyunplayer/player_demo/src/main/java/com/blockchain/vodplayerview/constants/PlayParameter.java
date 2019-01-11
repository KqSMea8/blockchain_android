package com.blockchain.vodplayerview.constants;

/**
 * 播放参数, 包含:
 *  vid, vidSts, akId, akSecre, scuToken
 */
public class PlayParameter {


    /**
     * type, 用于区分播放类型, 默认为vidsts播放
     * vidsts: vid类型
     * localSource: url类型
     *
     */
    public static  String PLAY_PARAM_TYPE = "vidsts";
//    public static  String PLAY_PARAM_TYPE = "localSource";

    /**
     * vid, 初始为: d9dc4c4ba91843ee81db7fef28c01560
     */
//    public static  String PLAY_PARAM_VID = "a34647538a1a4c9f9a0cc1c9b01747c4";
    public static  String PLAY_PARAM_VID = "a0ddb5dbef2040b59b35c65a76d7a0cf";

    /**
     * akId
     */
    public static  String PLAY_PARAM_AK_ID = "STS.NJRvcoe95t9vzwHARbPipmVyX";

    /**
     * akSecre
     */
    public static  String PLAY_PARAM_AK_SECRE = "4rYzvGAANujfVCochGb7SxM2o5YDH1AiHHTZFZuukSCo";

    /**
     * scuToken
     */
    public static  String PLAY_PARAM_SCU_TOKEN = "CAIS8QF1q6Ft5B2yfSjIr4nnPdnbiOYUw/udeFH5pVI3XOZcgpPSujz2IHhMf3BgAe4Zt/symWpW6/sYlqBtSpNIQgmcNZcoNXGCFbflMeT7oMWQweEuqv/MQBq+aXPS2MvVfJ+KLrf0ceusbFbpjzJ6xaCAGxypQ12iN+/i6/clFKN1ODO1dj1bHtxbCxJ/ocsBTxvrOO2qLwThjxi7biMqmHIl0DkmsP/knZTCukuD0AKk8IJP+dSteKrDRtJ3IZJyX+2y2OFLbafb2EZSkUMSrPwv3PQapm6Y44zHXQQMuQ/nLevY/9p1Kwt0djS/sAGIMW6VGoABLzarLfW4S0eVrPqXOCrtIwXMFFi3a5ktCgvTvsnNim9FZowEhN6Gbo0ijJugT9MBWPLbcKgqOcNHNemoHOjHiyJUoeP6E5IkpLE07eMrzINPKu4pxNOe5/WqO+1XYZ4k3WIC14dl9B5NebsrS/H+SThmRRx5Tfe7hojIeIZDXMk=";
    /**
     * url类型的播放地址, 初始为:http://player.alicdn.com/video/aliyunmedia.mp4
     */
//    public static String PLAY_PARAM_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";
//    public static String PLAY_PARAM_URL = "http://gslb.miaopai.com/stream/ed5HCfnhovu3tyIQAiv60Q__.mp4";
    public static String PLAY_PARAM_URL = "";

}
