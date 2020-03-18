@file:JvmName("RxUtils")

package com.tiamosu.fly.http.utils

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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