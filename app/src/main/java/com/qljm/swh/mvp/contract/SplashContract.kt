package com.qljm.swh.mvp.contract

import com.qljm.swh.bean.BannerBean
import com.qljm.swh.bean.base.BaseBean
import com.qljm.swh.mvp.base.IPresenterContractEx
import com.qljm.swh.mvp.view.BaseView
import io.reactivex.Observable
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
interface SplashContract {

    interface IView : BaseView {
        fun getBanner(bean: BannerBean)
    }

    interface IPresenter : IPresenterContractEx {
        fun getBanner()
    }

    interface IModel : IModelContract {
        fun getBanner(): Observable<BaseBean<BannerBean>>
    }
}
