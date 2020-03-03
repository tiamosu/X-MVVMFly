package com.tiamosu.fly.http.func

import com.tiamosu.fly.http.exception.ApiException
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * 描述：异常转换处理
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
class HttpResponseFunc<T> : Function<Throwable, Observable<T>> {

    @Throws(Exception::class)
    override fun apply(throwable: Throwable): Observable<T> {
        return Observable.error(ApiException.handleException(throwable))
    }
}