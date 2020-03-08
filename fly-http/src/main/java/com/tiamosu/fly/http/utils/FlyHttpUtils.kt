package com.tiamosu.fly.http.utils

import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.HttpParams
import okhttp3.MediaType
import okhttp3.Response
import java.io.UnsupportedEncodingException
import java.net.URLConnection
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*

/**
 * @author tiamosu
 * @date 2018/10/8.
 *
 * 用于处理空参数
 */
object FlyHttpUtils {

    @JvmStatic
    val UTF8: Charset by lazy { Charset.forName("UTF-8") }

    @JvmStatic
    fun <V> escapeParams(map: Map<String?, V?>?): Map<String, V> {
        if (map == null || map.isEmpty()) {
            return mapOf()
        }
        val hashMap: LinkedHashMap<String, V> = linkedMapOf()
        for ((key, value) in map) {
            if (key != null && value != null) {
                hashMap[key] = value
            }
        }
        return hashMap
    }

    @JvmStatic
    fun createUrlFromParams(url: String, params: Map<String, String>): String? {
        try {
            val builder = StringBuilder()
            builder.append(url)
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) builder.append("&") else builder.append(
                "?"
            )
            for ((key, urlValues) in params) {
                //对参数进行 utf-8 编码,防止头信息传中文
                //String urlValue = URLEncoder.encode(urlValues, UTF8.name());
                builder.append(key).append("=").append(urlValues).append("&")
            }
            builder.deleteCharAt(builder.length - 1)
            return builder.toString()
        } catch (e: Exception) {
            LogUtils.e(e.message)
        }
        return url
    }

    /**
     * 根据文件名获取MIME类型
     */
    @JvmStatic
    fun guessMimeType(fileName: String?): MediaType? {
        var newFileName = fileName ?: return HttpParams.MEDIA_TYPE_STREAM
        val fileNameMap = URLConnection.getFileNameMap()
        newFileName = newFileName.replace("#", "") //解决文件名中含有#号异常的问题
        val contentType =
            fileNameMap.getContentTypeFor(newFileName) ?: return HttpParams.MEDIA_TYPE_STREAM
        return MediaType.parse(contentType)
    }

    /**
     * 根据响应头或者url获取文件名
     */
    @JvmStatic
    fun getNetFileName(response: Response?, url: String?): String {
        var fileName: String? = getHeaderFileName(response)
        if (TextUtils.isEmpty(fileName)) fileName = getUrlFileName(url)
        if (TextUtils.isEmpty(fileName)) fileName = "unknownfile_" + System.currentTimeMillis()
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            FlyHttpLog.printStackTrace(e)
        }
        return fileName!!
    }

    /**
     * 解析文件头
     * Content-Disposition: attachment; filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private fun getHeaderFileName(response: Response?): String? {
        var dispositionHeader =
            response?.header(HttpHeaders.HEAD_KEY_CONTENT_DISPOSITION)
        if (dispositionHeader != null) {
            //文件名可能包含双引号，需要去除
            dispositionHeader = dispositionHeader.replace("\"".toRegex(), "")
            var split = "filename="
            var indexOf = dispositionHeader.indexOf(split)
            if (indexOf != -1) {
                return dispositionHeader.substring(indexOf + split.length)
            }
            split = "filename*="
            indexOf = dispositionHeader.indexOf(split)
            if (indexOf != -1) {
                var fileName =
                    dispositionHeader.substring(indexOf + split.length)
                val encode = "UTF-8''"
                if (fileName.startsWith(encode)) {
                    fileName = fileName.substring(encode.length)
                }
                return fileName
            }
        }
        return null
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    private fun getUrlFileName(url: String?): String? {
        var filename: String? = null
        val strings = url?.split("/")?.toTypedArray()
        strings?.forEach {
            if (it.contains("?")) {
                val endIndex = it.indexOf("?")
                if (endIndex != -1) {
                    filename = it.substring(0, endIndex)
                    return filename
                }
            }
        }
        if (strings?.isNotEmpty() == true) {
            filename = strings[strings.size - 1]
        }
        return filename
    }
}
