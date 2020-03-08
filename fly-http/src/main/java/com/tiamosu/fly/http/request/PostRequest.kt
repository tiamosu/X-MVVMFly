package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseBodyRequest
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class PostRequest(url: String) : BaseBodyRequest<PostRequest>(url) {

    override fun generateRequest(): Observable<ResponseBody>? {
        when {
            requestBody != null -> {
                return apiService?.postBody(url, requestBody!!)
            }
            json != null -> {
                val body = RequestBody.create(mediaType, json!!)
                return apiService?.postJson(url, body)
            }
            content != null -> {
                val body = RequestBody.create(mediaType, content!!)
                return apiService?.postBody(url, body)
            }
            bytes != null -> {
                val body = RequestBody.create(mediaType, bytes!!)
                return apiService?.postBody(url, body)
            }
            any != null -> {
                return apiService?.postBody(url, any!!)
            }
            else -> return apiService?.post(url, httpParams.urlParamsMap)
        }
    }
}
