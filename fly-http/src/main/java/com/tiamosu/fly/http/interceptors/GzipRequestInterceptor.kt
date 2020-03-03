package com.tiamosu.fly.http.interceptors

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import okio.GzipSink
import okio.Okio
import java.io.IOException

/**
 * 描述：post数据进行gzip后发送给服务器
 * okhttp内部默认启用了gzip，此选项是针对需要对post数据进行gzip后发送给服务器的，如服务器不支持，请勿开启
 *
 * @author tiamosu
 * @date 2020/3/3.
 */
class GzipRequestInterceptor : BaseInterceptor() {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
            return chain.proceed(originalRequest)
        }
        val compressedRequest = originalRequest.newBuilder()
            .header("Accept-Encoding", "gzip")
            .method(originalRequest.method(), gzip(originalRequest.body()))
            .build()
        return chain.proceed(compressedRequest)
    }

    private fun gzip(body: RequestBody?): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return body?.contentType()
            }

            override fun contentLength(): Long {
                return -1
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                val gzipSink = Okio.buffer(GzipSink(sink))
                body?.writeTo(gzipSink)
                gzipSink.close()
            }
        }
    }
}