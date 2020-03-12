package com.tiamosu.fly.http.utils

import com.blankj.utilcode.util.LogUtils
import com.tiamosu.fly.http.model.HttpParams
import okhttp3.MediaType
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.net.URLConnection
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

    @JvmStatic
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

    @JvmStatic
    fun getParameterizedType(type: Type?, i: Int): Type? {
        return when (type) {
            is ParameterizedType -> { //处理泛型类型
                type.actualTypeArguments[i]
            }
            is TypeVariable<*> -> { //处理泛型擦拭对象
                getType(type.bounds[0], 0)
            }
            else -> { //class本身也是type，强制转型
                type
            }
        }
    }

    @JvmStatic
    fun getType(type: Type?, i: Int): Type? {
        return when (type) {
            is ParameterizedType -> { //处理泛型类型
                getGenericType(type, i)
            }
            is TypeVariable<*> -> {//处理泛型擦拭对象
                getType(type.bounds[0], 0)
            }
            else -> { //class本身也是type，强制转型
                type
            }
        }
    }

    @JvmStatic
    fun getGenericType(parameterizedType: ParameterizedType?, i: Int): Type? {
        return when (val genericType = parameterizedType?.actualTypeArguments?.get(i)) {
            is ParameterizedType -> { //处理多级泛型
                genericType.rawType
            }
            is GenericArrayType -> { //处理数组泛型
                genericType.genericComponentType
            }
            is TypeVariable<*> -> { //处理泛型擦拭对象
                getClass(genericType.bounds[0], 0)
            }
            else -> {
                genericType
            }
        }
    }

    @JvmStatic
    fun getClass(type: Type?, i: Int): Class<*>? {
        return when (type) {
            is ParameterizedType -> { //处理泛型类型
                getGenericClass(type, i)
            }
            is TypeVariable<*> -> { //处理泛型擦拭对象
                getClass(type.bounds[0], 0)
            }
            else -> { //class本身也是type，强制转型
                type as? Class<*>
            }
        }
    }

    @JvmStatic
    fun getGenericClass(parameterizedType: ParameterizedType?, i: Int): Class<*>? {
        return when (val genericClass = parameterizedType?.actualTypeArguments?.get(i)) {
            is ParameterizedType -> { //处理多级泛型
                genericClass.rawType as? Class<*>
            }
            is GenericArrayType -> { //处理数组泛型
                genericClass.genericComponentType as? Class<*>
            }
            is TypeVariable<*> -> { //处理泛型擦拭对象
                getClass(genericClass.bounds[0], 0)
            }
            else -> {
                genericClass as? Class<*>
            }
        }
    }
}
