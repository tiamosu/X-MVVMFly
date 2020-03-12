package com.tiamosu.fly.http.subscriber

import com.tiamosu.fly.http.callback.ResultCallback
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.Platform
import io.reactivex.functions.Action

/**
 * @author tiamosu
 * @date 2020/3/7.
 */
class CallbackSubscriber<T>(val request: BaseRequest<*>) : BaseSubscriber<okhttp3.ResponseBody>() {

    @Suppress("UNCHECKED_CAST")
    private val callback = request.callback as? ResultCallback<T>

    override fun onStart() {
        Platform.postOnMain(Action {
            callback?.onStart()
        })
    }

    override fun onNext(t: okhttp3.ResponseBody) {
        try {
            val body = callback?.convertResponse(t)
            Platform.postOnMain(Action {
                callback?.onSuccess(body)
            })
        } catch (t: Throwable) {
            onError(t)
        }
    }

    override fun onError(t: Throwable) {
        if (request.isGlobalErrorHandle) {
            super.onError(t)
        }
        Platform.postOnMain(Action {
            callback?.onError(t)
            callback?.onFinish()
        })
    }

    override fun onComplete() {
        Platform.postOnMain(Action {
            callback?.onFinish()
        })
    }
}