package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseRequest
import io.reactivex.Observable
import okhttp3.Response

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class DownloadRequest<T>(url: String) : BaseRequest<T, DeleteRequest<T>>(url) {

    override fun generateRequest(): Observable<Response>? {
        return apiService?.downloadFile(url)
    }
}
