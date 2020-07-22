package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseBodyRequest
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class DeleteRequest(url: String) : BaseBodyRequest<DeleteRequest>(url) {

    override fun generateRequest(): Observable<ResponseBody>? {
        when {
            requestBody != null -> {
                return apiService?.deleteBody(getNewUrl(), requestBody!!)
            }
            json != null -> {
                val body = json!!.toRequestBody(mediaType)
                return apiService?.deleteJson(getNewUrl(), body)
            }
            content != null -> {
                val body = content!!.toRequestBody(mediaType)
                return apiService?.deleteBody(getNewUrl(), body)
            }
            bytes != null -> {
                val body = bytes!!.toRequestBody(mediaType, 0, bytes!!.size)
                return apiService?.deleteBody(getNewUrl(), body)
            }
            any != null -> {
                return apiService?.deleteBody(getNewUrl(), any!!)
            }
            else -> return apiService?.delete(url, httpParams.urlParamsMap)
        }
    }
}
