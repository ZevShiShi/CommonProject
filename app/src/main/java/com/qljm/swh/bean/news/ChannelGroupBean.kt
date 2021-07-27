package com.qljm.swh.bean.news

import java.io.Serializable

/**
 *   Created by ruirui on 2020/3/25
 */
data class ChannelGroupBean(

    val groupId:Int,
    val groupName:String,
    val channels:MutableList<ChannelBean>,
    var noDragChannels:Int,
    var isEdit: Boolean
) : Serializable {
    override fun toString(): String {
        return "ChannelGroupBean(groupId=$groupId, groupName='$groupName', channels=$channels, isEdit=$isEdit)"
    }
}