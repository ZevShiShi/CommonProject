package com.qljm.swh.mvp.contract

import com.qljm.swh.bean.base.BaseArrayBean
import com.qljm.swh.bean.news.ChannelGroupBean
import com.qljm.swh.mvp.base.IPresenterContractEx
import com.qljm.swh.mvp.view.BaseView
import io.reactivex.Observable
import mvp.ljb.kt.contract.IModelContract

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
interface HomeContract {

    interface IView : BaseView {
        fun getChannels(datas: MutableList<ChannelGroupBean>)
    }

    interface IPresenter : IPresenterContractEx {
        fun getChannels()
    }

    interface IModel : IModelContract {
        fun getChannels(): Observable<BaseArrayBean<ChannelGroupBean>>
    }
}
