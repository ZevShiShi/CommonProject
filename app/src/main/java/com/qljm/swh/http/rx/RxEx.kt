package com.qljm.swh.http.rx

import android.content.Context
import io.reactivex.Observable

fun <T> Observable<T>.subscribeEx(func: (BaseObserver<T>.() -> Unit)) {
    subscribe(BaseObserver<T>().apply(func))
}

fun <T> Observable<T>.subscribeNet(context: Context, func: (BaseNetObserver<T>.() -> Unit)) {
    subscribe(BaseNetObserver<T>(context).apply(func))
}