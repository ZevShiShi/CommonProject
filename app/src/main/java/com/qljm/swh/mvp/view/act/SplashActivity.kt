package com.qljm.swh.mvp.view.act

import com.blankj.utilcode.util.LogUtils
import com.qljm.swh.R
import com.qljm.swh.base.BaseMvpActivity
import com.qljm.swh.bean.BannerBean
import com.qljm.swh.mvp.contract.SplashContract
import com.qljm.swh.mvp.presenter.SplashPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
class SplashActivity : BaseMvpActivity<SplashContract.IPresenter>(), SplashContract.IView {

    override fun registerPresenter() = SplashPresenter::class.java

    override fun getLayoutId() = R.layout.activity_splash

    override fun initView() {
        super.initView()
    }

    override fun initData() {
        super.initData()
        getPresenter().getBanner()
    }

    override fun getBanner(bean: BannerBean) {
        LogUtils.e("SplashActivity getBanner=========$bean")
    }
}
