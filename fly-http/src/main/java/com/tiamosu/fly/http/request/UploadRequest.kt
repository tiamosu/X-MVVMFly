package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.request.base.BaseBodyRequest
import com.tiamosu.fly.http.request.base.ProgressRequestBody
import com.tiamosu.fly.http.request.base.ProgressRequestBody.ProgressResponseCallBack
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody

/**
 * @author tiamosu
 * @date 2020/3/8.
 */
class UploadRequest(url: String) : BaseBodyRequest<UploadRequest>(url) {
    private var progressCallBack: ProgressResponseCallBack? = null//上传回调监听

    fun updateFileCallback(progressCallBack: ProgressResponseCallBack?): UploadRequest {
        this.progressCallBack = progressCallBack
        return this
    }

    override fun generateRequest(): Observable<ResponseBody>? {
        return apiService?.uploadFiles(url, generateMultipartRequestBody())
    }

    private fun generateMultipartRequestBody(): RequestBody {
        //表单提交，有文件
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        //拼接键值对
        for ((key, value) in httpParams.urlParamsMap) {
            builder.addFormDataPart(key, value)
        }
        //拼接文件
        for ((key, files) in httpParams.fileParamsMap) {
            files.forEach {
                val fileBody: RequestBody = it.file.asRequestBody(it.contentType)
                val body = ProgressRequestBody(fileBody, callback)
                body.setProgressCallback(progressCallBack)
                builder.addFormDataPart(key, it.fileName, body)
            }
        }
        return builder.build()
    }
}
