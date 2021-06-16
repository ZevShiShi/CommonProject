package com.qljm.swh.base

import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.fragment.BaseMvpFragment

abstract class BaseMvpLazyFragment<out P : IPresenterContract> : BaseMvpFragment<P>() {

    private var lazyLoad = false

    override fun onResume() {
        super.onResume()
        // todo:懒加载 FragmentPagerAdapter 使用这种方式BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        if (!lazyLoad) {
            lazyLoad = true
            loadLazyData()
        }
    }

    abstract fun loadLazyData()
}

