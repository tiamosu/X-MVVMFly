package com.tiamosu.fly.http.request.base

import android.text.TextUtils
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.api.ApiService
import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.converter.IDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.cache.model.CacheMode.*
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.https.HttpsUtils
import com.tiamosu.fly.http.interceptors.*
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.HttpParams
import com.tiamosu.fly.http.request.RequestCall
import com.tiamosu.fly.integration.obtainRetrofitService
import com.tiamosu.fly.utils.checkNotNull
import com.tiamosu.fly.utils.getAppComponent
import io.reactivex.rxjava3.core.Observable
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.io.File
import java.io.InputStream
import java.net.Proxy
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * @author tiamosu
 * @date 2020/2/26.
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class BaseRequest<R : BaseRequest<R>>(val url: String) {
    internal var readTimeOut = 0L                                //读超时，单位 ms
    internal var writeTimeOut = 0L                               //写超时，单位 ms
    internal var connectTimeout = 0L                             //链接超时，单位 ms
    internal var proxy: Proxy? = null                            //代理
    internal var hostnameVerifier: HostnameVerifier? = null      //使用 verify 函数效验服务器主机名的合法性
    internal var sslParams: HttpsUtils.SSLParams? =
        null         //获取 SSLSocketFactory 和 X509TrustManager
    internal var sign = false                                    //是否需要签名
    internal var timeStamp = false                               //是否需要追加时间戳
    internal var accessToken = false                             //是否需要追加 token
    internal var cookies: MutableList<Cookie> = mutableListOf()  //用户手动添加的 Cookie
    internal val interceptors: MutableList<Interceptor> = mutableListOf()        //拦截器
    internal val networkInterceptors: MutableList<Interceptor> = mutableListOf() //网络拦截器

    internal var baseUrl: String? = null
    internal var httpUrl: HttpUrl? = null
    internal var retryCount = 0                                  //超时重试次数，默认0次
    internal var retryDelay = 0                                  //超时重试延时，单位 s
    internal var isSyncRequest = false                           //是否是同步请求，默认为异步请求
    internal var isGlobalErrorHandle = false                     //是否进行全局错误统一处理
    internal val converterFactories:
            MutableList<retrofit2.Converter.Factory> = mutableListOf()    //转换器
    internal val adapterFactories:
            MutableList<CallAdapter.Factory> = mutableListOf()            //适配器

    internal var cache: Cache? = null
    internal var cacheMode: CacheMode = NO_CACHE             //默认无缓存
    internal var cacheTime = -1L                             //缓存时间
    internal var cacheKey: String? = null                    //缓存Key
    internal var diskConverter: IDiskConverter? = null       //设置RxCache磁盘转换器

    internal val httpHeaders by lazy { HttpHeaders() }       //添加的 header
    internal val httpParams by lazy { HttpParams() }         //添加的 param

    private val okHttpNewBuilder by lazy { FlyHttp.getOkHttpClient().newBuilder() }

    internal val okHttpClient: OkHttpClient by lazy {
        generateOkClient().apply {
            if (cacheMode === DEFAULT && cache != null) {
                this.cache(cache)
            }
        }.build()
    }

    internal val retrofit: Retrofit by lazy {
        generateRetrofit().apply {
            client(okHttpClient)
        }.build()
    }

    internal val apiService: ApiService? by lazy {
        getAppComponent().repositoryManager()
            .obtainRetrofitService<ApiService>(retrofit)
    }

    internal val rxCache: RxCache by lazy {
        generateRxCache().build()
    }

    internal var callback: Callback<*>? = null

    internal var isBreakpointDownload = false    //是否进行断点下载

    init {
        baseUrl = FlyHttp.getBaseUrl()
        if (!TextUtils.isEmpty(baseUrl)) {
            httpUrl = baseUrl?.toHttpUrlOrNull()
        }
        if (baseUrl == null && (url.startsWith("http://") || url.startsWith("https://"))) {
            httpUrl = url.toHttpUrlOrNull()
            baseUrl = httpUrl?.toUrl()?.protocol + "://" + httpUrl?.toUrl()?.host + "/"
        }
        retryCount = FlyHttp.getRetryCount()
        retryDelay = FlyHttp.getRetryDelay()
        cacheMode = FlyHttp.getCacheMode()
        cacheTime = FlyHttp.getCacheTime()
        cache = FlyHttp.getHttpCache()
        isGlobalErrorHandle = FlyHttp.isGlobalErrorHandle()

        //默认添加 Accept-Language
        val acceptLanguage = HttpHeaders.acceptLanguage
        if (!TextUtils.isEmpty(acceptLanguage)) headers(
            HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage
        )
        //默认添加 User-Agent
        val userAgent = HttpHeaders.userAgent
        if (!TextUtils.isEmpty(userAgent)) headers(
            HttpHeaders.HEAD_KEY_USER_AGENT, userAgent
        )
        //添加公共请求参数
        FlyHttp.getCommonParams()?.let(httpParams::put)
        FlyHttp.getCommonHeaders()?.let(httpHeaders::put)
    }

    /**
     * 读超时，单位 ms
     */
    fun readTimeOut(readTimeOut: Long): R {
        this.readTimeOut = readTimeOut
        return this as R
    }

    /**
     * 写超时，单位 ms
     */
    fun writeTimeOut(writeTimeOut: Long): R {
        this.writeTimeOut = writeTimeOut
        return this as R
    }

    /**
     * 链接超时，单位 ms
     */
    fun connectTimeout(connectTimeout: Long): R {
        this.connectTimeout = connectTimeout
        return this as R
    }

    /**
     * 超时设置，单位 ms
     */
    fun timeOut(timeOut: Long): R {
        this.readTimeOut = timeOut
        this.writeTimeOut = timeOut
        this.connectTimeout = timeOut
        return this as R
    }

    /**
     * 设置代理
     */
    fun okHttpProxy(proxy: Proxy): R {
        this.proxy = proxy
        return this as R
    }

    /**
     * https的全局访问规则
     */
    fun hostnameVerifier(hostnameVerifier: HostnameVerifier): R {
        this.hostnameVerifier = hostnameVerifier
        return this as R
    }

    /**
     * https的全局自签名证书
     */
    fun certificates(certificates: Array<InputStream>): R {
        this.sslParams = HttpsUtils.getSslSocketFactory(certificates)
        return this as R
    }

    /**
     * https双向认证证书
     */
    fun certificates(
        bksFile: InputStream,
        password: String,
        certificates: Array<InputStream>
    ): R {
        this.sslParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates)
        return this as R
    }

    /**
     * 添加拦截器
     */
    fun addInterceptor(interceptor: Interceptor): R {
        interceptor.let(interceptors::add)
        return this as R
    }

    /**
     * 添加网络拦截器
     */
    fun addNetworkInterceptor(networkInterceptor: Interceptor): R {
        networkInterceptor.let(networkInterceptors::add)
        return this as R
    }

    /**
     * 是否需要签名
     */
    fun sign(sign: Boolean): R {
        this.sign = sign
        return this as R
    }

    /**
     * 是否需要追加时间戳
     */
    fun timeStamp(timeStamp: Boolean): R {
        this.timeStamp = timeStamp
        return this as R
    }

    /**
     * 是否需要追加 token
     */
    fun accessToken(accessToken: Boolean): R {
        this.accessToken = accessToken
        return this as R
    }

    /**
     * 用户手动添加的 Cookie
     */
    fun addCookie(name: String, value: String): R {
        val builder = Cookie.Builder()
        if (httpUrl != null) {
            val cookie = builder.name(name).value(value).domain(httpUrl!!.host).build()
            cookies.add(cookie)
        }
        return this as R
    }

    /**
     * 用户手动添加的 Cookie
     */
    fun addCookie(cookie: Cookie): R {
        cookies.add(cookie)
        return this as R
    }

    /**
     * 用户手动添加的 Cookie
     */
    fun addCookies(cookies: List<Cookie>): R {
        this.cookies.addAll(cookies)
        return this as R
    }

    /**
     * 域名设置
     */
    fun baseUrl(baseUrl: String): R {
        this.baseUrl = baseUrl
        if (!TextUtils.isEmpty(baseUrl)) httpUrl = baseUrl.toHttpUrlOrNull()
        return this as R
    }

    /**
     * 超时重试次数，默认0次
     */
    fun retryCount(retryCount: Int): R {
        this.retryCount = retryCount
        return this as R
    }

    /**
     * 超时重试延时，单位 s
     */
    fun retryDelay(retryDelay: Int): R {
        this.retryDelay = retryDelay
        return this as R
    }

    /**
     * 是否是同步请求，默认为异步请求
     */
    fun syncRequest(syncRequest: Boolean): R {
        this.isSyncRequest = syncRequest
        return this as R
    }

    /**
     * 设置Converter.Factory,默认GsonConverterFactory.create()
     */
    fun addConverterFactory(factory: retrofit2.Converter.Factory): R {
        converterFactories.add(factory)
        return this as R
    }

    /**
     * 设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
     */
    fun addCallAdapterFactory(factory: CallAdapter.Factory): R {
        adapterFactories.add(factory)
        return this as R
    }

    /**
     * 添加头信息
     */
    fun headers(headers: HttpHeaders): R {
        httpHeaders.put(headers)
        return this as R
    }

    /**
     * 添加头信息
     */
    fun headers(key: String?, value: String?): R {
        httpHeaders.put(key, value)
        return this as R
    }

    /**
     * 移除头信息
     */
    fun removeHeader(key: String): R {
        httpHeaders.remove(key)
        return this as R
    }

    /**
     * 移除所有头信息
     */
    fun removeAllHeaders(): R {
        httpHeaders.clear()
        return this as R
    }

    /**
     * 设置参数
     */
    fun params(params: HttpParams): R {
        httpParams.put(params)
        return this as R
    }

    /**
     * 设置参数
     */
    fun params(key: String?, value: String?): R {
        httpParams.put(key, value)
        return this as R
    }

    /**
     * 设置参数
     */
    fun params(keyValues: Map<String?, String?>?): R {
        httpParams.put(keyValues)
        return this as R
    }

    /**
     * 通过 key，移除参数
     */
    fun removeParam(key: String): R {
        httpParams.remove(key)
        return this as R
    }

    /**
     * 清除参数
     */
    fun removeAllParams(): R {
        httpParams.clear()
        return this as R
    }

    /**
     * 是否进行全局错误统一处理
     */
    fun globalErrorHandle(isGlobalErrorHandle: Boolean): R {
        this.isGlobalErrorHandle = isGlobalErrorHandle
        return this as R
    }

    /**
     * 添加 okhttp 缓存
     */
    fun okCache(cache: Cache): R {
        this.cache = cache
        return this as R
    }

    /**
     * 缓存模式
     */
    fun cacheMode(cacheMode: CacheMode): R {
        this.cacheMode = cacheMode
        return this as R
    }

    /**
     * 缓存的时间 单位:秒
     */
    fun cacheTime(cacheTime: Long): R {
        var newCacheTime = cacheTime
        if (newCacheTime <= -1) newCacheTime = FlyHttp.DEFAULT_CACHE_NEVER_EXPIRE
        this.cacheTime = newCacheTime
        return this as R
    }

    /**
     * 缓存 Key
     */
    fun cacheKey(cacheKey: String): R {
        this.cacheKey = cacheKey
        return this as R
    }

    /**
     * 设置缓存的转换器
     */
    fun cacheDiskConverter(converter: IDiskConverter): R {
        diskConverter = converter
        return this as R
    }

    /**
     * 是否进行断点下载
     */
    fun breakpointDownload(isBreakpointDownload: Boolean): R {
        this.isBreakpointDownload = isBreakpointDownload
        return this as R
    }

    /**
     * 根据当前的请求参数，生成对应的 OkHttpClient
     */
    private fun generateOkClient(): OkHttpClient.Builder {
        if (readTimeOut > 0) okHttpNewBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
        if (writeTimeOut > 0) okHttpNewBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
        if (connectTimeout > 0) okHttpNewBuilder.connectTimeout(
            connectTimeout,
            TimeUnit.MILLISECONDS
        )
        if (cookies.size > 0) FlyHttp.getCookieJar()?.addCookies(cookies)

        hostnameVerifier?.let(okHttpNewBuilder::hostnameVerifier)
        sslParams?.let { okHttpNewBuilder.sslSocketFactory(it.sslSocketFactory, it.trustManager) }
        proxy?.let(okHttpNewBuilder::proxy)

        //添加头  头添加放在最前面方便其他拦截器可能会用到
        if (!httpHeaders.isEmpty()) {
            okHttpNewBuilder.addInterceptor(HeadersInterceptor(httpHeaders))
        }
        interceptors.forEach { okHttpNewBuilder.addInterceptor(it) }
        networkInterceptors.forEach { okHttpNewBuilder.addNetworkInterceptor(it) }

        okHttpNewBuilder.interceptors().forEach {
            if (it is BaseDynamicInterceptor<*>) {
                it.sign(sign).timeStamp(timeStamp).accessToken(accessToken)
            }
        }
        return okHttpNewBuilder
    }

    /**
     * 根据当前的请求参数，生成对应的Retrofit
     */
    private fun generateRetrofit(): Retrofit.Builder {
        if (converterFactories.isEmpty() && adapterFactories.isEmpty()) {
            val builder = FlyHttp.getRetrofitBuilder()
            if (!TextUtils.isEmpty(baseUrl)) {
                builder.baseUrl(baseUrl!!)
            }
            return builder
        }

        val builder = FlyHttp.getRetrofit().newBuilder()
        if (!TextUtils.isEmpty(baseUrl)) {
            builder.baseUrl(baseUrl!!)
        }
        converterFactories.forEach {
            builder.addConverterFactory(it)
        }
        adapterFactories.forEach {
            builder.addCallAdapterFactory(it)
        }
        return builder
    }

    /**
     * 根据当前的请求参数，生成对应的RxCache和Cache
     */
    private fun generateRxCache(): RxCache.Builder {
        val rxCacheBuilder: RxCache.Builder = FlyHttp.getRxCacheBuilder()
        when (cacheMode) {
            NO_CACHE -> {
                val noCacheInterceptor = NoCacheInterceptor()
                interceptors.add(noCacheInterceptor)
                networkInterceptors.add(noCacheInterceptor)
            }
            DEFAULT -> {
                if (cache == null) {
                    val cacheDirectory: File? = FlyHttp.getCacheDirectory()
                    cache = cacheDirectory?.let {
                        val maxSize =
                            (5 * 1024 * 1024).coerceAtLeast(FlyHttp.getCacheMaxSize().toInt())
                                .toLong()
                        Cache(it, maxSize)
                    }
                }
                val cacheControlValue =
                    String.format("max-age=%d", (-1).coerceAtLeast(cacheTime.toInt()))
                val cacheInterceptor = CacheInterceptor(cacheControlValue)
                val cacheInterceptorOffline = CacheInterceptorOffline(cacheControlValue)
                networkInterceptors.add(cacheInterceptor)
                networkInterceptors.add(cacheInterceptorOffline)
                interceptors.add(cacheInterceptorOffline)
            }
            FIRSTREMOTE, FIRSTCACHE, ONLYREMOTE, ONLYCACHE, CACHEANDREMOTE, CACHEANDREMOTEDISTINCT -> {
                interceptors.add(NoCacheInterceptor())
                return if (diskConverter == null) {
                    rxCacheBuilder.also {
                        it.cacheKey(checkNotNull(cacheKey, "cacheKey == null"))
                            .cacheTime(cacheTime)
                    }
                } else {
                    FlyHttp.getRxCache().newBuilder().also {
                        it.diskConverter(diskConverter)
                            .cacheKey(checkNotNull(cacheKey, "cacheKey == null"))
                            .cacheTime(cacheTime)
                    }
                }
            }
        }
        return rxCacheBuilder
    }

    fun build(): RequestCall {
        return RequestCall(this)
    }

    abstract fun generateRequest(): Observable<ResponseBody>?
}
