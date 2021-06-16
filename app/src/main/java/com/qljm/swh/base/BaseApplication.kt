package com.qljm.swh.base

import android.app.Application
import com.blankj.utilcode.util.AppUtils
import com.qljm.swh.utils.AppUtil
import com.qljm.swh.utils.MultiLanguageUtils

class BaseApplication : Application() {

    companion object {
        lateinit var app: Application

        fun isDebug(): Boolean {
            return AppUtils.isAppDebug()
        }
    }


    override fun onCreate() {
        super.onCreate()
        app = this
        AppUtil.changeBaseUrl()
        registerActivityLifecycleCallbacks(MultiLanguageUtils.callbacks)
    }


}