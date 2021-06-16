package com.qljm.swh.mvp.presenter

import mvp.ljb.kt.presenter.BaseMvpPresenter
import com.qljm.swh.mvp.contract.MainContract
import com.qljm.swh.mvp.model.MainModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/06/04
 * @Description input description
 **/
class MainPresenter : BaseMvpPresenter<MainContract.IView, MainContract.IModel>(), MainContract.IPresenter{

    override fun registerModel() = MainModel::class.java

}
