package com.tiamosu.fly.http.subscriber

import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.callback.CacheResultCallback
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.postOnMain
import io.reactivex.functions.Action

/**
 * @author tiamosu
 * @date 2020/3/11.
 */
class CacheCallbackSubscriber<T>(val request: BaseRequest<*>) :
    BaseSubscriber<CacheResult<String>>() {

    @Suppress("UNCHECKED_CAST")
    private val callback = request.callback as? CacheResultCallback<T>

    override fun onStart() {
        postOnMain(Action {
            callback?.onStart()
        })
    }

    override fun onNext(t: CacheResult<String>) {
        try {
            val body = callback?.convertResponse(t.data)
            postOnMain(Action {
                val response = Response.success(t.isFromCache, body)
                callback?.onSuccess(response)
            })
        } catch (throwable: Throwable) {
            onError(t.isFromCache, throwable)
        }
    }

    override fun onError(t: Throwable) {
        if (request.isGlobalErrorHandle) {
            super.onError(t)
        }
        onError(false, t)
    }

    override fun onComplete() {
        postOnMain(Action {
            callback?.onFinish()
        })
    }

    private fun onError(isFromCache: Boolean, throwable: Throwable?) {
        postOnMain(Action {
            val response = Response.error<T>(isFromCache, throwable)
            callback?.onError(response)
            callback?.onFinish()
        })
    }
}