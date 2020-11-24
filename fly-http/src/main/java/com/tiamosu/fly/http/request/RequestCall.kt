package com.tiamosu.fly.http.request

import android.annotation.SuppressLint
import com.tiamosu.fly.http.callback.CacheResultCallback
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.callback.NoCacheResultCallback
import com.tiamosu.fly.http.func.StringResultFunc
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.http.subscriber.CacheCallbackSubscriber
import com.tiamosu.fly.http.subscriber.NoCacheCallbackSubscriber
import com.tiamosu.fly.http.utils.io
import com.tiamosu.fly.http.utils.main
import io.reactivex.rxjava3.disposables.Disposable
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class RequestCall(private val request: BaseRequest<*>) {

    @SuppressLint("CheckResult")
    fun <T> execute(callback: Callback<T>): Disposable? {
        request.callback = callback
        return when (callback) {
            is CacheResultCallback -> {
                request.generateRequest()
                    ?.map(StringResultFunc())
                    ?.compose(if (request.isSyncRequest) main<String>() else io<String>())
                    ?.compose(request.rxCache.transformer(request.cacheMode))
                    ?.retryWhen(RetryWithDelay(request.retryCount, request.retryDelay))
                    ?.subscribeWith(CacheCallbackSubscriber<T>(request))
            }
            is NoCacheResultCallback -> {
                request.generateRequest()
                    ?.compose(if (request.isSyncRequest) main<ResponseBody>() else io<ResponseBody>())
                    ?.retryWhen(RetryWithDelay(request.retryCount, request.retryDelay))
                    ?.subscribeWith(NoCacheCallbackSubscriber<T>(request))
            }
            else -> throw IllegalArgumentException("Callback is must be CacheResultCallback or NoCacheResultCallback!")
        }
    }
}
