package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.body.ProgressResponseCallBack
import com.tiamosu.fly.http.body.UploadProgressRequestBody
import com.tiamosu.fly.http.request.base.BaseBodyRequest
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class PostRequest<T>(url: String) : BaseBodyRequest<T, PostRequest<T>>(url) {
    private var callBack: ProgressResponseCallBack? = null//上传回调监听

    fun updateFileCallback(callback: ProgressResponseCallBack?): PostRequest<T> {
        this.callBack = callback
        return this
    }

    override fun generateRequest(): Observable<Response>? {
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
            else -> return if (httpParams.fileParamsMap.isEmpty()) {
                apiService?.post(url, httpParams.urlParamsMap)
            } else {
                apiService?.uploadFiles(url, generateMultipartRequestBody())
            }
        }
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
                val fileBody: RequestBody = RequestBody.create(it.contentType, it.file)
                val body = UploadProgressRequestBody(fileBody, callBack)
                builder.addFormDataPart(key, it.fileName, body)
            }
        }
        return builder.build()
    }
}
