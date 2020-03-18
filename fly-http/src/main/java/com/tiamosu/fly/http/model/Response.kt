package com.tiamosu.fly.http.model

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class Response<T> {
    var body: T? = null
    var exception: Throwable? = null
    var isFromCache = false

    companion object {

        fun <T> success(isFromCache: Boolean, body: T?): Response<T> {
            val response = Response<T>()
            response.isFromCache = isFromCache
            response.body = body
            return response
        }

        fun <T> error(isFromCache: Boolean, throwable: Throwable?): Response<T> {
            val response = Response<T>()
            response.isFromCache = isFromCache
            response.exception = throwable
            return response
        }
    }

    override fun toString(): String {
        return "Response(body=$body, exception=$exception, isFromCache=$isFromCache)"
    }
}