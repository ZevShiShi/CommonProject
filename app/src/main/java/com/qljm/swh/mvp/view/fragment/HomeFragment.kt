package com.qljm.swh.mvp.view.fragment

import com.blankj.utilcode.util.LogUtils
import com.qljm.swh.R
import com.qljm.swh.base.BaseMvpLazyFragment
import com.qljm.swh.bean.news.ChannelGroupBean
import com.qljm.swh.mvp.contract.HomeContract
import com.qljm.swh.mvp.presenter.HomePresenter

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
class HomeFragment : BaseMvpLazyFragment<HomeContract.IPresenter>(), HomeContract.IView {

    override fun registerPresenter() = HomePresenter::class.java

    override fun getLayoutId() = R.layout.fragment_home


    override fun initView() {
        super.initView()
    }

    override fun initData() {
        super.initData()
        getPresenter().getChannels()
    }

    override fun getChannels(datas: MutableList<ChannelGroupBean>) {
        LogUtils.d("getChannels==============$datas")

    }

}
