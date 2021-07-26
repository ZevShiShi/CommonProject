package com.qljm.swh.http.rx

import android.content.Context
import com.qljm.swh.mvp.view.BaseView
import com.trello.rxlifecycle3.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


fun <T> Observable<T>.subscribeEx(func: (BaseObserver<T>.() -> Unit)) {
    subscribe(BaseObserver<T>().apply(func))
}

fun <T> Observable<T>.subscribeNet(context: Context, func: (BaseNetObserver<T>.() -> Unit)) {
    subscribe(BaseNetObserver<T>(context).apply(func))
}

fun <T> applySchedulers(view: BaseView, lifecycleProvider: LifecycleProvider<*>): ObservableTransformer<T, T>? {
    return ObservableTransformer { observable ->
        observable.subscribeOn(Schedulers.io())
            .doOnSubscribe {
                view.showLoading() //显示进度条
            }
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally {
                view.hideLoading() //隐藏进度条
            }.compose(lifecycleProvider.bindToLifecycle())
    }
}