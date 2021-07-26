package com.qljm.swh.mvp.presenter

import com.qljm.swh.mvp.contract.IPresenterContractEx
import com.trello.rxlifecycle3.LifecycleProvider
import mvp.ljb.kt.contract.IModelContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.presenter.BaseMvpPresenter

/**
 *  扩展对RxLifecycle的注入
 */
abstract class BaseMvpPresenterEx<out V : IViewContract, out M : IModelContract> :
    BaseMvpPresenter<V, M>(),
    IPresenterContractEx {

    protected var lifecycleProvider: LifecycleProvider<*>? = null

    override fun registerLifecycle(provider: LifecycleProvider<*>) {
        lifecycleProvider = provider
    }
}