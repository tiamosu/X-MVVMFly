package com.tiamosu.fly.http.interceptors

import androidx.annotation.IntDef
import com.tiamosu.fly.http.utils.FlyHttpUtils
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 * 描述：设置日志拦截器
 *
 * @author tiamosu
 * @date 2020/3/1.
 */
class HttpLoggingInterceptor : Interceptor {
    @Volatile
    private var level = Level.NONE
    private var logger: Logger
    private var isLogEnable = false
    private var colorLevel: java.util.logging.Level = java.util.logging.Level.INFO

    constructor(tag: String) : this(tag, true)

    constructor(tag: String, isLogEnable: Boolean) {
        this.isLogEnable = isLogEnable
        logger = Logger.getLogger(tag)
    }

    fun setLevel(@Level.Level level: Int): HttpLoggingInterceptor {
        this.level = level
        return this
    }

    fun setColorLevel(level: java.util.logging.Level) {
        this.colorLevel = level
    }

    private fun log(message: String?) {
        logger.log(colorLevel, message)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }
        //请求日志拦截
        logForRequest(request, chain.connection())

        //执行请求，计算请求时间
        val startNs = System.nanoTime()
        val response: Response
        response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        //响应日志拦截
        return logForResponse(response, tookMs)
    }

    @Throws(IOException::class)
    private fun logForRequest(
        request: Request,
        connection: Connection?
    ) {
        log("------------------------------- request -------------------------------")
        val logBody = level == Level.BODY
        val logHeaders = level == Level.BODY || level == Level.HEADERS
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1

        try {
            val requestStartMessage =
                "--> " + request.method() + ' ' + URLDecoder.decode(
                    request.url().url().toString(), UTF8.name()
                ) + ' ' + protocol
            log(requestStartMessage)

            if (logHeaders) {
                if (hasRequestBody) {
                    // Request body headers are only present when installed as a network interceptor. Force
                    // them to be included (when available) so there values are known.
                    if (requestBody?.contentType() != null) {
                        log("\tContent-Type: " + requestBody.contentType())
                    }
                    if (requestBody?.contentLength() != -1L) {
                        log("\tContent-Length: " + requestBody?.contentLength())
                    }
                }

                val headers = request.headers()
                var i = 0
                val count = headers.size()
                while (i < count) {
                    val name = headers.name(i)
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equals(name, ignoreCase = true)
                        && !"Content-Length".equals(name, ignoreCase = true)
                    ) {
                        log("\t" + name + ": " + headers.value(i))
                    }
                    i++
                }

                log(" ")
                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody?.contentType())) {
                        bodyToString(request)
                    } else {
                        log("\tbody: maybe [file part] , too large too print , ignored!")
                    }
                }
            }
        } catch (e: Exception) {
            e(e)
        } finally {
            log("--> END " + request.method())
        }
    }

    private fun logForResponse(response: Response, tookMs: Long): Response {
        log("------------------------------- response -------------------------------")
        val builder = response.newBuilder()
        val clone = builder.build()
        var responseBody = clone.body()
        val logBody = level == Level.BODY
        val logHeaders = level == Level.BODY || level == Level.HEADERS

        try {
            log(
                "<-- " + clone.code() + ' ' + clone.message() + ' ' + URLDecoder.decode(
                    clone.request().url().url().toString(), UTF8.name()
                ) + " (" + tookMs + "ms）"
            )
            if (logHeaders) {
                log(" ")
                val headers = clone.headers()
                var i = 0
                val count = headers.size()
                while (i < count) {
                    log("\t" + headers.name(i) + ": " + headers.value(i))
                    i++
                }
                log(" ")
                if (logBody && HttpHeaders.hasBody(clone)) {
                    var contentType: MediaType?
                    if (isPlaintext(responseBody?.contentType().also { contentType = it })) {
                        val bytes = FlyHttpUtils.toByteArray(responseBody?.byteStream())
                        val body = bytes?.let { String(it, getCharset(contentType)) }
                        log("\tbody:$body")

                        responseBody = body?.let { ResponseBody.create(contentType, it) }
                        return response.newBuilder().body(responseBody).build()
                    } else {
                        log("\tbody: maybe [file part] , too large too print , ignored!")
                    }
                }
                log(" ")
            }
        } catch (e: Exception) {
            e(e)
        } finally {
            log("<-- END HTTP")
        }
        return response
    }

    private fun bodyToString(request: Request) {
        try {
            val copy = request.newBuilder().build()
            val body = copy.body() ?: return
            val buffer = Buffer()
            body.writeTo(buffer)

            val charset: Charset = getCharset(body.contentType())
            val result = buffer.readString(charset)
            log("\tbody:" + URLDecoder.decode(replacer(result), UTF8.name()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun replacer(content: String): String {
        var data = content
        try {
            data = data.replace("%(?![0-9a-fA-F]{2})".toRegex(), "%25")
            data = data.replace("\\+".toRegex(), "%2B")
            data = URLDecoder.decode(data, "utf-8")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }

    fun e(t: Throwable) {
        if (isLogEnable) t.printStackTrace()
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        fun isPlaintext(mediaType: MediaType?): Boolean {
            if (mediaType == null) return false
            if (mediaType.type() == "text") {
                return true
            }
            var subtype = mediaType.subtype()
            subtype = subtype.toLowerCase(Locale.ROOT)
            if (subtype.contains("x-www-form-urlencoded") ||
                subtype.contains("json") ||
                subtype.contains("xml") ||
                subtype.contains("html")
            )
                return true
            return false
        }

        fun getCharset(contentType: MediaType?): Charset {
            return contentType?.charset(UTF8) ?: UTF8
        }
    }

    object Level {
        const val NONE = 1      //不打印log
        const val BASIC = 2     //只打印 请求首行 和 响应首行
        const val HEADERS = 3   //打印请求和响应的所有 Header
        const val BODY = 4      //所有数据全部打印

        @IntDef(NONE, BASIC, HEADERS, BODY)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Level
    }
}