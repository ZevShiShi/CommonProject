package com.qljm.swh.mvp.presenter

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ObjectUtils
import com.qljm.swh.http.rx.applySchedulers
import com.qljm.swh.http.rx.subscribeEx
import com.qljm.swh.mvp.base.BaseMvpPresenterEx
import com.qljm.swh.mvp.contract.SplashContract
import com.qljm.swh.mvp.model.SplashModel

class SplashPresenter :
    BaseMvpPresenterEx<SplashContract.IView, SplashContract.IModel>(),
    SplashContract.IPresenter {

    override fun registerModel() = SplashModel::class.java

    override fun getBanner() {
//        LogUtils.e("getBanner=========${lifecycleProvider}")
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
