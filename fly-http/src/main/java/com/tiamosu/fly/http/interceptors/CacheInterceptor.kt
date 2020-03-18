package com.tiamosu.fly.http.interceptors

import android.text.TextUtils
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.utils.eLog
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 描述：设置缓存功能
 *
 * @author tiamosu
 * @date 2020/3/11.
 */
open class CacheInterceptor(
    protected var cachecontrolvalueOffline: String?,
    protected var cachecontrolvalueOnline: String?
) : Interceptor {

    @JvmOverloads
    constructor(cacheControlValue: String? = String.format("max-age=%d", maxStaleOnline)) : this(
        cacheControlValue,
        String.format("max-age=%d", maxStale)
    )

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val cacheControl = originalResponse.header(HttpHeaders.HEAD_KEY_CACHE_CONTROL)
        eLog(maxStaleOnline.toString() + "s load cache:" + cacheControl)
        return if (TextUtils.isEmpty(cacheControl) || cacheControl!!.contains("no-store")
            || cacheControl.contains("no-cache") || cacheControl.contains("must-revalidate")
            || cacheControl.contains("max-age") || cacheControl.contains("max-stale")
        ) {
            originalResponse.newBuilder()
                .removeHeader("Pragma")
                .removeHeader(HttpHeaders.HEAD_KEY_CACHE_CONTROL)
                .header(
                    HttpHeaders.HEAD_KEY_CACHE_CONTROL,
                    "public, max-age=$maxStale"
                )
                .build()
        } else {
            originalResponse
        }
    }

    companion object {
        //set cahe times is 3 days
        protected const val maxStale = 60 * 60 * 24 * 3

        // read from cache for 60 s
        protected const val maxStaleOnline = 60
    }
}