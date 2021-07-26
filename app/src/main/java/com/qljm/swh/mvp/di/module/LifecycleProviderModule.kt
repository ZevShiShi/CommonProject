package com.qljm.swh.mvp.di.module

import com.qljm.swh.mvp.di.scope.ActivityScope
import com.trello.rxlifecycle3.LifecycleProvider
import dagger.Module
import dagger.Provides


@Module
class LifecycleProviderModule(private val lifecycleProvider: LifecycleProvider<*>) {

    @ActivityScope
    @Provides
    fun provideLifecycleProvider(): LifecycleProvider<*> {
        return this.lifecycleProvider
    }
}


