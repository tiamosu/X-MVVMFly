package com.tiamosu.fly.http.model

/**
 * @author tiamosu
 * @date 2020/3/6.
 */
class Response {
    var body: Any? = null
    var exception: Throwable? = null

    val isSuccessful: Boolean
        get() = exception == null

    companion object {

        fun success(body: Any?): Response {
            val response = Response()
            response.body = body
            return response
        }

        fun error(throwable: Throwable?): Response {
            val response = Response()
            response.exception = throwable
            return response
        }
    }
}