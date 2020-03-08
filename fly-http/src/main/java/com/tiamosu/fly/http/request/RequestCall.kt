package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.http.subsciber.CallbackSubscriber
import com.tiamosu.fly.http.utils.RxUtils
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class RequestCall(private val request: BaseRequest<*>) {

    fun request(callback: Callback<*>) {
        request.callback = callback
        request.generateRequest()?.also { it ->
            it.compose(if (request.isSyncRequest) RxUtils.main<ResponseBody>() else RxUtils.io<ResponseBody>())
                .retryWhen(RetryWithDelay(request.retryCount, request.retryDelay))
                .subscribe(CallbackSubscriber(request))
        }
    }
}
