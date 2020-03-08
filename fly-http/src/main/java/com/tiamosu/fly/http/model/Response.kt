package com.tiamosu.fly.http.model

import okhttp3.Headers

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class Response<T> {
    var body: T? = null
    var exception: Throwable? = null
    var isFromCache = false
    var rawResponse: okhttp3.Response? = null

    fun code(): Int {
        return rawResponse?.code() ?: -1
    }

    fun message(): String? {
        return rawResponse?.message()
    }

    fun headers(): Headers? {
        return rawResponse?.headers()
    }

    val isSuccessful: Boolean
        get() = exception == null

    companion object {

        fun <T> success(
            isFromCache: Boolean,
            body: T?,
            rawResponse: okhttp3.Response?
        ): Response<T> {
            val response = Response<T>()
            response.isFromCache = isFromCache
            response.body = body
            response.rawResponse = rawResponse
            return response
        }

        fun <T> error(
            isFromCache: Boolean,
            rawResponse: okhttp3.Response?,
            throwable: Throwable?
        ): Response<T> {
            val response = Response<T>()
            response.isFromCache = isFromCache
            response.rawResponse = rawResponse
            response.exception = throwable
            return response
        }
    }
}