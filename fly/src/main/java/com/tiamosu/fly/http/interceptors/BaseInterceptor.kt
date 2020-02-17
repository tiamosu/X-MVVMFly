package com.tiamosu.fly.http.interceptors

import okhttp3.FormBody
import okhttp3.Interceptor
import java.util.*

/**
 * @author xia
 * @date 2018/7/29.
 */
@Suppress("unused")
abstract class BaseInterceptor : Interceptor {

    protected fun getUrlParameters(chain: Interceptor.Chain): LinkedHashMap<String, String> {
        val url = chain.request().url()
        val size = url.querySize()
        val params = LinkedHashMap<String, String>()
        for (i in 0 until size) {
            params[url.queryParameterName(i)] = url.queryParameterValue(i)
        }
        return params
    }

    protected fun getUrlParameters(chain: Interceptor.Chain, key: String): String? {
        val request = chain.request()
        return request.url().queryParameter(key)
    }

    protected fun getBodyParameters(chain: Interceptor.Chain): LinkedHashMap<String, String> {
        val formBody = chain.request().body() as FormBody?
        val params = LinkedHashMap<String, String>()
        formBody?.apply {
            val size = size()
            for (i in 0 until size) {
                params[name(i)] = value(i)
            }
        }
        return params
    }

    protected fun getBodyParameters(chain: Interceptor.Chain, key: String): String? {
        return getBodyParameters(chain)[key]
    }
}
