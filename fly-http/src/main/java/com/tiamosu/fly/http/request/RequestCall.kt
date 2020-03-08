package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.http.subsciber.CallbackSubscriber
import com.tiamosu.fly.http.utils.RxUtils
import me.jessyan.rxerrorhandler.handler.RetryWithDelay

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class RequestCall<T>(private val request: BaseRequest<T, *>) {

    fun request(callback: Callback<T>) {
        request.callback = callback
        request.generateRequest()?.also { it ->
            it.compose(if (request.isSyncRequest) RxUtils.main() else RxUtils.io())
                .retryWhen(RetryWithDelay(request.retryCount, request.retryDelay))
                .subscribe(CallbackSubscriber(request))
        }
    }
}