package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseBodyRequest
import io.reactivex.rxjava3.core.Observable
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
                return apiService?.putBody(getNewUrl(), requestBody!!)
            }
            json != null -> {
                val body = json!!.toRequestBody(mediaType)
                return apiService?.putJson(getNewUrl(), body)
            }
            content != null -> {
                val body = content!!.toRequestBody(mediaType)
                return apiService?.putBody(getNewUrl(), body)
            }
            bytes != null -> {
                val body = bytes!!.toRequestBody(mediaType, 0, bytes!!.size)
                return apiService?.putBody(getNewUrl(), body)
            }
            any != null -> {
                return apiService?.putBody(getNewUrl(), any!!)
            }
            else -> return apiService?.put(url, httpParams.urlParamsMap)
        }
    }
}
