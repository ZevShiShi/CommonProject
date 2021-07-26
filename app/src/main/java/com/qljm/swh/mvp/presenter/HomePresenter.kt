package com.qljm.swh.mvp.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.qljm.swh.mvp.contract.HomeContract
import com.qljm.swh.mvp.model.HomeModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
class HomePresenter : BaseMvpPresenter<HomeContract.IView, HomeContract.IModel>(), HomeContract.IPresenter{

    override fun registerModel() = HomeModel::class.java

}
