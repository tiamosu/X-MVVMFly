package com.tiamosu.fly.http.request

import android.annotation.SuppressLint
import android.text.TextUtils
import com.blankj.utilcode.util.Utils
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.FlyHttp.Companion.getRxCache
import com.tiamosu.fly.http.api.ApiService
import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.converter.IDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.cache.model.CacheMode.*
import com.tiamosu.fly.http.interceptors.*
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.HttpParams
import com.tiamosu.fly.http.ssl.HttpsUtils
import com.tiamosu.fly.http.utils.FlyHttpLog
import com.tiamosu.fly.http.utils.RxUtil
import com.tiamosu.fly.utils.FlyUtils
import com.tiamosu.fly.utils.Preconditions
import io.reactivex.Observable
import okhttp3.*
import retrofit2.CallAdapter
import retrofit2.Converter
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
@Suppress("UNCHECKED_CAST")
abstract class BaseRequest<R : BaseRequest<R>>(protected val url: String) {
    //读超时，单位 ms
    private var readTimeOut = 0L
    //写超时，单位 ms
    private var writeTimeOut = 0L
    //链接超时，单位 ms
    private var connectTimeout = 0L
    //代理
    private var proxy: Proxy? = null
    //使用 verify 函数效验服务器主机名的合法性
    private var hostnameVerifier: HostnameVerifier? = null
    //获取 SSLSocketFactory 和 X509TrustManager
    private var sslParams: HttpsUtils.SSLParams? = null
    //拦截器
    private val interceptors: MutableList<Interceptor> = mutableListOf()
    //网络拦截器
    private val networkInterceptors: MutableList<Interceptor> = mutableListOf()
    //是否需要签名
    private var sign = false
    //是否需要追加时间戳
    private var timeStamp = false
    //是否需要追加 token
    private var accessToken = false
    //用户手动添加的 Cookie
    private var cookies: MutableList<Cookie> = mutableListOf()


    //Okhttp缓存对象
    private var cache: Cache? = null
    //缓存类型，默认无缓存
    protected var cacheMode: CacheMode = NO_CACHE
    //缓存时间
    private var cacheTime = -1L
    //缓存Key
    private var cacheKey: String? = null
    //设置RxCache磁盘转换器
    private var diskConverter: IDiskConverter? = null


    //BaseUrl
    private var baseUrl: String? = null
    //HttpUrl
    private var httpUrl: HttpUrl? = null
    //超时重试次数，默认3次
    protected var retryCount = 3
    //超时重试延时，单位 ms
    protected var retryDelay = 0L
    //超时重试叠加延时，单位 ms
    protected var retryIncreaseDelay = 0L
    //是否是同步请求
    protected var isSyncRequest = false
    //转换器
    private var converterFactories: MutableList<Converter.Factory> = mutableListOf()
    //适配器
    private var adapterFactories: MutableList<CallAdapter.Factory> = mutableListOf()


    private val flyHttp by lazy { FlyHttp.instance }
    //添加的 header
    private val httpHeaders by lazy { HttpHeaders() }
    //添加的 param
    protected val httpParams by lazy { HttpParams() }
    //retrofit
    private var retrofit: Retrofit? = null
    //rxCache缓存
    protected var rxCache: RxCache? = null
    //通用的的api接口
    protected var apiManager: ApiService? = null
    //okHttpClient
    private var okHttpClient: OkHttpClient? = null

    init {
        baseUrl = FlyHttp.getBaseUrl()
        if (!TextUtils.isEmpty(baseUrl)) {
            httpUrl = HttpUrl.parse(baseUrl!!)
        }
        if (baseUrl == null && (url.startsWith("http://") || url.startsWith("https://"))) {
            httpUrl = HttpUrl.parse(url)
            baseUrl = httpUrl!!.url().protocol + "://" + httpUrl!!.url().host + "/"
        }
        cacheMode = FlyHttp.getCacheMode()
        cacheTime = FlyHttp.getCacheTime()
        retryCount = FlyHttp.getRetryCount()
        retryDelay = FlyHttp.getRetryDelay()
        retryIncreaseDelay = FlyHttp.getRetryIncreaseDelay()
        cache = FlyHttp.getHttpCache()

        //默认添加 Accept-Language
        val acceptLanguage = HttpHeaders.acceptLanguage
        if (!TextUtils.isEmpty(acceptLanguage)) headers(
            HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE,
            acceptLanguage
        )
        //默认添加 User-Agent
        val userAgent = HttpHeaders.userAgent
        if (!TextUtils.isEmpty(userAgent)) headers(
            HttpHeaders.HEAD_KEY_USER_AGENT,
            userAgent
        )
        //添加公共请求参数
        flyHttp.getCommonParams()?.let(httpParams::put)
        flyHttp.getCommonHeaders()?.let(httpHeaders::put)
    }

