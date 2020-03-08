package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.api.ApiService
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.FlyUtils
import io.reactivex.Observable
import okhttp3.Response

/**
 * 描述：自定义请求，例如你有自己的ApiService
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
open class CustomRequest<T>(url: String) : BaseRequest<T, CustomRequest<T>>(url) {
    private var observable: Observable<Response>? = null

    fun <R : ApiService> create(serviceClass: Class<R>): R? {
        return FlyUtils.getAppComponent().repositoryManager()
            .obtainRetrofitService(serviceClass, retrofit)
    }

    fun apiCall(observable: Observable<Response>?, callback: Callback<T>? = null) {
        this.observable = observable
        val newCallback = callback ?: Callback.DefaultCallback()
        RequestCall(this)
            .request(newCallback)
    }

    override fun generateRequest(): Observable<Response>? {
        return observable
    }
}