package com.qljm.swh.base

import android.os.Bundle
import com.trello.rxlifecycle3.components.support.RxFragment
import mvp.ljb.kt.contract.IPresenterContract
import mvp.ljb.kt.contract.IViewContract
import mvp.ljb.kt.view.IBaseView

abstract class MvpFragmentEx<out P : IPresenterContract> : RxFragment(), IBaseView<P>, IViewContract {

    private var mPresenter: P? = null

    protected fun getPresenter() = mPresenter!!

    private fun initPresenter() {
        mPresenter = registerPresenter().newInstance()
        mPresenter?.register(this)
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
        if (mPresenter != null) {
            mPresenter!!.onDestroy()
            mPresenter = null
        }
    }
}