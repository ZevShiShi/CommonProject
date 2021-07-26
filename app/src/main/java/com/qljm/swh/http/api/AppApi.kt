package com.qljm.swh.http.api

import com.qljm.swh.bean.BannerBean
import com.qljm.swh.bean.base.BaseBean
import io.reactivex.Observable
import retrofit2.http.GET

interface AppApi {

    @GET("/app/banner/getStartBanner")
    fun getBanner(): Observable<BaseBean<BannerBean>>
}