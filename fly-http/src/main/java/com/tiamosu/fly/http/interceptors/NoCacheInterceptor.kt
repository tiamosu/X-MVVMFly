package com.tiamosu.fly.http.interceptors

import com.tiamosu.fly.http.model.HttpHeaders
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 描述：不加载缓存
 * 不适用Okhttp自带的缓存
 *
 * @author tiamosu
 * @date 2020/3/11.
 */
class NoCacheInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request =
            request.newBuilder().header(HttpHeaders.HEAD_KEY_CACHE_CONTROL, "no-cache").build()
        var originalResponse = chain.proceed(request)
        originalResponse =
            originalResponse.newBuilder().header(HttpHeaders.HEAD_KEY_CACHE_CONTROL, "no-cache")
                .build()
        return originalResponse
    }
}