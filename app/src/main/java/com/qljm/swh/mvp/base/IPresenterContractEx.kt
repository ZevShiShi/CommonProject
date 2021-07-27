package com.qljm.swh.mvp.base

import com.trello.rxlifecycle3.LifecycleProvider
import mvp.ljb.kt.contract.IPresenterContract

/**
 * 需要对Presenter接口继承替换
 * todo 扩展接口实现了对RxLifecycle的注入
 */
interface IPresenterContractEx : IPresenterContract {

    fun registerLifecycle(provider: LifecycleProvider<*>)
}