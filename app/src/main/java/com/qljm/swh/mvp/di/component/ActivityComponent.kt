package com.qljm.swh.mvp.di.component

import android.app.Activity
import com.qljm.swh.mvp.di.module.ActivityModule
import com.qljm.swh.mvp.di.module.LifecycleProviderModule
import com.qljm.swh.mvp.di.scope.ActivityScope
import com.trello.rxlifecycle3.LifecycleProvider
import dagger.Component

@ActivityScope
@Component(modules = [ActivityModule::class, LifecycleProviderModule::class])
interface ActivityComponent {
    fun lifecycleProvider(): LifecycleProvider<*>
    fun activity():Activity
}