package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.request.base.BaseRequest
import com.tiamosu.fly.utils.getAppComponent
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody

/**
 * 描述：自定义请求，例如你有自己的ApiService
 *
 * @author tiamosu
 * @date 2020/3/6.
 */
open class CustomRequest(url: String) : BaseRequest<CustomRequest>(url) {
    private var observable: Observable<ResponseBody>? = null

    inline fun <reified R> create(): R? {
        return create(R::class.java)
    }

    fun <R> create(serviceClass: Class<R>): R? {
        checkValidate()
        return getAppComponent().repositoryManager()
            .obtainRetrofitService(serviceClass, retrofit)
    }

    fun <T> apiCall(observable: Observable<ResponseBody>?, callback: Callback<T>) {
        checkValidate()
        this.observable = observable
        RequestCall(this)
            .execute(callback)
    }

    private fun checkValidate() {
        if (retrofit == null) {
            build()
        }
    }

    override fun generateRequest(): Observable<ResponseBody>? {
        return observable
    }
}