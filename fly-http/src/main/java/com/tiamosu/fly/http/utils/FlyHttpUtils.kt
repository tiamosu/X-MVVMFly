@file:JvmName("FlyHttpUtils")

package com.tiamosu.fly.http.utils

import com.tiamosu.fly.http.model.HttpParams
import okhttp3.MediaType
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URLConnection
import java.nio.charset.Charset

/**
 * @author tiamosu
 * @date 2020/3/18.
 */

val UTF8: Charset by lazy { Charset.forName("UTF-8") }

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
        e(e.message)
    }
    return url
}

/**
 * 根据文件名获取MIME类型
 */
fun guessMimeType(fileName: String?): MediaType? {
    var newFileName = fileName ?: return HttpParams.MEDIA_TYPE_STREAM
    val fileNameMap = URLConnection.getFileNameMap()
    newFileName = newFileName.replace("#", "") //解决文件名中含有#号异常的问题
    val contentType =
        fileNameMap.getContentTypeFor(newFileName) ?: return HttpParams.MEDIA_TYPE_STREAM
    return MediaType.parse(contentType)
}

@Throws(IOException::class)
fun toByteArray(input: InputStream?): ByteArray? {
    input ?: return null
    val output = ByteArrayOutputStream()
    write(input, output)
    output.close()
    return output.toByteArray()
}

@Throws(IOException::class)
private fun write(inputStream: InputStream, outputStream: OutputStream) {
    var len: Int
    val buffer = ByteArray(4096)
    while (inputStream.read(buffer).also { len = it } != -1) {
        outputStream.write(buffer, 0, len)
    }
}