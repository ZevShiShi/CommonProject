package com.qljm.swh.mvp.base

import com.blankj.utilcode.util.LogUtils
import com.lihui.base.data.http.RetrofitFactory
import com.qljm.swh.mvp.di.component.DaggerAppComponent
import com.qljm.swh.mvp.di.module.AppModule
import mvp.ljb.kt.model.BaseModel
import javax.inject.Inject

abstract class BaseModelEx : BaseModel() {

    @Inject
    protected lateinit var retrofitFactory: RetrofitFactory

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder().appModule(AppModule()).build().inject(this)
        LogUtils.d("BaseModelEx==========注入====$retrofitFactory")
    }

}