package com.tiamosu.fly.http.model

import com.tiamosu.fly.http.utils.FlyHttpUtils
import okhttp3.MediaType
import java.io.File
import java.io.Serializable
import java.util.*

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
    val fileParamsMap: LinkedHashMap<String, MutableList<FileWrapper>> = linkedMapOf()

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

    fun put(key: String?, file: File?) {
        put(key, file, file?.name)
    }

    fun put(key: String?, file: File?, fileName: String?) {
        put(key, file, fileName, null)
    }

    fun put(key: String?, fileWrapper: FileWrapper?) {
        put(key, fileWrapper?.file, fileWrapper?.fileName, fileWrapper?.contentType)
    }

    fun put(key: String?, file: File?, fileName: String?, contentType: MediaType?) {
        if (key != null && file != null && fileName != null) {
            val mediaType = contentType ?: FlyHttpUtils.guessMimeType(fileName)
            var fileWrappers: MutableList<FileWrapper>? = fileParamsMap[key]
            if (fileWrappers == null) {
                fileWrappers = mutableListOf()
                fileParamsMap[key] = fileWrappers
            }
            fileWrappers.add(FileWrapper(file, fileName, mediaType))
        }
    }

    fun putFileParams(key: String?, files: List<File>?) {
        files?.forEach { put(key, it) }
    }

    fun putFileWrapperParams(key: String?, fileWrappers: List<FileWrapper?>?) {
        fileWrappers?.forEach { put(key, it) }
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

    /**
     * 文件类型的包装类
     */
    class FileWrapper(var file: File, var fileName: String, var contentType: MediaType?) {
        var fileSize = file.length()

        override fun toString(): String {
            return "FileWrapper(file=$file, fileName='$fileName', contentType=$contentType, fileSize=$fileSize)"
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