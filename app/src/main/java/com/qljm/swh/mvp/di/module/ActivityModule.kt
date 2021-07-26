package com.qljm.swh.mvp.di.module

import android.app.Activity
import com.qljm.swh.mvp.di.scope.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: Activity) {

    @ActivityScope
    @Provides
    fun providesActivity(): Activity {
        return activity
    }
}


