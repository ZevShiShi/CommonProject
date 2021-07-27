package com.qljm.swh.base

import android.os.Bundle
import com.qljm.swh.mvp.base.IPresenterContractEx
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.view.IBaseView

abstract class MvpAppCompatActivityEx<out P : IPresenterContractEx> : RxAppCompatActivity(),
    IBaseView<P>,
    IViewContract {

    private var mPresenter: P? = null

    protected fun getPresenter() = mPresenter!!

    private fun initPresenter(): P {
        mPresenter = registerPresenter().newInstance()
        mPresenter?.register(this)
        return mPresenter!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
        mPresenter?.onCreate()
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mPresenter?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (mPresenter != null) {
//            mPresenter!!.onDestroy()
//            mPresenter = null
//        }
    }
}