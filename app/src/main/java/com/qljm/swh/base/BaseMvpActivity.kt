package com.qljm.swh.base

import android.R
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.blankj.utilcode.util.BarUtils
import mvp.ljb.kt.act.BaseMvpAppCompatActivity
import mvp.ljb.kt.contract.IPresenterContract

abstract class BaseMvpActivity<out P : IPresenterContract> : BaseMvpAppCompatActivity<P>() {

    val BUNDLE_FRAGMENTS_KEY = "android:support:fragments"

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        savedInstanceState?.remove(BUNDLE_FRAGMENTS_KEY)
        super.onCreate(savedInstanceState)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.remove(BUNDLE_FRAGMENTS_KEY)
    }


    /**
     * 设置亮色标题栏，白底黑字
     */
    protected open fun setLightStateMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBar(this, resources.getColor(R.color.white, null))
            BarUtils.setStatusBarLightMode(this, true)
        } else {
            setStatusBar(this, resources.getColor(R.color.white))
        }
    }

    protected open fun setStatusBar(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以上直接设置状态栏颜色
            activity.window.statusBarColor = color
        } else {
            //根布局添加占位状态栏
            val decorView = activity.window.decorView as ViewGroup
            val statusBarView = View(activity)
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
            )
            statusBarView.setBackgroundColor(color)
            decorView.addView(statusBarView, lp)
        }
    }

    /**
     * 沉浸式效果
     */
    fun setImmersive() {
        // 状态栏透明，导航栏不透明
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 沉浸式效果
     */
    fun setImmersive2() {
        // 状态栏透明，导航栏透明
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
            window.navigationBarColor = Color.TRANSPARENT
            window.statusBarColor = Color.TRANSPARENT
        }
        actionBar?.hide()
    }
}