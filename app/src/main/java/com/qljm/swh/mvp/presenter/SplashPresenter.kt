package com.qljm.swh.mvp.presenter

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ObjectUtils
import com.qljm.swh.http.rx.applySchedulers
import com.qljm.swh.http.rx.subscribeEx
import com.qljm.swh.mvp.contract.SplashContract
import com.qljm.swh.mvp.model.SplashModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
class SplashPresenter() :
    BaseMvpPresenterEx<SplashContract.IView, SplashContract.IModel>(),
    SplashContract.IPresenter {

    override fun registerModel() = SplashModel::class.java

    override fun getBanner() {
        getModel().getBanner().compose(applySchedulers(getMvpView(), lifecycleProvider!!))
            .subscribeEx {
                onNextEx {
                    LogUtils.e("getBanner=========$it")
                    if (ObjectUtils.isEmpty(it?.data)) return@onNextEx
                    getMvpView().getBanner(it.data)
                }

                onErrorEx {
                    LogUtils.e("getBanner=========$it")
                }
            }
    }
}
