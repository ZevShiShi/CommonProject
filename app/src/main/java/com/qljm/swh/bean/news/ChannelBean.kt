package com.qljm.swh.bean.news

import java.io.Serializable

data class ChannelBean(
    val id: Int,
    var name: String,
    val seq: Int,
    val groupId: Int,
    val showType: String,
    val url: String,
    val isUser: Int,
    val isDefault: String,
    val isDefaultChannelId: String,
    val children: MutableList<ChannelDropBean>,
    var isEdit: Boolean = false,
    var selectIndex: Int = 0
) : Serializable {
    override fun toString(): String {
        return "ChannelBean(id=$id, name='$name', seq=$seq, groupId=$groupId, showType='$showType', url='$url', isUser=$isUser, isDefault='$isDefault', isDefaultChannelId='$isDefaultChannelId', children=$children, isEdit=$isEdit, selectIndex=$selectIndex)"
    }
}

//id (integer, optional): 频道ID ,
//name (string, optional): 频道名称 ,
//seq (integer, optional): 频道顺序 ,
//groupId (integer, optional): 频道组ID ,