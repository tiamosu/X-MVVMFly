package com.tiamosu.fly.http.utils

import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.HttpParams
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import java.io.UnsupportedEncodingException
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
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

    @JvmStatic
    fun getParameterizedType(type: Type?, i: Int): Type? {
        return when (type) {
            is ParameterizedType -> { // 处理泛型类型
                type.actualTypeArguments[i]
            }
            is TypeVariable<*> -> {// 处理泛型擦拭对象
                getType(type.bounds[0], 0)
            }
            else -> { // class本身也是type，强制转型
                type
            }
        }
    }

    @JvmStatic
    fun getType(type: Type?, i: Int): Type? {
        return when (type) {
            is ParameterizedType -> { // 处理泛型类型
                getGenericType(type as? ParameterizedType, i)
            }
            is TypeVariable<*> -> {// 处理泛型擦拭对象
                getType(type.bounds[0], 0)
            }
            else -> { // class本身也是type，强制转型
                type
            }
        }
    }

    @JvmStatic
    fun getGenericType(parameterizedType: ParameterizedType?, i: Int): Type? {
        return when (val genericType = parameterizedType?.actualTypeArguments?.get(i)) {
            is ParameterizedType -> { // 处理多级泛型
                genericType.rawType
            }
            is GenericArrayType -> { // 处理数组泛型
                genericType.genericComponentType
            }
            is TypeVariable<*> -> { // 处理泛型擦拭对象
                getClass(genericType.bounds[0], 0)
            }
            else -> {
                genericType
            }
        }
    }

    @JvmStatic
    fun getClass(type: Type?, i: Int): Class<*> {
        return when (type) {
            is ParameterizedType -> { // 处理泛型类型
                getGenericClass(type, i)
            }
            is TypeVariable<*> -> {// 处理泛型擦拭对象
                getClass(type.bounds[0], 0)
            }
            else -> { // class本身也是type，强制转型
                type as Class<*>
            }
        }
    }

    @JvmStatic
    fun getGenericClass(parameterizedType: ParameterizedType, i: Int): Class<*> {
        return when (val genericClass = parameterizedType.actualTypeArguments[i]) {
            is ParameterizedType -> { // 处理多级泛型
                genericClass.rawType as Class<*>
            }
            is GenericArrayType -> { // 处理数组泛型
                genericClass.genericComponentType as Class<*>
            }
            is TypeVariable<*> -> { // 处理泛型擦拭对象
                getClass(genericClass.bounds[0], 0)
            }
            else -> {
                genericClass as Class<*>
            }
        }
    }

    /**
     * 普通类反射获取泛型方式，获取需要实际解析的类型
     */
    @JvmStatic
    fun <T> findNeedClass(cls: Class<T>): Type? { //以下代码是通过泛型解析实际参数,泛型必须传
        val genType = cls.genericSuperclass
        val params = (genType as? ParameterizedType)?.actualTypeArguments
        val type = params?.get(0)
        val finalNeedType: Type?
        finalNeedType = if (params?.size ?: 0 > 1) { //这个类似是：CacheResult<SkinTestResult> 2层
            check(type is ParameterizedType) { "没有填写泛型参数" }
            type.actualTypeArguments[0]
            //Type rawType = ((ParameterizedType) type).getRawType();
        } else { //这个类似是:SkinTestResult  1层
            type
        }
        return finalNeedType
    }

    /**
     * 普通类反射获取泛型方式，获取最顶层的类型
     */
    @JvmStatic
    fun <T> findRawType(cls: Class<T>): Type? {
        val genType = cls.genericSuperclass
        return getGenericType(genType as? ParameterizedType, 0)
    }

    /**
     * find the type by interfaces
     */
    @JvmStatic
    fun <R> findNeedType(cls: Class<R>): Type? {
        val typeList: List<Type>? = getMethodTypes(cls)
        return if (typeList == null || typeList.isEmpty()) {
            RequestBody::class.java
        } else typeList[0]
    }

    @JvmStatic
    fun <T> getMethodTypes(cls: Class<T>): List<Type>? {
        val typeOri = cls.genericSuperclass
        var needtypes: MutableList<Type>? = null
        // if Type is T
        if (typeOri is ParameterizedType) {
            needtypes = ArrayList()
            val parentypes = typeOri.actualTypeArguments
            for (childtype in parentypes) {
                needtypes.add(childtype)
                if (childtype is ParameterizedType) {
                    val childtypes = childtype.actualTypeArguments
                    Collections.addAll(needtypes, *childtypes)
                }
            }
        }
        return needtypes
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
