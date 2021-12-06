@file:JvmName("RxUtils")

package com.tiamosu.fly.http.utils

import com.tiamosu.fly.http.FlyHttp
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.ObservableTransformer

/**
 * @author tiamosu
 * @date 2020/3/18.
 */

fun <T : Any> io(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream ->
        upstream
            .subscribeOn(FlyHttp.scheduler)
            .unsubscribeOn(FlyHttp.scheduler)
    }
}

fun <T : Any> main(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream ->
        upstream.observeOn(AndroidSchedulers.mainThread())
    }
}