package com.qljm.swh.base

import android.app.Application
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.qljm.swh.utils.AppUtil
import com.qljm.swh.utils.MultiLanguageUtils
import io.reactivex.plugins.RxJavaPlugins

class BaseApplication : Application() {

    companion object {
        lateinit var context: Application
        fun isDebug(): Boolean {
            return AppUtils.isAppDebug()
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        AppUtil.changeBaseUrl()
        registerActivityLifecycleCallbacks(MultiLanguageUtils.callbacks)
        RxJavaPlugins.setErrorHandler { e: Throwable ->
            LogUtils.e("RxError", e)
        }
    }

}