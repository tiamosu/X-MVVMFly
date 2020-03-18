package com.tiamosu.fly.http.subscriber

import com.tiamosu.fly.http.cache.model.CacheResult
import com.tiamosu.fly.http.callback.CacheResultCallback
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.postOnMain
import io.reactivex.functions.Action

/**
 * @author tiamosu
 * @date 2020/3/11.
 */
class CacheCallbackSubscriber(val request: BaseRequest<*>) : BaseSubscriber<CacheResult<String>>() {

    private val callback = request.callback as? CacheResultCallback<*>

    override fun onStart() {
        postOnMain(Action {
            callback?.onStart()
        })
    }

    override fun onNext(t: CacheResult<String>) {
        try {
            callback?.convertResponse(t.data)
            postOnMain(Action {
                callback?.onSuccess(t)
            })
        } catch (t: Throwable) {
            onError(t)
        }
    }

    override fun onError(t: Throwable) {
        if (request.isGlobalErrorHandle) {
            super.onError(t)
        }
        postOnMain(Action {
            callback?.onError(t)
            callback?.onFinish()
        })
    }

    override fun onComplete() {
        postOnMain(Action {
            callback?.onFinish()
        })
    }
}