package com.qljm.swh.mvp.di.module

import com.lihui.base.data.http.RetrofitFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RetrofitFactoryModule {

    @Singleton
    @Provides
    fun providesRetrofitFactory(): RetrofitFactory {
        return RetrofitFactory()
    }
}