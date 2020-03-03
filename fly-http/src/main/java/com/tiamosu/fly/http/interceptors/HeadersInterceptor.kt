package com.tiamosu.fly.http.interceptors

import com.tiamosu.fly.http.model.HttpHeaders
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 描述：配置公共头部
 *
 * @author tiamosu
 * @date 2020/2/26.
 */
class HeadersInterceptor(private val headers: HttpHeaders) : BaseInterceptor() {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request()
            .newBuilder()
            .also { builder ->
                headers.headersMap.forEach {
                    builder.addHeader(it.key, it.value)
                }
            }
            .build()
            .let(chain::proceed)
    }
}