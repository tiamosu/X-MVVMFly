package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseRequest
import io.reactivex.Observable
import okhttp3.Response

/**
 * @author tiamosu
 * @date 2020/2/26.
 */
class GetRequest<T>(url: String) : BaseRequest<T, GetRequest<T>>(url) {

    override fun generateRequest(): Observable<Response>? {
        return apiService?.get(url, httpParams.urlParamsMap)
    }
}
