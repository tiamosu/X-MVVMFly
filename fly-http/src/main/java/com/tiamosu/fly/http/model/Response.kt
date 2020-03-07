package com.tiamosu.fly.http.model

import okhttp3.Call
import okhttp3.Headers

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class Response<T> {
    private var body: T? = null
    var exception: Throwable? = null
    var isFromCache = false
    var rawCall: Call? = null
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

    fun setBody(body: T?) {
        this.body = body
    }

    fun body(): T? {
        return body
    }

    companion object {

        fun <T> success(
            isFromCache: Boolean,
            body: T,
            rawCall: Call?,
            rawResponse: okhttp3.Response?
        ): Response<T> {
            val response = Response<T>()
            response.isFromCache = isFromCache
            response.setBody(body)
            response.rawCall = rawCall
            response.rawResponse = rawResponse
            return response
        }

        fun <T> error(
            isFromCache: Boolean,
            rawCall: Call?,
            rawResponse: okhttp3.Response?,
            throwable: Throwable?
        ): Response<T> {
            val response = Response<T>()
            response.isFromCache = isFromCache
            response.rawCall = rawCall
            response.rawResponse = rawResponse
            response.exception = throwable
            return response
        }
    }
}