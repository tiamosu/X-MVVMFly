package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseBodyRequest
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class PutRequest<T>(url: String) : BaseBodyRequest<T, PutRequest<T>>(url) {

    override fun generateRequest(): Observable<ResponseBody>? {
        when {
            requestBody != null -> {
                return apiService?.putBody(url, requestBody!!)
            }
            json != null -> {
                val body = RequestBody.create(mediaType, json!!)
                return apiService?.putJson(url, body)
            }
            content != null -> {
                val body = RequestBody.create(mediaType, content!!)
                return apiService?.putBody(url, body)
            }
            bytes != null -> {
                val body = RequestBody.create(mediaType, bytes!!)
                return apiService?.putBody(url, body)
            }
            any != null -> {
                return apiService?.putBody(url, any!!)
            }
            else -> return apiService?.put(url, httpParams.urlParamsMap)
        }
    }
}
