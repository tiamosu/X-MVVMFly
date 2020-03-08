package com.tiamosu.fly.http.request.base

import android.text.TextUtils
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.api.ApiService
import com.tiamosu.fly.http.callback.Callback
import com.tiamosu.fly.http.https.HttpsUtils
import com.tiamosu.fly.http.interceptors.BaseDynamicInterceptor
import com.tiamosu.fly.http.interceptors.HeadersInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.HttpParams
import com.tiamosu.fly.http.request.RequestCall
import com.tiamosu.fly.utils.FlyUtils
import io.reactivex.Observable
import okhttp3.*
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.io.InputStream
import java.net.Proxy
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * @author tiamosu
 * @date 2020/2/26.
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class BaseRequest<T, R : BaseRequest<T, R>>(val url: String) {
    var readTimeOut = 0L                                //读超时，单位 ms
        private set
    var writeTimeOut = 0L                               //写超时，单位 ms
        private set
    var connectTimeout = 0L                             //链接超时，单位 ms
        private set
    var proxy: Proxy? = null                            //代理
        private set
    var hostnameVerifier: HostnameVerifier? = null      //使用 verify 函数效验服务器主机名的合法性
        private set
    var sslParams: HttpsUtils.SSLParams? = null         //获取 SSLSocketFactory 和 X509TrustManager
        private set
    var sign = false                                    //是否需要签名
        private set
    var timeStamp = false                               //是否需要追加时间戳
        private set
    var accessToken = false                             //是否需要追加 token
        private set
    var cookies: MutableList<Cookie> = mutableListOf()  //用户手动添加的 Cookie
        private set
    val interceptors: MutableList<Interceptor> = mutableListOf()        //拦截器
    val networkInterceptors: MutableList<Interceptor> = mutableListOf() //网络拦截器

    var baseUrl: String? = null
        private set
    var httpUrl: HttpUrl? = null
        private set
    var retryCount = 3                                  //超时重试次数，默认3次
        private set
    var retryDelay = 0                                  //超时重试延时，单位 s
        private set
    var isSyncRequest = false                           //是否是同步请求，默认为异步请求
        private set
    val converterFactories: MutableList<retrofit2.Converter.Factory> = mutableListOf()    //转换器
    val adapterFactories: MutableList<CallAdapter.Factory> = mutableListOf()    //适配器

    val httpHeaders by lazy { HttpHeaders() }           //添加的 header
    val httpParams by lazy { HttpParams() }             //添加的 param

    var retrofit: Retrofit? = null
        private set
    var apiService: ApiService? = null
        private set
    var okHttpClient: OkHttpClient? = null
        private set
    var isGlobalErrorHandle = true                    //是否进行全局错误统一处理
        private set

    internal var callback: Callback<*>? = null

    init {
        baseUrl = FlyHttp.getBaseUrl()
        if (!TextUtils.isEmpty(baseUrl)) {
            httpUrl = HttpUrl.parse(baseUrl!!)
        }
        if (baseUrl == null && (url.startsWith("http://") || url.startsWith("https://"))) {
            httpUrl = HttpUrl.parse(url)
            baseUrl = httpUrl!!.url().protocol + "://" + httpUrl!!.url().host + "/"
        }
        retryCount = FlyHttp.getRetryCount()
        retryDelay = FlyHttp.getRetryDelay()

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
        FlyHttp.instance.getCommonParams()?.let(httpParams::put)
        FlyHttp.instance.getCommonHeaders()?.let(httpHeaders::put)
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

    fun baseUrl(baseUrl: String): R {
        this.baseUrl = baseUrl
        if (!TextUtils.isEmpty(baseUrl)) httpUrl = HttpUrl.parse(baseUrl)
        return this as R
    }

    fun retryCount(retryCount: Int): R {
        this.retryCount = retryCount
        return this as R
    }

    fun retryDelay(retryDelay: Int): R {
        this.retryDelay = retryDelay
        return this as R
    }

    fun syncRequest(syncRequest: Boolean): R {
        this.isSyncRequest = syncRequest
        return this as R
    }

    /**
     * 设置Converter.Factory,默认GsonConverterFactory.create()
     */
    fun addConverterFactory(factory: retrofit2.Converter.Factory?): R {
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

    fun globalErrorHandle(isGlobalErrorHandle: Boolean): R {
        this.isGlobalErrorHandle = isGlobalErrorHandle
        return this as R
    }

    /**
     * 根据当前的请求参数，生成对应的 OkHttpClient
     */
    private fun generateOkClient(): OkHttpClient.Builder {
        val builder = FlyHttp.getOkHttpClient().newBuilder()
        if (readTimeOut > 0) builder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
        if (writeTimeOut > 0) builder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
        if (connectTimeout > 0) builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        if (cookies.size > 0) FlyHttp.getCookieJar()?.addCookies(cookies)
        hostnameVerifier?.let(builder::hostnameVerifier)
        sslParams?.let { builder.sslSocketFactory(it.sslSocketFactory, it.trustManager) }
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

    fun build(): RequestCall<T> {
        val okHttpClientBuilder = generateOkClient()
        val retrofitBuilder = generateRetrofit()
        okHttpClient = okHttpClientBuilder.build().also {
            it.let(retrofitBuilder::client)
        }
        retrofit = retrofitBuilder.build()
        apiService = FlyUtils.getAppComponent().repositoryManager()
            .obtainRetrofitService(ApiService::class.java, retrofit)
        return RequestCall(this)
    }

    abstract fun generateRequest(): Observable<ResponseBody>?
}
