package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseRequest
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class DownloadRequest(url: String) : BaseRequest<DeleteRequest>(url) {

    override fun generateRequest(): Observable<ResponseBody>? {
        return apiService?.downloadFile(url)
    }
}
