@file:JvmName("RxUtils")

package com.tiamosu.fly.http.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author tiamosu
 * @date 2020/3/18.
 */

fun <T> io(): ObservableTransformer<T, T>? {
    return ObservableTransformer { upstream ->
        upstream
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
    }
}

fun <T> main(): ObservableTransformer<T, T>? {
    return ObservableTransformer { upstream ->
        upstream.observeOn(AndroidSchedulers.mainThread())
    }
}