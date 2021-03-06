package com.tencent.liteav.play;


/**
 * Created by hans on 2019/3/25.
 * 使用腾讯云fileId播放
 */
public class SuperPlayerVideoId {
    public String           fileId;                             // 腾讯云视频fileId

    /**
     * 防盗链参数 具体可参考{@link SuperPlayerSignUtils}
     */
    public String           timeout;                            // 【可选】加密链接超时时间戳，转换为16进制小写字符串，腾讯云 CDN 服务器会根据该时间判断该链接是否有效。
    public int              exper           = -1;               // 【V2可选】试看时长，单位：秒。可选
    public String           us;                                 // 【可选】唯一标识请求，增加链接唯一性
    public String           sign;                               // 【可选】防盗链签名

    @Override
    public String toString() {
        return "SuperPlayerVideoId{" +
                ", fileId='" + fileId + '\'' +
                ", timeout='" + timeout + '\'' +
                ", exper=" + exper +
                ", us='" + us + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
