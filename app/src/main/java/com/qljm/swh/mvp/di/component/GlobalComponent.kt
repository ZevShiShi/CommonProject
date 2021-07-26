package com.qljm.swh.mvp.di.component

import com.lihui.base.data.http.RetrofitFactory
import com.qljm.swh.mvp.di.module.RetrofitFactoryModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RetrofitFactoryModule::class])
interface GlobalComponent {
    fun retrofitFactory(): RetrofitFactory
}