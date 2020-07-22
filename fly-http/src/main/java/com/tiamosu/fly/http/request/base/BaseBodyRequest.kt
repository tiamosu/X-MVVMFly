package com.tiamosu.fly.http.request.base

import com.tiamosu.fly.http.model.HttpParams
import com.tiamosu.fly.http.model.HttpParams.FileWrapper
import com.tiamosu.fly.http.utils.createUrlFromParams
import okhttp3.MediaType
import okhttp3.RequestBody
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
    private var isAddParamsToUrl = true             //是否把 Params 拼接到 Url

    /**
     * 用于调用 upXxx() 相关函数，并且有传入 urlParams 时，把 urlParams 拼接到 url 上
     */
    protected fun getNewUrl(): String {
        if (isAddParamsToUrl && httpParams.urlParamsMap.isNotEmpty()) {
            return createUrlFromParams(url, httpParams.urlParamsMap)
        }
        return url
    }

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

    override fun addParamsToUrl(isAddParamsToUrl: Boolean): R {
        this.isAddParamsToUrl = isAddParamsToUrl
        return this as R
    }
}
