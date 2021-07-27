package com.qljm.swh.mvp.presenter

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ObjectUtils
import com.qljm.swh.http.rx.applySchedulers
import com.qljm.swh.http.rx.subscribeEx
import com.qljm.swh.mvp.base.BaseMvpPresenterEx
import com.qljm.swh.mvp.contract.HomeContract
import com.qljm.swh.mvp.model.HomeModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
class HomePresenter : BaseMvpPresenterEx<HomeContract.IView, HomeContract.IModel>(),
    HomeContract.IPresenter {

    override fun registerModel() = HomeModel::class.java

    override fun getChannels() {
        getModel().getChannels().compose(applySchedulers(getMvpView(), lifecycleProvider!!))
            .subscribeEx {
                onNextEx {
                    if (ObjectUtils.isNotEmpty(it?.data)) {
                        getMvpView().getChannels(it.data)
                    }
                }

                onErrorEx {
                    LogUtils.d("onErrorEx==========$it")
                }
            }

    }

}
