package com.tiamosu.fly.http.utils

import com.tiamosu.fly.http.func.HandleFuc
import com.tiamosu.fly.http.func.HttpResponseFunc
import com.tiamosu.fly.http.model.ApiResult
import com.tiamosu.fly.http.utils.FlyHttpLog.i
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author tiamosu
 * @date 2020/3/2.
 */
object RxUtil {

    fun <T> io2main(): ObservableTransformer<T?, T?> {
        return ObservableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable -> i("+++doOnSubscribe+++" + disposable.isDisposed) }
                .doFinally { i("+++doFinally+++") }
        }
    }

    fun <T> io2mainWithApiresult(): ObservableTransformer<ApiResult<T?>, T?> {
        return ObservableTransformer { upstream ->
            upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(HandleFuc())
                .doOnSubscribe { disposable -> i("+++doOnSubscribe+++" + disposable.isDisposed) }
                .doFinally { i("+++doFinally+++") }
                .onErrorResumeNext(HttpResponseFunc<T>())
        }
    }

    fun <T> main(): ObservableTransformer<ApiResult<T?>, T?> {
        return ObservableTransformer { upstream ->
            upstream
                .map(HandleFuc())
                .doOnSubscribe { disposable -> i("+++doOnSubscribe+++" + disposable.isDisposed) }
                .doFinally { i("+++doFinally+++") }
                .onErrorResumeNext(HttpResponseFunc<T>())
        }
    }
}