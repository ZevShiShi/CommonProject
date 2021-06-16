package com.qljm.swh.mvp.view.act

import com.qljm.swh.R
import com.qljm.swh.base.BaseMvpActivity
import com.qljm.swh.mvp.contract.MainContract
import com.qljm.swh.mvp.presenter.MainPresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/06/04
 * @Description input description
 **/
class MainActivity : BaseMvpActivity<MainContract.IPresenter>() , MainContract.IView {

    override fun registerPresenter() = MainPresenter::class.java

    override fun getLayoutId() = R.layout.activity_main

}