    fun readTimeOut(readTimeOut: Long): R {
        this.readTimeOut = readTimeOut
        return this as R
    }

    fun writeTimeOut(writeTimeOut: Long): R {
        this.writeTimeOut = writeTimeOut
        return this as R
    }

    fun connectTimeout(connectTimeout: Long): R {
        this.connectTimeout = connectTimeout
        return this as R
    }

    /**
     * 设置代理
     */
    fun okproxy(proxy: Proxy?): R {
        this.proxy = proxy
        return this as R
    }

    /**
     * https的全局访问规则
     */
    fun hostnameVerifier(hostnameVerifier: HostnameVerifier?): R {
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

    fun addInterceptor(interceptor: Interceptor?): R {
        interceptor?.let(interceptors::add)
        return this as R
    }

    fun addNetworkInterceptor(networkInterceptor: Interceptor?): R {
        networkInterceptor?.let(networkInterceptors::add)
        return this as R
    }

    fun sign(sign: Boolean): R {
        this.sign = sign
        return this as R
    }

    fun timeStamp(timeStamp: Boolean): R {
        this.timeStamp = timeStamp
        return this as R
    }

    fun accessToken(accessToken: Boolean): R {
        this.accessToken = accessToken
        return this as R
    }

    fun addCookie(name: String, value: String): R {
        val builder = Cookie.Builder()
        if (httpUrl != null) {
            val cookie = builder.name(name).value(value).domain(httpUrl!!.host()).build()
            cookies.add(cookie)
        }
        return this as R
    }

    fun addCookie(cookie: Cookie?): R {
        cookie?.let(cookies::add)
        return this as R
    }

    fun addCookies(cookies: List<Cookie>?): R {
        cookies?.let(this.cookies::addAll)
        return this as R
    }

    fun okCache(cache: Cache?): R {
        this.cache = cache
        return this as R
    }

    fun cacheMode(cacheMode: CacheMode?): R {
        this.cacheMode = cacheMode!!
        return this as R
    }

    fun cacheTime(cacheTime: Long): R {
        var newCacheTime = cacheTime
        if (newCacheTime <= -1) newCacheTime = FlyHttp.DEFAULT_CACHE_NEVER_EXPIRE
        this.cacheTime = newCacheTime
        return this as R
    }

    fun cacheKey(cacheKey: String?): R {
        this.cacheKey = cacheKey
        return this as R
    }

    /**
     * 设置缓存的转换器
     */
    fun cacheDiskConverter(converter: IDiskConverter?): R {
        this.diskConverter = converter
        return this as R
    }

    fun baseUrl(baseUrl: String): R {
        this.baseUrl = baseUrl
        if (!TextUtils.isEmpty(baseUrl)) httpUrl = HttpUrl.parse(baseUrl)
        return this as R
    }

    fun retryCount(retryCount: Int): R {
        this.retryCount = retryCount
        return this as R
    }

    fun retryDelay(retryDelay: Long): R {
        this.retryDelay = retryDelay
        return this as R
    }

    fun retryIncreaseDelay(retryIncreaseDelay: Long): R {
        this.retryIncreaseDelay = retryIncreaseDelay
        return this as R
    }

    fun syncRequest(syncRequest: Boolean): R {
        this.isSyncRequest = syncRequest
        return this as R
    }

    /**
     * 设置Converter.Factory,默认GsonConverterFactory.create()
     */
    fun addConverterFactory(factory: Converter.Factory?): R {
        factory?.let(converterFactories::add)
        return this as R
    }

    /**
     * 设置CallAdapter.Factory,默认RxJavaCallAdapterFactory.create()
     */
    fun addCallAdapterFactory(factory: CallAdapter.Factory?): R {
        factory?.let(adapterFactories::add)
        return this as R
    }

    /**
     * 添加头信息
     */
    fun headers(headers: HttpHeaders?): R {
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
    fun removeHeader(key: String?): R {
        key?.let(httpHeaders::remove)
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
    fun params(params: HttpParams?): R {
        httpParams.put(params)
        return this as R
    }

    fun params(key: String?, value: String?): R {
        httpParams.put(key, value)
        return this as R
    }

    fun params(keyValues: Map<String?, String?>?): R {
        httpParams.put(keyValues)
        return this as R
    }

    fun removeParam(key: String?): R {
        key?.let(httpParams::remove)
        return this as R
    }

    fun removeAllParams(): R {
        httpParams.clear()
        return this as R
    }

    /**
     * 移除缓存（key）
     */
    @SuppressLint("CheckResult")
    fun removeCache(key: String) {
        getRxCache().remove(key).compose(RxUtil.io2main()).subscribe(
            { FlyHttpLog.i("removeCache success!!!") },
            { throwable -> FlyHttpLog.i("removeCache err!!!$throwable") })
    }

    /**
     * 根据当前的请求参数，生成对应的 OkHttpClient
     */
    private fun generateOkClient(): OkHttpClient.Builder {
        val builder = FlyUtils.getAppComponent().okHttpClient().newBuilder()
        if (readTimeOut > 0) builder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
        if (writeTimeOut > 0) builder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
        if (connectTimeout > 0) builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        if (cookies.size > 0) FlyHttp.getCookieJar()?.addCookies(cookies)
        hostnameVerifier?.let(builder::hostnameVerifier)
        sslParams?.let { builder.sslSocketFactory(it.sslSocketFactory!!, it.trustManager!!) }
        proxy?.let(builder::proxy)

        //添加头  头添加放在最前面方便其他拦截器可能会用到
        if (!httpHeaders.isEmpty()) {
            builder.addInterceptor(HeadersInterceptor(httpHeaders))
        }
        interceptors.forEach {
            builder.addInterceptor(it)
        }
        builder.interceptors().forEach {
            if (it is BaseDynamicInterceptor<*>) {
                it.sign(sign).timeStamp(timeStamp).accessToken(accessToken)
            }
        }
        networkInterceptors.forEach { builder.addNetworkInterceptor(it) }
        return builder
    }

    /**
     * 根据当前的请求参数，生成对应的Retrofit
     */
    private fun generateRetrofit(): Retrofit.Builder {
        val builder = FlyUtils.getAppComponent().retrofit().newBuilder()
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
                    var cacheDirectory: File? = FlyHttp.getCacheDirectory()
                    if (cacheDirectory == null) {
                        cacheDirectory = File(Utils.getApp().cacheDir, "data-cache")
                    } else {
                        if (cacheDirectory.isDirectory && !cacheDirectory.exists()) {
                            cacheDirectory.mkdirs()
                        }
                    }
                    cache = Cache(
                        cacheDirectory,
                        (5 * 1024 * 1024).coerceAtLeast(FlyHttp.getCacheMaxSize()).toLong()
                    )
                }
                val cacheControlValue =
                    String.format("max-age=%d", (-1).coerceAtLeast(cacheTime.toInt()))
                val rewriteCacheControlInterceptor = CacheInterceptor(cacheControlValue)
                val rewriteCacheControlInterceptorOffline =
                    CacheInterceptorOffline(cacheControlValue)
                networkInterceptors.add(rewriteCacheControlInterceptor)
                networkInterceptors.add(rewriteCacheControlInterceptorOffline)
                interceptors.add(rewriteCacheControlInterceptorOffline)
            }
            FIRSTREMOTE, FIRSTCACHE, ONLYREMOTE, ONLYCACHE, CACHEANDREMOTE, CACHEANDREMOTEDISTINCT -> {
                interceptors.add(NoCacheInterceptor())
                return if (diskConverter == null) {
                    rxCacheBuilder.cachekey(
                        Preconditions.checkNotNull(cacheKey, "cacheKey == null")
                    ).cacheTime(cacheTime)
                    rxCacheBuilder
                } else {
                    val cacheBuilder: RxCache.Builder = getRxCache().newBuilder()
                    cacheBuilder.diskConverter(diskConverter!!)
                        .cachekey(Preconditions.checkNotNull(cacheKey, "cacheKey == null"))
                        .cacheTime(cacheTime)
                    cacheBuilder
                }
            }
        }
        return rxCacheBuilder
    }

    protected open fun build(): R {
//        val rxCacheBuilder = generateRxCache()
        val okHttpClientBuilder = generateOkClient()
        if (cacheMode === DEFAULT) { //okhttp缓存
            okHttpClientBuilder.cache(cache)
        }
        val retrofitBuilder = generateRetrofit()
        okHttpClient = okHttpClientBuilder.build().also {
            it.let(retrofitBuilder::client)
        }
        retrofit = retrofitBuilder.build()

//        rxCache = rxCacheBuilder.build()
        apiManager = FlyUtils.getAppComponent().repositoryManager()
            .obtainRetrofitService(ApiService::class.java, retrofit)
        return this as R
    }

    protected abstract fun generateRequest(): Observable<ResponseBody>?
}
