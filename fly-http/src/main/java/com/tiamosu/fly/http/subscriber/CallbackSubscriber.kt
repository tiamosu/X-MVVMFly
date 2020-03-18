package com.tiamosu.fly.http.subscriber

import com.tiamosu.fly.http.callback.ResultCallback
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.postOnMain
import io.reactivex.functions.Action

/**
 * @author tiamosu
 * @date 2020/3/7.
 */
class CallbackSubscriber<T>(val request: BaseRequest<*>) : BaseSubscriber<okhttp3.ResponseBody>() {

    @Suppress("UNCHECKED_CAST")
    private val callback = request.callback as? ResultCallback<T>

    override fun onStart() {
        postOnMain(Action {
            callback?.onStart()
        })
    }

    override fun onNext(t: okhttp3.ResponseBody) {
        try {
            val body = callback?.convertResponse(t)
            postOnMain(Action {
                val response = Response.success(false, body)
                callback?.onSuccess(response)
            })
        } catch (throwable: Throwable) {
            onError(false, throwable)
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