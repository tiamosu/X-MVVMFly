package com.tiamosu.fly.http.request.base

import com.tiamosu.fly.http.model.HttpParams
import com.tiamosu.fly.http.model.HttpParams.FileWrapper
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * 描述：body请求的基类
 *
 * @author tiamosu
 * @date 2020/3/3.
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseBodyRequest<R : BaseBodyRequest<R>>(url: String) : BaseRequest<R>(url),
    HasBody<R> {

    protected var mediaType: MediaType? = null      //上传的MIME类型
    protected var content: String? = null           //上传的文本内容
    protected var json: String? = null              //上传的Json
    protected var bytes: ByteArray? = null          //上传的字节数据
    protected var any: Any? = null                  //上传的对象
    protected var requestBody: RequestBody? = null  //上传的自定义请求体

    override fun upRequestBody(requestBody: RequestBody?): R {
        this.requestBody = requestBody
        return this as R
    }

    override fun params(key: String?, file: File?): R {
        httpParams.put(key, file)
        return this as R
    }

    override fun params(key: String?, file: File?, fileName: String?): R {
        httpParams.put(key, file, fileName)
        return this as R
    }

    override fun params(key: String?, file: File?, fileName: String?, contentType: MediaType?): R {
        httpParams.put(key, file, fileName, contentType)
        return this as R
    }

    override fun addFileParams(key: String?, files: List<File>?): R {
        httpParams.putFileParams(key, files)
        return this as R
    }

    override fun addFileWrapperParams(key: String?, fileWrappers: List<FileWrapper>?): R {
        httpParams.putFileWrapperParams(key, fileWrappers)
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    override fun upString(content: String?): R {
        this.content = content
        this.mediaType = HttpParams.MEDIA_TYPE_PLAIN
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     * 该方法用于定制请求content-type
     */
    override fun upString(content: String?, mediaType: MediaType?): R {
        this.content = content
        this.mediaType = mediaType
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    override fun upJson(json: String?): R {
        this.json = json
        this.mediaType = HttpParams.MEDIA_TYPE_JSON
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    override fun upJson(jsonObject: JSONObject?): R {
        this.json = jsonObject.toString()
        this.mediaType = HttpParams.MEDIA_TYPE_JSON
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    override fun upJson(jsonArray: JSONArray?): R {
        this.json = jsonArray.toString()
        this.mediaType = HttpParams.MEDIA_TYPE_JSON
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    override fun upBytes(bs: ByteArray?): R {
        this.bytes = bs
        this.mediaType = HttpParams.MEDIA_TYPE_STREAM
        return this as R
    }

    /**
     * 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除
     */
    override fun upBytes(bs: ByteArray?, mediaType: MediaType?): R {
        this.bytes = bs
        this.mediaType = mediaType
        return this as R
    }

    override fun upObject(any: Any?): R {
        this.any = any
        return this as R
    }

    override fun generateRequest(): Observable<ResponseBody>? {
        when {
            requestBody != null -> {
                return apiManager?.postBody(url, requestBody!!)
            }
            json != null -> {
                val body = RequestBody.create(mediaType, json!!)
                return apiManager?.postJson(url, body)
            }
            content != null -> {
                val body = RequestBody.create(mediaType, content!!)
                return apiManager?.postBody(url, body)
            }
            bytes != null -> {
                val body = RequestBody.create(mediaType, bytes!!)
                return apiManager?.postBody(url, body)
            }
            any != null -> {
                return apiManager?.postBody(url, any!!)
            }
            else -> return if (httpParams.fileParamsMap.isEmpty()) {
                apiManager?.post(url, httpParams.urlParamsMap)
            } else {
                apiManager?.uploadFiles(url, generateMultipartRequestBody())
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
                builder.addFormDataPart(key, it.fileName, fileBody)
            }
        }
        return builder.build()
    }
}