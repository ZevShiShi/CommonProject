package com.tencent.liteav.play.utils;

import android.text.TextUtils;

/**
 * 直播协议判断工具
 */
public class TCUrlUtil {

    /**
     * 是否是RTMP协议
     *
     * @param videoURL
     * @return
     */
    public static boolean isRTMPPlay(String videoURL) {
        return !TextUtils.isEmpty(videoURL) && videoURL.startsWith("rtmp");
    }

    /**
     * 是否是RTMP协议
     *
     * @param videoURL
     * @return
     */
    public static boolean isM3U8(String videoURL) {
        return !TextUtils.isEmpty(videoURL) && videoURL.endsWith("m3u8");
    }



    /**
     * 是否是HTTP-FLV协议
     *
     * @param videoURL
     * @return
     */
    public static boolean isFLVPlay(String videoURL) {
        return (!TextUtils.isEmpty(videoURL) && videoURL.startsWith("http://")
                || videoURL.startsWith("https://")) && videoURL.contains(".flv");
    }
}
