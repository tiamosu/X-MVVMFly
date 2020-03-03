package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.callback.CallbackSubscriber
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/2/26.
 */
class GetRequest(url: String) : BaseRequest<GetRequest>(url) {

    fun request() {
        generateRequest()?.apply {
            subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(CallbackSubscriber())
        }
    }

    override fun generateRequest(): Observable<ResponseBody>? {
        return apiManager?.get(url, httpParams.urlParamsMap)
    }
}