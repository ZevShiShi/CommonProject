package com.qljm.swh.mvp.di.component

import com.qljm.swh.base.BaseApplication
import com.qljm.swh.mvp.di.module.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: BaseApplication)
}