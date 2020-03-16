package com.tiamosu.fly.http.interceptors

import com.blankj.utilcode.util.NetworkUtils
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.utils.FlyHttpLog.i
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 描述：支持离线缓存,使用oKhttp自带的缓存功能
 * 配置Okhttp的Cache
 * 配置请求头中的cache-control或者统一处理所有请求的请求头
 * 云端配合设置响应头或者自己写拦截器修改响应头中cache-control
 *
 *
 * 如果你不想加入公共缓存，想单独对某个api进行缓存，可用Headers来实现
 * 请参考网址：http://www.jianshu.com/p/9c3b4ea108a7
 *
 * @author tiamosu
 * @date 2020/3/11.
 */
class CacheInterceptorOffline : CacheInterceptor {
    constructor() : super()
    constructor(cacheControlValue: String?) : super(cacheControlValue)
    constructor(cacheControlValue: String?, cacheOnlineControlValue: String?) : super(
        cacheControlValue, cacheOnlineControlValue
    )

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!NetworkUtils.isConnected()) {
            i(" no network load cache:" + request.cacheControl().toString())
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
            val response = chain.proceed(request)
            return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader(HttpHeaders.HEAD_KEY_CACHE_CONTROL)
                .header(
                    HttpHeaders.HEAD_KEY_CACHE_CONTROL,
                    "public, only-if-cached, $cachecontrolvalueOffline"
                )
                .build()
        }
        return chain.proceed(request)
    }
}