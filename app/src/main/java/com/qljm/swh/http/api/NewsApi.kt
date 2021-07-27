package com.qljm.swh.http.api

import com.qljm.swh.bean.base.BaseArrayBean
import com.qljm.swh.bean.news.ChannelGroupBean
import io.reactivex.Observable
import retrofit2.http.GET

interface NewsApi {

    @GET("/app/channel/channerlList")
    fun getChannels(): Observable<BaseArrayBean<ChannelGroupBean>>
}