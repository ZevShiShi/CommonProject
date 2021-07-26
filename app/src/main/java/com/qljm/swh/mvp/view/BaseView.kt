package com.qljm.swh.mvp.view

import mvp.ljb.kt.contract.IViewContract

interface BaseView : IViewContract {
    fun showLoading()
    fun hideLoading()
}