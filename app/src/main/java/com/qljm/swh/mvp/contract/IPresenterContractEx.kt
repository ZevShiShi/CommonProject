package com.qljm.swh.mvp.contract

import com.trello.rxlifecycle3.LifecycleProvider
import mvp.ljb.kt.contract.IPresenterContract

interface IPresenterContractEx : IPresenterContract {
    /**
     * todo 扩展接口实现了对RxLifecycle的注入
     */
    fun registerLifecycle(provider: LifecycleProvider<*>)
}