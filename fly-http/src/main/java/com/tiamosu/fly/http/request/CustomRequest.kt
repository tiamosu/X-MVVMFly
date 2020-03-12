package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.FlyUtils
import com.tiamosu.fly.utils.Preconditions
import io.reactivex.Observable
import okhttp3.ResponseBody

/**
 * 描述：自定义请求，例如你有自己的ApiService
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
open class CustomRequest(url: String) : BaseRequest<CustomRequest>(url) {
    private var observable: Observable<ResponseBody>? = null

    fun <R> create(serviceClass: Class<R>): R? {
        checkvalidate()
        return FlyUtils.getAppComponent().repositoryManager()
            .obtainRetrofitService(serviceClass, retrofit)
    }

    fun <T> apiCall(observable: Observable<ResponseBody>?, callback: Callback<T>) {
        checkvalidate()
        this.observable = observable
        RequestCall(this)
            .execute(callback)
    }

    private fun checkvalidate() {
        Preconditions.checkNotNull(retrofit, "请先调用build()才能使用")
    }

    override fun generateRequest(): Observable<ResponseBody>? {
        return observable
    }
}