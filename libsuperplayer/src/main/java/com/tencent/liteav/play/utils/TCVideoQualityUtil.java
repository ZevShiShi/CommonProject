package com.tencent.liteav.play.utils;

import com.tencent.liteav.play.bean.TCPlayInfoStream;
import com.tencent.liteav.play.bean.TCVideoQuality;
import com.tencent.rtmp.TXBitrateItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuejiaoli on 2018/7/6.
 *
 * 清晰度转换工具
 */

public class TCVideoQualityUtil {

    /**
     * 从比特流信息转换为清晰度信息
     *
     * @param bitrateItem
     * @return
     */
    public static TCVideoQuality convertToVideoQuality(TXBitrateItem bitrateItem, int index) {
        TCVideoQuality quality = new TCVideoQuality();
        quality.bitrate = bitrateItem.bitrate;
        quality.index = bitrateItem.index;

        switch (index) {
            case 0:
                quality.name = "SD";
                quality.title = "标清";
                break;
            case 1:
                quality.name = "HD";
                quality.title = "高清";
                break;
            case 2:
                quality.name = "FHD";
                quality.title = "超清";
                break;
        }
        return quality;
    }

    /**
     * 从源视频信息与视频类别信息转换为清晰度信息
     *
     * @param sourceStream
     * @param classification
     * @return
     */
    public static TCVideoQuality convertToVideoQuality(TCPlayInfoStream sourceStream, String classification) {
        TCVideoQuality quality = new TCVideoQuality();
        quality.bitrate = sourceStream.getBitrate();

        if (classification.equals("FLU")) {
            quality.name = "FLU";
            quality.title = "流畅";
        } else if (classification.equals("SD")) {
            quality.name = "SD";
            quality.title = "标清";
        } else if (classification.equals("HD")) {
            quality.name = "HD";
            quality.title = "高清";
        } else if (classification.equals("FHD")) {
            quality.name = "FHD";
            quality.title = "全高清";
        } else if (classification.equals("2K")) {
            quality.name = "2K";
            quality.title = "2K";
        } else if (classification.equals("4K")) {
            quality.name = "4K";
            quality.title = "4K";
        }
        quality.url = sourceStream.url;
        quality.index = -1;
        return quality;
    }

    /**
     * 从{@link TCPlayInfoStream}转换为{@link TCVideoQuality}
     *
     * @param stream
     * @return
     */
    public static TCVideoQuality convertToVideoQuality(TCPlayInfoStream stream) {
        TCVideoQuality qulity = new TCVideoQuality();
        qulity.bitrate = stream.getBitrate();
        qulity.name = stream.id;
        qulity.title = stream.name;
        qulity.url = stream.url;
        qulity.index = -1;
        return qulity;
    }

    /**
     * 从转码列表转换为清晰度列表
     *
     * @param transcodeList
     * @return
     */
    public static List<TCVideoQuality> convertToVideoQualityList(HashMap<String, TCPlayInfoStream> transcodeList) {
        List<TCVideoQuality> videoQulities = new ArrayList<>();
        for (String classification : transcodeList.keySet()) {
            TCVideoQuality videoQulity = convertToVideoQuality(transcodeList.get(classification));
            videoQulities.add(videoQulity);
        }
        return videoQulities;
    }
}
