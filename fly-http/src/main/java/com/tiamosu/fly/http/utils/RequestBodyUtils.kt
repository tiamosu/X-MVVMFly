package com.tiamosu.fly.http.utils

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.internal.Util
import okio.BufferedSink
import okio.Okio
import okio.Source
import java.io.IOException
import java.io.InputStream

/**
 * 描述：请求体处理工具类
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
object RequestBodyUtils {

    @JvmStatic
    fun create(mediaType: MediaType?, inputStream: InputStream): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return mediaType
            }

            override fun contentLength(): Long {
                return try {
                    inputStream.available().toLong()
                } catch (e: IOException) {
                    0
                }
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                var source: Source? = null
                try {
                    source = Okio.source(inputStream)
                    source?.let(sink::writeAll)
                } finally {
                    Util.closeQuietly(source)
                }
            }
        }
    }
}