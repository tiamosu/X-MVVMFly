package com.tiamosu.fly.http.subscriber

import com.tiamosu.fly.http.callback.FileCallback
import com.tiamosu.fly.http.callback.NoCacheResultCallback
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.postOnMain
import io.reactivex.rxjava3.disposables.Disposable

/**
 * @author tiamosu
 * @date 2020/3/7.
 */
class NoCacheCallbackSubscriber<T>(val request: BaseRequest<*>) :
    BaseSubscriber<okhttp3.ResponseBody>() {

    @Suppress("UNCHECKED_CAST")
    private val callback = request.callback as? NoCacheResultCallback<T>

    override fun onStart(disposable: Disposable) {
        postOnMain {
            callback?.onStart(disposable)
        }
    }

    override fun onNext(t: okhttp3.ResponseBody) {
        try {
            val body = callback?.convertResponse(t)
            if (callback !is FileCallback) {
                postOnMain {
                    val response = Response.success(false, body)
                    callback?.onSuccess(response)
                }
            }
        } catch (throwable: Throwable) {
            errorHandle(throwable)
        }
    }

    override fun onError(t: Throwable) {
        if (request.isGlobalErrorHandle) {
            super.onError(t)
        }
        errorHandle(t)
    }

    override fun onComplete() {
        if (callback !is FileCallback) {
            postOnMain {
                callback?.onFinish()
            }
        }
    }

    private fun errorHandle(throwable: Throwable?) {
        postOnMain {
            val response = Response.error<T>(false, throwable)
            callback?.onError(response)
            callback?.onFinish()
        }
    }
}