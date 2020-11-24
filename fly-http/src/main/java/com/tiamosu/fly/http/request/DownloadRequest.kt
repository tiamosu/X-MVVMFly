package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.callback.FileCallback
import com.tiamosu.fly.http.request.base.BaseRequest
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class DownloadRequest(url: String) : BaseRequest<DownloadRequest>(url) {

    override fun generateRequest(): Observable<ResponseBody>? {
        var range = 0L
        (callback as? FileCallback)?.apply {
            update(isBreakpointDownload, url)
            if (isBreakpointDownload) {
                range = downloadFile.length()
            }
        }
        return apiService?.downloadFile("bytes=${range}-", url)
    }
}
