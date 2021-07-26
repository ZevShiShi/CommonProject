package com.qljm.swh.mvp.di.component

import com.qljm.swh.mvp.di.module.SplashModule
import com.qljm.swh.mvp.di.scope.PerComponsentScope
import com.qljm.swh.mvp.view.act.SplashActivity
import dagger.Component

@PerComponsentScope
@Component(dependencies = [ActivityComponent::class], modules = [SplashModule::class])
interface SplashComponent {
    fun inject(activity: SplashActivity)
}