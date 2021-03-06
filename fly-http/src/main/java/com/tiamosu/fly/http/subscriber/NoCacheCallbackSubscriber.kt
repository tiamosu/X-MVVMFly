package com.tiamosu.fly.http.subscriber

import com.tiamosu.fly.http.callback.NoCacheCustomCallback
import com.tiamosu.fly.http.callback.NoCacheResultCallback
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.launchMain
import io.reactivex.rxjava3.disposables.Disposable

/**
 * @author tiamosu
 * @date 2020/3/7.
 */
class NoCacheCallbackSubscriber<T>(val request: BaseRequest<*>) :
    BaseSubscriber<okhttp3.ResponseBody>() {

    @Suppress("UNCHECKED_CAST")
    private val callback by lazy { request.callback as? NoCacheResultCallback<T> }

    override fun onStart(disposable: Disposable) {
        launchMain {
            callback?.onStart(disposable)
        }
    }

    override fun onNext(t: okhttp3.ResponseBody) {
        try {
            val body = callback?.convertResponse(t)
            //若回调为 NoCacheCustomCallback，则交由 NoCacheCustomCallback 内部自行处理
            if (callback is NoCacheCustomCallback) {
                return
            }
            launchMain {
                val response = Response.success(false, body)
                callback?.onSuccess(response)
            }
        } catch (throwable: Throwable) {
            error(throwable)
        }
    }

    override fun onError(t: Throwable) {
        if (request.isGlobalErrorHandle) {
            super.onError(t)
        }
        error(t)
    }

    override fun onComplete() {
        if (callback !is NoCacheCustomCallback) {
            launchMain {
                callback?.onFinish()
            }
        }
    }

    private fun error(throwable: Throwable?) {
        launchMain {
            val response = Response.error<T>(false, throwable)
            callback?.onError(response)
            callback?.onFinish()
        }
    }
}