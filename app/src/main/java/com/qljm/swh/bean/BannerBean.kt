package com.qljm.swh.bean

import java.io.Serializable

class BannerBean : Serializable {
    var id: String? = null
    var name: String? = null
    var bannerType: String? = "img"  //广告类型 1:广告页 2:banner
    var bannerUrl: String? = null   //Banner图的url
    var startTime: String? = null
    var endTime: String? = null
    var type: String? = null    //跳转类型 1:链接 2:新闻资讯
    var typeContent: String? = null
    var sort: String? = null    //排序
    var zhVideo: String = ""    // 中文视频
    var enVideo: String = ""    // 英文视频
    var redirectType: String? = null    //跳转类型 1 链接 2 资讯 3:视频资讯 4：直播 5：专题
    var redirectUrl: String? =
        null //跳转url redirectType=1时，redirect url为链接的url，redirectType=2时，redirectUrl为资讯的ID

    override fun toString(): String {
        return "BannerBean(id=$id, name=$name, bannerType=$bannerType, bannerUrl=$bannerUrl, startTime=$startTime, endTime=$endTime, type=$type, typeContent=$typeContent, sort=$sort, redirectType=$redirectType, redirectUrl=$redirectUrl)"
    }

    fun getViewType(): Int {
        if (redirectType == "6") {
            return 1
        }
        return 0
    }
}