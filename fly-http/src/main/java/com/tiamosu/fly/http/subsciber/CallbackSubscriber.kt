package com.tiamosu.fly.http.subsciber

import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.FlyUtils
import com.tiamosu.fly.utils.Platform
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber

/**
 * @author tiamosu
 * @date 2020/3/7.
 */
class CallbackSubscriber(val request: BaseRequest<*>) :
    ErrorHandleSubscriber<okhttp3.ResponseBody>(FlyUtils.getAppComponent().rxErrorHandler()) {

    private val callback: Callback<*>? = request.callback

    override fun onSubscribe(d: Disposable) {
        Platform.postOnMain(Action {
            callback?.onStart(request)
        })
    }

    override fun onNext(t: okhttp3.ResponseBody) {
        try {
            val body = callback?.convertResponse(t)
            val success = Response.success(body)
            Platform.postOnMain(Action {
                callback?.onSuccess(success)
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
            val error = Response.error(t)
            callback?.onError(error)
        })
    }

    override fun onComplete() {
        Platform.postOnMain(Action {
            callback?.onComplete()
        })
    }
}