package com.qljm.swh.base

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.qljm.swh.R
import com.qljm.swh.mvp.contract.IPresenterContractEx
import com.qljm.swh.mvp.di.component.ActivityComponent
import com.qljm.swh.mvp.view.BaseView
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle3.LifecycleProvider
import mvp.ljb.kt.view.MvpAppCompatActivity


/**
 * Activity基类
 * 使用该项目前，请下载MVP Creator
 * https://plugins.jetbrains.com/plugin/10605-mvp-creator
 */
abstract class BaseMvpActivity<out P : IPresenterContractEx> : MvpAppCompatActivity<P>(), BaseView {

    private val SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mLifecycleProvider: LifecycleProvider<*>? =
            AndroidLifecycle.createLifecycleProvider(this)
        LogUtils.d("BaseMvpActivity=============${mLifecycleProvider}")
        getPresenter().registerLifecycle(mLifecycleProvider!!)
        setContentView(getLayoutId())
        init(savedInstanceState)
        initView()
        initData()
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun setupComponent(activityComponent: ActivityComponent)

    protected open fun init(savedInstanceState: Bundle?) {}

    protected open fun initView() {}

    protected open fun initData() {}

    override fun getResources(): Resources {
        val res = super.getResources()
        val newConfig = Configuration()
        newConfig.setToDefaults()
        res.updateConfiguration(newConfig, res.displayMetrics)
        return res
    }

    override fun showToast(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    override fun showToast(text: String?) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    protected open fun goActivity(cls: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, cls)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    protected open fun goActivity(cls: Class<*>) {
        goActivity(cls, null)
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
        supportActionBar?.hide()
    }

    /**
     * 设置亮色标题栏，白底黑字
     */
    fun setLightStateMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBar(this, ContextCompat.getColor(this, R.color.white))
            BarUtils.setStatusBarLightMode(this, true)
        } else {
            setStatusBar(this, ContextCompat.getColor(this, R.color.white))
            setOPPOStatusTextColor(true, this)
        }
    }


    private fun setStatusBar(activity: Activity, color: Int) {
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
     * 设置OPPO手机状态栏字体为黑色(colorOS3.0,6.0以下部分手机)
     *
     * @param lightStatusBar
     * @param activity
     */
    protected open fun setOPPOStatusTextColor(
        lightStatusBar: Boolean,
        activity: Activity
    ) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        var vis = window.decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vis = if (lightStatusBar) {
                vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vis = if (lightStatusBar) {
                vis or SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT
            } else {
                vis and SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT.inv()
            }
        }
        window.decorView.systemUiVisibility = vis
    }

    override fun showLoading() {

    }


    override fun hideLoading() {

    }
}