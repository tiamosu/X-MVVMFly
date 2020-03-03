package com.tiamosu.fly.http.request

import com.tiamosu.fly.http.body.ProgressResponseCallBack
import com.tiamosu.fly.http.body.UploadProgressRequestBody
import com.tiamosu.fly.http.model.HttpParams.FileWrapper
import com.tiamosu.fly.http.utils.RequestBodyUtils.create
import com.tiamosu.fly.utils.Preconditions.checkNotNull
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import java.io.File
import java.io.InputStream

/**
 * 描述：body请求的基类
 *
 * @author tiamosu
 * @date 2020/3/3.
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseBodyRequest<R : BaseBodyRequest<R>>(url: String) : BaseRequest<R>(url) {
    //上传的文本内容
    protected var string: String? = null
    //上传的文本内容
    protected var mediaType: MediaType? = null
    //上传的Json
    protected var json: String? = null
    //上传的字节数据
    protected var bytes: ByteArray? = null
    //上传的对象
    protected var any: Any? = null
    //自定义的请求体
    protected var requestBody: RequestBody? = null
    //上传方式
    private var currentUploadType = UploadType.PART

    enum class UploadType {
        PART, //MultipartBody.Part方式上传
        BODY  //Map RequestBody方式上传
    }

    /**
     * 上传文件的方式，默认part方式上传
     */
    fun <T> uploadType(uploadtype: UploadType): R {
        currentUploadType = uploadtype
        return this as R
    }

    fun requestBody(requestBody: RequestBody?): R {
        this.requestBody = requestBody
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    fun upString(string: String?): R {
        this.string = string
        mediaType = MediaType.parse("text/plain")
        return this as R
    }

    fun upString(string: String?, mediaType: String): R {
        this.string = string
        this.mediaType = MediaType.parse(mediaType)
        return this as R
    }

    fun upObject(@Body any: Any?): R {
        this.any = any
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    fun upJson(json: String?): R {
        this.json = json
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    fun upBytes(bytes: ByteArray?): R {
        this.bytes = bytes
        return this as R
    }

    fun params(
        key: String?,
        file: File,
        responseCallBack: ProgressResponseCallBack?
    ): R {
        httpParams.put(key, file, responseCallBack)
        return this as R
    }

    fun params(
        key: String?,
        stream: InputStream,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ): R {
        httpParams.put(key, stream, fileName, responseCallBack)
        return this as R
    }

    fun params(
        key: String?,
        bytes: ByteArray,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ): R {
        httpParams.put(key, bytes, fileName, responseCallBack)
        return this as R
    }

    fun addFileParams(
        key: String?,
        files: List<File?>?,
        responseCallBack: ProgressResponseCallBack?
    ): R {
        httpParams.putFileParams(key, files, responseCallBack)
        return this as R
    }

    fun addFileWrapperParams(
        key: String?,
        fileWrappers: List<FileWrapper<*>?>?
    ): R {
        httpParams.putFileWrapperParams(key, fileWrappers)
        return this as R
    }

    fun params(
        key: String?,
        file: File,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ): R {
        httpParams.put(key, file, fileName, responseCallBack)
        return this as R
    }

    fun <T> params(
        key: String?,
        file: T,
        fileName: String,
        contentType: MediaType?,
        responseCallBack: ProgressResponseCallBack?
    ): R {
        httpParams.put(key, file, fileName, contentType, responseCallBack)
        return this as R
    }

    override fun generateRequest(): Observable<ResponseBody>? {
        when {
            requestBody != null -> { //自定义的请求体
                return apiManager?.postBody(url, requestBody!!)
            }
            json != null -> { //上传的Json
                val body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json!!
                )
                return apiManager?.postJson(url, body)
            }
            any != null -> { //自定义的请求object
                return apiManager?.postBody(url, any!!)
            }
            string != null -> { //上传的文本内容
                val body = RequestBody.create(mediaType, string!!)
                return apiManager?.postBody(url, body)
            }
            bytes != null -> { //上传的字节数据
                val body =
                    RequestBody.create(MediaType.parse("application/octet-stream"), bytes!!)
                return apiManager?.postBody(url, body)
            }
            else -> return if (httpParams.fileParamsMap.isEmpty()) {
                apiManager?.post(url, httpParams.urlParamsMap)
            } else {
                if (currentUploadType == UploadType.PART) { //part方式上传
                    uploadFilesWithParts()
                } else { //body方式上传
                    uploadFilesWithBodys()
                }
            }
        }
    }

    private fun uploadFilesWithParts(): Observable<ResponseBody>? {
        val parts: MutableList<MultipartBody.Part> = mutableListOf()
        //拼接参数键值对
        for ((key, value) in httpParams.urlParamsMap) {
            parts.add(MultipartBody.Part.createFormData(key, value))
        }
        //拼接文件
        for ((key, fileValues) in httpParams.fileParamsMap) {
            for (fileWrapper in fileValues) {
                val part = addFile(key, fileWrapper)
                parts.add(part)
            }
        }
        return apiManager?.uploadFiles(url, parts)
    }

    private fun uploadFilesWithBodys(): Observable<ResponseBody>? {
        val bodyMap: MutableMap<String, RequestBody> = mutableMapOf()
        //拼接参数键值对
        for ((key, value) in httpParams.urlParamsMap) {
            val body =
                RequestBody.create(MediaType.parse("text/plain"), value)
            bodyMap[key] = body
        }
        //拼接文件
        for ((key, fileValues) in httpParams.fileParamsMap) {
            for (fileWrapper in fileValues) {
                val requestBody = getRequestBody(fileWrapper)
                val uploadProgressRequestBody =
                    UploadProgressRequestBody(requestBody, fileWrapper.responseCallBack)
                bodyMap[key] = uploadProgressRequestBody
            }
        }
        return apiManager?.uploadFiles(url, bodyMap)
    }

    //文件方式
    private fun addFile(
        key: String,
        fileWrapper: FileWrapper<*>
    ): MultipartBody.Part {
        val requestBody = getRequestBody(fileWrapper)
        checkNotNull(
            requestBody,
            "requestBody == null fileWrapper.file must is File/InputStream/byte[]"
        )
        //包装RequestBody，在其内部实现上传进度监听
        return if (fileWrapper.responseCallBack != null) {
            val uploadProgressRequestBody =
                UploadProgressRequestBody(requestBody, fileWrapper.responseCallBack)
            MultipartBody.Part.createFormData(
                key,
                fileWrapper.fileName,
                uploadProgressRequestBody
            )
        } else {
            MultipartBody.Part.createFormData(key, fileWrapper.fileName, requestBody!!)
        }
    }

    private fun getRequestBody(fileWrapper: FileWrapper<*>): RequestBody? {
        return when (fileWrapper.file) {
            is File -> {
                RequestBody.create(fileWrapper.contentType, fileWrapper.file as File)
            }
            is InputStream -> {
                create(fileWrapper.contentType, (fileWrapper.file as InputStream))
            }
            is ByteArray -> {
                RequestBody.create(fileWrapper.contentType, fileWrapper.file as ByteArray)
            }
            else -> null
        }
    }
}