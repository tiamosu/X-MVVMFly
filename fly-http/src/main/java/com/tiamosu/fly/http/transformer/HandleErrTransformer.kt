package com.tiamosu.fly.http.transformer

import com.tiamosu.fly.http.func.HttpResponseFunc
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 * 描述：错误转换Transformer
 *
 * @author tiamosu
 * @date 2020/3/3.
 */
class HandleErrTransformer<T> : ObservableTransformer<T, T> {

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.onErrorResumeNext(HttpResponseFunc())
    }
}