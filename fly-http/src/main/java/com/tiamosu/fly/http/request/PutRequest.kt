package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseBodyRequest
import io.reactivex.Observable
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class PutRequest(url: String) : BaseBodyRequest<PutRequest>(url) {

    override fun generateRequest(): Observable<ResponseBody>? {
        when {
            requestBody != null -> {
                return apiService?.putBody(url, requestBody!!)
            }
            json != null -> {
                val body = json!!.toRequestBody(mediaType)
                return apiService?.putJson(url, body)
            }
            content != null -> {
                val body = content!!.toRequestBody(mediaType)
                return apiService?.putBody(url, body)
            }
            bytes != null -> {
                val body = bytes!!.toRequestBody(mediaType, 0, bytes!!.size)
                return apiService?.putBody(url, body)
            }
            any != null -> {
                return apiService?.putBody(url, any!!)
            }
            else -> return apiService?.put(url, httpParams.urlParamsMap)
        }
    }
}
