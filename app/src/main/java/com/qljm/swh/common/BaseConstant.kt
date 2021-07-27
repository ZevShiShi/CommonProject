package com.lihui.base.common

class BaseConstant {
    companion object {
        // ===================================新闻接口环境======================================================
        //本地服务器-付志豪
        var BASE_WU_URL = "http://192.168.1.45:9871"
        var BASE_WU_h5 = "http://192.168.1.5:2020"

        //本地服务器-小伟
        var BASE_WEI_URL = "http://192.168.1.43:9871"
        var BASE_WEI_h5 = "http://192.168.1.5:2020"

        //本地服务器-黄宇飞
        var BASE_YU_URL = "http://192.168.1.34:9871"
        var BASE_YU_h5 = "http://192.168.1.5:2020"

        //本地服务器-刘震海
        var BASE_LIU_URL = "http://192.168.1.56:9871"

        // 本地服务器
        const val BASE_LOCAL_URL = "http://192.168.1.2:9871" //本地服务器
        const val BASE_LOCAL_h5 = "http://192.168.1.2:2020"  //测试环境h5

        // 华为测试环境
        const val BASE_HUAWEI_URL = "http://test.api.worldhds.net"  //布置华为测试环境
        const val BASE_HUAWEI_h5 = "http://test.admin.worldhds.net:2020"  //华为测试环境h5

        // 阿里的正式环境
        const val BASE_ALI_URL = "https://api.worldhds.net"
        const val BASE_ALI_h5 = "http://admin.worldhds.net:2020"

        // 阿里UAT环境
        const val BASE_ALI_UAT_URL = "http://47.107.75.83:19871"
        const val BASE_ALI_UAT_PUSH_URL = "http://47.107.75.83:10301"
        const val BASE_ALI_UAT_h5 = "http://uat.wap.worldhds.net"    //"http://47.107.75.83:12020"

        // 这里千万不要改动，release包，默认正式打包环境是阿里，如果debug需要切换请从首页logo的PopWindow点击切换服务  19871
        var BASE_URL = BASE_ALI_URL
        var BASE_h5 = BASE_ALI_h5
//                var BASE_URL = BASE_ALI_UAT_URL
//        var BASE_h5 = BASE_ALI_UAT_h5
        var BASE_PUSH_URL = "http://api.worldhds.net:10300"

        // ===================================千人千面环境======================================================
        // 本地服务器-黄宇飞
        const val RECOMMEND_SERVER_HUANG_URL = "http://192.168.1.34:10200"

        // 本地服务器
        const val RECOMMEND_SERVER_LOCAL_URL = "http://192.168.1.5:10200"

        // 华为测试环境
        const val RECOMMEND_SERVER_HUAWEI_URL = "http://139.159.133.151:10200"

        // 阿里的正式环境
        const val RECOMMEND_SERVER_ALI_URL = "http://api.worldhds.net:10200"

        var RECOMMEND_SERVER_URL = RECOMMEND_SERVER_ALI_URL

        //SP表名
        const val TABLE_PREFS = "lihui_wh"

        //Token Key
        const val KEY_SP_TOKEN = "token"
        const val WEB_FONT_SIZE = "web_font_size"
        const val USER_NICK_NAME = "user_nickname"
        const val USER_APPLEOPENID = "user_appleOpenId"
        const val USER_AREACODE = "user_areaCode"
        const val USER_FBOPENID = "user_fbOpenId"
        const val USER_GGOPENID = "user_ggOpenId"
        const val USER_HEADURL = "user_headUrl"
        const val USER_ID = "user_id"
        const val RONGYUN_USER_ID = "rongyun_user_id"
        const val USER_NAME = "user_name"
        const val USER_PASSWORD = "user_password"
        const val USER_MOBILE = "user_mobile"
        const val USER_QQOPENID = "user_qqOpenId"
        const val USER_WXOPENID = "user_wxOpenId"
        const val INVITE_URER_ID = "inviter_userId"
        const val INVITE_MOBILE = "inviter_mobile"
        const val INVITE_NICK_NAME = "inviter_nick_name"
        const val INVITE_SHARE_URL = "inviter_share_url"
        const val INVITER_USER_TYPE = "inviter_user_type "
        const val USER_TYPE = "user_type "
        const val TRACK_ID = "track_id"
        const val NEWS_TITLE = "new_title"

        const val USER_PHONE = "user_phone"
        const val USER_PHONE_INPUT = "user_phone_input"
        const val USER_HEADIMG = "user_headImg"
        const val USER_USERGRADEID = "userGradeId"
        const val USER_USERGRADENAME = "userGradeName "
        const val TAG_LANGUAGE = "languageSelect"
        const val LOCALE_LANGUAGE = "LOCALE_LANGUAGE"
        const val LOCALE_COUNTRY = "LOCALE_COUNTRY"

        const val MEDIA_ID = "MEDIA_ID"
        const val MEDIA_NAME = "LOCALE_COUNTRY"
        const val MEDIA_INTRODUCE = "MEDIA_INTRODUCE"
        const val MEDIA_HEADPIC = "MEDIA_HEADPIC"
        const val MEDIA_ISFAVOURITE = "MEDIA_ISFAVOURITE"

        const val NEWS_ID = "NEWS_ID"
        const val NEWS_IMAGE = "NEWS_IMAGE"

        const val LIVE_TOKEN = "LIVE_TOKEN"
        const val BANNER_URL = "BANNER_URL"

        const val CUSTOMER_MOBILE = "customer_mobile"//客服电话
        const val FEED_BACK_CONTENT = "feed_back_content"//意见反馈内容
        const val NOTIFY_DIALOG_SHOW_DATE = "notify_dialog_show_date"//通知栏弹窗时间
        const val TUIJIAN_ID = "tuijian_id"//推荐频道的id

        //消息通知的小红点
        const val NOTIFY_TIME = "notify_time" //消息通知的时间
        const val COMENT_NUMBER = "coment_number" //新增评论数
        const val LIKE_NUMBER = "like_number"   //新增点赞数
        const val COUNT_NUMBER = "count_number" //总数


        //倒计时时间
        const val LONG_TIME_COUNT: Long = 60000

        const val PAGENUM = "pageNum"
        const val PAGESIZE = "pageSize"
        const val PAGESIZE_5 = "5"
        const val PAGESIZE_10 = "10"

        //字体大小
        const val FONT_SIZE = "FONT_SIZE"
        const val SP_BASE_URL = "SP_BASE_URL"
        const val SP_BASE_H5 = "SP_BASE_H5"

        //播放状态（自动播放还是在无线网线播放）
        const val VIDEO_AUTO_STATUS = " VIDEO_AUTO_STATUS" //播放状态
        const val AUTO_PLAY = "AUTO_PLAY"  //自动播放
        const val PLAY_OVER_THE_AIR = " PLAY_OVER_THE_AIR"  //无线下

        //播放清晰度（自动播放还是在无线网线播放）
        const val PLAYBACK_CLARITY = " PLAYBACK_CLARITY" //播放状态
        const val SD = "SD"  //标清
        const val HD = " HD"  //高清
        const val UL_TRAHD = "UL_TRAHD"  //超清

        //添加城市名称
        const val CITY_NAME = "CITYNAME"  //超清
        const val CITYN_ID = "CITYNID"  //超清
        const val CITY_ADDRESS = "CITYADDRESS"  //选择地址

        // showType 类别
        const val NEWS = "1"  // 新闻
        const val IMPORTANT_NEWS = "2"  // 要闻
        const val OVERSEAS_CITY = "3"  // 海外名城
        const val FOLLOW = "4"  // 关注
        const val H5 = "5"  // H5
        const val LOCAL = "6"  // 本地-替换
        const val ADD_CHANNEL = "add_channel"  // '+'号添加频道

        const val ADVERTISING_URL = "ADVERTISING_URL"


        const val BASE_IP = "BASE_IP"
        const val FOLLOW_REQ_CODE = 10

        const val CACHE_CHANNEL = "cache_channel"  // 缓存频道
        const val CACHE_NEWS = "cache_news"  // 缓存新闻
        const val CACHE_NEWS_BANNER = "cache_news_banner"  // 缓存新闻banner
        const val CACHE_FOLLOW_MEDIA = "cache_follow_media"  // 缓存关注媒体
        const val CACHE_FOLLOW_NEWS = "cache_follow_news"  // 缓存关注新闻
        const val CACHE_OVERSEAS_CITY = "cache_overseas_city"  // 缓存海外名城
        const val CACHE_VIDEO = "cache_video"  // 缓存视频
        const val CACHE_HUA_MEDIA = "cache_hua_media"  // 缓存华媒
        const val CACHE_CITY = "cache_city"  // 缓存城市
        const val CACHE_MARQUEE = "cache_marquee"  // 缓存跑马灯

        const val INTERS_MAIN_SWITCH = "inters_main_switch"  // 插屏总开关
        const val INTERS_CHANNEL_SWITCH = "inters_channel_switch"  // 插屏-对应频道开关集合
        const val FLOW_DETAIL_SWITCH = "flow_detail_switch"  // 信息流-新闻详情页开关
        const val FLOW_LIST_SWITCH = "flow_list_switch"  // 信息流-新闻列表开关
        const val SPLASH_AD_SWITCH = "splash_ad_switch"  // 开屏-开关
        const val CLOSE_INTERS_CHANNEL = "close_inters_channel"  // 插屏-对应频道是否开启

        const val H5_PUSH = "1"
        const val NEWS_PUSH = "2"
        const val VIDEO_PUSH = "3"
        const val LIVE_PUSH = "4"
        const val TOPIC_PUSH = "5"
        const val HOME_PUSH = "6"

        const val SHARD_INVITER_ID = "shard_inviter_id"
        const val SHARD_ID = "shard_id"
        const val SHARD_INVITER_TIME = "shard_inviter_time"
        const val SHARD_INVITER_TYPE = "shard_inviter_type"

        const val LOCAL_CHANNEL_ID = 128  //  本地频道id

        const val SIGN = "dfasuiyhkuhjk2t5290wouojjeerweeqwqdfd"

        const val APP_AUTHORITIES = "com.qljm.worldhds.fileprovider"
        const val QQ_APPID = "101937585"

        // 网易盾-产品编号
        const val WY_PRODUCT_NUMBER = "YD00801947028806"

        //在微博开发平台为应用申请的App Key
        const val APP_KY = "1067047878"

        //在微博开放平台设置的授权回调页
        const val REDIRECT_URL = "https://api.weibo.com/oauth2/default.html"

        //在微博开放平台为应用申请的高级权限
        const val SCOPE = ("email,direct_messages_read,direct_messages_write,"
                + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                + "follow_app_official_microblog," + "invitation_write")
        const val H5_REQUEST_CODE = 0x9999
        const val H5_RESULT_CODE = 0x8888

    }
}