package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseRequest
import io.reactivex.Observable
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/2/26.
 */
class GetRequest<T>(url: String) : BaseRequest<T, GetRequest<T>>(url) {

    override fun generateRequest(): Observable<ResponseBody>? {
        return apiService?.get(url, httpParams.urlParamsMap)
    }
}
