package com.tiamosu.fly.http.model

import com.tiamosu.fly.http.body.ProgressResponseCallBack
import com.tiamosu.fly.http.utils.FlyHttpUtils
import okhttp3.MediaType
import java.io.File
import java.io.InputStream
import java.io.Serializable
import java.net.URLConnection

/**
 * 描述：普通参数
 *
 * @author tiamosu
 * @date 2020/2/26.
 */
class HttpParams : Serializable {

    /**
     * 普通的键值对参数
     */
    val urlParamsMap: LinkedHashMap<String, String> = linkedMapOf()

    /**
     * 文件的键值对参数
     */
    val fileParamsMap: LinkedHashMap<String, MutableList<FileWrapper<*>>> = linkedMapOf()

    fun put(key: String?, value: String?) {
        if (key != null && value != null) {
            urlParamsMap[key] = value
        }
    }

    fun put(params: Map<String?, String?>?) {
        if (params?.isNotEmpty() == true) {
            urlParamsMap.putAll(FlyHttpUtils.escapeParams(params))
        }
    }

    fun put(params: HttpParams?) {
        params ?: return
        if (params.urlParamsMap.isNotEmpty()) {
            urlParamsMap.putAll(params.urlParamsMap)
        }
        if (params.fileParamsMap.isNotEmpty()) {
            fileParamsMap.putAll(params.fileParamsMap)
        }
    }

    fun <T : File> put(
        key: String?,
        file: T,
        responseCallBack: ProgressResponseCallBack?
    ) {
        put(key, file, file.name, responseCallBack)
    }

    fun <T : File> put(
        key: String?,
        file: T,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack)
    }

    fun <T : InputStream> put(
        key: String?,
        file: T,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ) {
        put(key, file, fileName, guessMimeType(fileName), responseCallBack)
    }

    fun put(
        key: String?,
        bytes: ByteArray,
        fileName: String,
        responseCallBack: ProgressResponseCallBack?
    ) {
        put(key, bytes, fileName, guessMimeType(fileName), responseCallBack)
    }

    fun put(key: String?, fileWrapper: FileWrapper<*>?) {
        if (fileWrapper != null) {
            put(
                key,
                fileWrapper.file,
                fileWrapper.fileName,
                fileWrapper.contentType,
                fileWrapper.responseCallBack
            )
        }
    }

    fun <T> put(
        key: String?,
        countent: T,
        fileName: String,
        contentType: MediaType?,
        responseCallBack: ProgressResponseCallBack?
    ) {
        if (key != null) {
            var fileWrappers: MutableList<FileWrapper<*>>? = fileParamsMap[key]
            if (fileWrappers == null) {
                fileWrappers = mutableListOf()
                fileParamsMap[key] = fileWrappers
            }
            fileWrappers.add(FileWrapper(countent, fileName, contentType, responseCallBack))
        }
    }

    fun <T : File> putFileParams(
        key: String?,
        files: List<T?>?,
        responseCallBack: ProgressResponseCallBack?
    ) {
        if (files?.isNotEmpty() == true) {
            for (file in files) {
                file ?: continue
                put(key, file, responseCallBack)
            }
        }
    }

    fun putFileWrapperParams(key: String?, fileWrappers: List<FileWrapper<*>?>?) {
        if (fileWrappers?.isNotEmpty() == true) {
            for (fileWrapper in fileWrappers) {
                put(key, fileWrapper)
            }
        }
    }

    fun removeUrl(key: String) {
        urlParamsMap.remove(key)
    }

    fun removeFile(key: String) {
        fileParamsMap.remove(key)
    }

    fun remove(key: String) {
        removeUrl(key)
        removeFile(key)
    }

    fun clear() {
        urlParamsMap.clear()
        fileParamsMap.clear()
    }

    private fun guessMimeType(path: String): MediaType? {
        var newPath = path
        val fileNameMap = URLConnection.getFileNameMap()
        newPath = newPath.replace("#", "") //解决文件名中含有#号异常的问题
        var contentType = fileNameMap.getContentTypeFor(newPath)
        if (contentType == null) {
            contentType = "application/octet-stream"
        }
        return MediaType.parse(contentType)
    }

    /**
     * 文件类型的包装类
     */
    class FileWrapper<T>(
        var file: T,
        var fileName: String,
        var contentType: MediaType?,
        var responseCallBack: ProgressResponseCallBack?
    ) {
        var fileSize = 0L

        init {
            if (file is File) {
                fileSize = (file as File).length()
            } else if (file is ByteArray) {
                fileSize = (file as ByteArray).size.toLong()
            }
        }

        override fun toString(): String {
            return "FileWrapper{countent=$file, fileName='$fileName, contentType=$contentType, fileSize=$fileSize}"
        }
    }

    override fun toString(): String {
        val result = StringBuilder()
        for ((key, value) in urlParamsMap) {
            if (result.isNotEmpty()) result.append("&")
            result.append(key).append("=").append(value)
        }
        for ((key, value) in fileParamsMap) {
            if (result.isNotEmpty()) result.append("&")
            result.append(key).append("=").append(value)
        }
        return result.toString()
    }

    companion object {
        val MEDIA_TYPE_PLAIN = MediaType.parse("text/plain; charset=utf-8")
        val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
        val MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream")
    }
}