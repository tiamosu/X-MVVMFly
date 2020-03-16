package com.tiamosu.fly.http.interceptors

import com.blankj.utilcode.util.LogUtils
import com.tiamosu.fly.http.utils.FlyHttpUtils.UTF8
import com.tiamosu.fly.http.utils.FlyHttpUtils.createUrlFromParams
import okhttp3.*
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

/**
 * 描述：动态拦截器
 *
 * 主要功能是针对参数：
 * 1.可以获取到全局公共参数和局部参数，统一进行签名sign
 * 2.可以自定义动态添加参数，类似时间戳timestamp是动态变化的，token（登录了才有），参数签名等
 * 3.参数值是经过UTF-8编码的
 * 4.默认提供询问是否动态签名（签名需要自定义），动态添加时间戳等
 *
 * @author tiamosu
 * @date 2020/2/29.
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseDynamicInterceptor<R : BaseDynamicInterceptor<R>> : Interceptor {
    protected var httpUrl: HttpUrl? = null
    protected var isSign = false            //是否需要签名
    protected var isTimeStamp = false       //是否需要追加时间戳
    protected var isAccessToken = false     //是否需要添加token

    fun sign(sign: Boolean): R {
        isSign = sign
        return this as R
    }

    fun timeStamp(timeStamp: Boolean): R {
        isTimeStamp = timeStamp
        return this as R
    }

    fun accessToken(accessToken: Boolean): R {
        isAccessToken = accessToken
        return this as R
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (request.method() == "GET") {
            httpUrl = HttpUrl.parse(parseUrl(request.url().url().toString()))
            request = addGetParamsSign(request)
        } else if (request.method() == "POST") {
            httpUrl = request.url()
            request = addPostParamsSign(request)
        }
        return chain.proceed(request)
    }

    /**
     * @return 添加签名和公共动态参数
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    private fun addGetParamsSign(request: Request): Request {
        var newRequest = request
        var httpUrl = newRequest.url()
        val newBuilder = httpUrl.newBuilder()
        //获取原有的参数
        val nameSet = httpUrl.queryParameterNames()
        val nameList = ArrayList<String>()
        nameList.addAll(nameSet)

        val oldparams = TreeMap<String, String>()
        for (i in nameList.indices) {
            val value =
                if (httpUrl.queryParameterValues(nameList[i]).size > 0) httpUrl.queryParameterValues(
                    nameList[i]
                )[0] else ""
            oldparams[nameList[i]] = value
        }
        val nameKeys = listOf(nameList).toString()
        //拼装新的参数
        val newParams = dynamic(oldparams)
        for ((key, value) in newParams) {
            val urlValue = URLEncoder.encode(value, UTF8.name())
            //原来的URl: https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult?appId=10101
            if (!nameKeys.contains(key)) { //避免重复添加
                newBuilder.addQueryParameter(key, urlValue)
            }
        }
        httpUrl = newBuilder.build()
        newRequest = newRequest.newBuilder().url(httpUrl).build()
        return newRequest
    }

    //post 添加签名和公共动态参数
    @Throws(UnsupportedEncodingException::class)
    private fun addPostParamsSign(request: Request): Request {
        var newRequest = request
        if (newRequest.body() is FormBody) {
            val bodyBuilder = FormBody.Builder()
            var formBody = newRequest.body() as FormBody
            //原有的参数
            val oldparams = TreeMap<String, String>()
            for (i in 0 until formBody.size()) {
                oldparams[formBody.encodedName(i)] = formBody.encodedValue(i)
            }
            //拼装新的参数
            val newParams = dynamic(oldparams)
            for ((key, value1) in newParams) {
                val value = URLDecoder.decode(value1, UTF8.name())
                bodyBuilder.addEncoded(key, value)
            }
            if (httpUrl != null) {
                val url = createUrlFromParams(httpUrl!!.url().toString(), newParams)
                LogUtils.i(url)
            }

            formBody = bodyBuilder.build()
            newRequest = newRequest.newBuilder().post(formBody).build()
        } else if (newRequest.body() is MultipartBody) {
            var multipartBody = newRequest.body() as MultipartBody
            val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            val oldparts = multipartBody.parts()
            //拼装新的参数
            val newparts: MutableList<MultipartBody.Part> = ArrayList(oldparts)
            val oldparams = TreeMap<String, String>()
            val newParams = dynamic(oldparams)
            for ((key, value) in newParams) {
                val part = MultipartBody.Part.createFormData(key, value)
                newparts.add(part)
            }
            for (part in newparts) {
                bodyBuilder.addPart(part)
            }
            multipartBody = bodyBuilder.build()
            newRequest = newRequest.newBuilder().post(multipartBody).build()
        }
        return newRequest
    }

    //解析前：https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult?appId=10101
    //解析后：https://xxx.xxx.xxx/app/chairdressing/skinAnalyzePower/skinTestResult
    private fun parseUrl(url: String): String {
        var newUrl = url
        if ("" != newUrl && newUrl.contains("?")) { // 如果URL不是空字符串
            newUrl = newUrl.substring(0, newUrl.indexOf('?'))
        }
        return newUrl
    }

    /**
     * 动态处理参数
     *
     * @param dynamicMap
     * @return 返回新的参数集合
     */
    abstract fun dynamic(dynamicMap: TreeMap<String, String>): TreeMap<String, String>
}