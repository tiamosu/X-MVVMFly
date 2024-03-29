package com.tiamosu.fly.http

import android.annotation.SuppressLint
import android.text.TextUtils
import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.converter.IDiskConverter
import com.tiamosu.fly.http.cache.converter.SerializableDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.cookie.CookieManger
import com.tiamosu.fly.http.https.HttpsUtils
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.HttpParams
import com.tiamosu.fly.http.request.*
import com.tiamosu.fly.http.utils.FlyHttpLog
import com.tiamosu.fly.http.utils.main
import com.tiamosu.fly.utils.getAppComponent
import com.tiamosu.fly.utils.getHttpCacheFile
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.io.InputStream
import java.net.Proxy
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * 描述：网络请求入口类
 *
 * 主要功能：
 * 1.全局设置超时时间
 * 2.支持请求错误重试相关参数，包括重试次数、重试延时时间
 * 3.支持支持get、post、put、delete请求
 * 4.支持支持自定义请求
 * 5.支持文件上传、下载
 * 6.支持全局公共请求头
 * 7.支持全局公共参数
 * 8.支持okHttp相关参数，包括拦截器
 * 9.支持Retrofit相关参数
 * 10.支持Cookie管理
 *
 * @author tiamosu
 * @date 2020/2/26.
 */
@Suppress("unused")
class FlyHttp {
    private var cookieJar: CookieManger? = null                     //Cookie管理
    private var baseUrl: String? = null                             //全局 BaseUrl
    private var retryCount = DEFAULT_RETRY_COUNT                    //超时重试次数，默认0次
    private var retryDelay = DEFAULT_RETRY_DELAY                    //超时重试延时，单位 s
    private var commonHeaders: HttpHeaders? = null                  //全局公共请求头
    private var commonParams: HttpParams? = null                    //全局公共请求参数
    private var cache: Cache? = null                                //OkHttp缓存对象
    private var cacheMode: CacheMode = CacheMode.NO_CACHE           //缓存类型
    private var cacheTime = -1L                                     //缓存时间
    private var cacheDirectory: File? = null                        //缓存目录
    private var cacheMaxSize = 0L                                   //缓存大小
    private var isGlobalErrorHandle = false                         //是否进行全局错误统一处理
    private var httpLoggingInterceptor: HttpLoggingInterceptor? = null  //数据请求打印拦截器

    //OkHttpClient请求的Builder
    private val okHttpClientBuilder by lazy {
        getAppComponent().okHttpClient().newBuilder()
            .apply {
                val sslParams = HttpsUtils.getSslSocketFactory()
                sslSocketFactory(sslParams.sslSocketFactory, sslParams.trustManager)
                hostnameVerifier(HttpsUtils.DefaultHostnameVerifier())
            }
    }

    //Retrofit请求Builder
    private val retrofitBuilder by lazy { getAppComponent().retrofit().newBuilder() }

    //RxCache请求的Builder
    private val globalRxCacheBuilder by lazy {
        RxCache.Builder().init()
            .diskConverter(SerializableDiskConverter()) //目前只支持Serializable和Gson缓存其它可以自己扩展
    }

    /**
     * 全局读取超时时间，单位 ms
     */
    fun setReadTimeOut(readTimeOut: Long): FlyHttp {
        okHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
        return this
    }

    /**
     * 全局写入超时时间，单位 ms
     */
    fun setWriteTimeOut(writeTimeout: Long): FlyHttp {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
        return this
    }

    /**
     * 全局连接超时时间，单位 ms
     */
    fun setConnectTimeout(connectTimeout: Long): FlyHttp {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
        return this
    }

    /**
     * 全局设置代理
     */
    fun setOkHttpProxy(proxy: Proxy): FlyHttp {
        okHttpClientBuilder.proxy(proxy)
        return this
    }

    /**
     * https的全局访问规则
     */
    fun setHostnameVerifier(hostnameVerifier: HostnameVerifier): FlyHttp {
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier)
        return this
    }

    /**
     * 信任所以证书
     */
    fun setCertificates(): FlyHttp {
        val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory()
        okHttpClientBuilder.sslSocketFactory(sslParams.sslSocketFactory, sslParams.trustManager)
        return this
    }

    /**
     * https的全局自签名证书
     */
    fun setCertificates(certificates: Array<InputStream>): FlyHttp {
        val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory(certificates)
        okHttpClientBuilder.sslSocketFactory(sslParams.sslSocketFactory, sslParams.trustManager)
        return this
    }

    /**
     * https双向认证证书
     */
    fun setCertificates(
        bksFile: InputStream,
        password: String,
        certificates: Array<InputStream>
    ): FlyHttp {
        val sslParams: HttpsUtils.SSLParams =
            HttpsUtils.getSslSocketFactory(bksFile, password, certificates)
        okHttpClientBuilder.sslSocketFactory(sslParams.sslSocketFactory, sslParams.trustManager)
        return this
    }

    /**
     * 添加全局拦截器
     */
    fun addInterceptor(interceptor: Interceptor): FlyHttp {
        okHttpClientBuilder.addInterceptor(interceptor)
        return this
    }

    /**
     * 添加全局网络拦截器
     */
    fun addNetworkInterceptor(interceptor: Interceptor): FlyHttp {
        okHttpClientBuilder.addNetworkInterceptor(interceptor)
        return this
    }

    /**
     * 全局cookie存取规则
     */
    fun setCookieStore(cookieManager: CookieManger): FlyHttp {
        cookieJar = cookieManager
        okHttpClientBuilder.cookieJar(cookieManager)
        return this
    }

    /**
     * 全局设置 baseUrl
     */
    fun setBaseUrl(baseUrl: String): FlyHttp {
        this.baseUrl = baseUrl
        return this
    }

    /**
     * 超时重试次数
     */
    fun setRetryCount(retryCount: Int): FlyHttp {
        this.retryCount = retryCount
        return this
    }

    /**
     * 超时重试延迟时间，单位 s
     */
    fun setRetryDelay(retryDelay: Int): FlyHttp {
        this.retryDelay = retryDelay
        return this
    }

    /**
     * 全局设置请求的连接池
     */
    fun setOkHttpConnectionPool(connectionPool: ConnectionPool): FlyHttp {
        okHttpClientBuilder.connectionPool(connectionPool)
        return this
    }

    /**
     * 全局为Retrofit设置自定义的OkHttpClient
     */
    fun setOkHttpClient(client: OkHttpClient): FlyHttp {
        retrofitBuilder.client(client)
        return this
    }

    /**
     * 全局设置Converter.Factory，默认GsonConverterFactory.create()
     */
    fun addConverterFactory(factory: Converter.Factory): FlyHttp {
        retrofitBuilder.addConverterFactory(factory)
        return this
    }

    /**
     * 全局设置CallAdapter.Factory，默认RxJavaCallAdapterFactory.create()
     */
    fun addCallAdapterFactory(factory: CallAdapter.Factory): FlyHttp {
        retrofitBuilder.addCallAdapterFactory(factory)
        return this
    }

    /**
     * 全局设置Retrofit callbackExecutor
     */
    fun setCallbackExecutor(executor: Executor): FlyHttp {
        retrofitBuilder.callbackExecutor(executor)
        return this
    }

    /**
     * 全局设置Retrofit对象Factory
     */
    fun setCallFactory(factory: Call.Factory): FlyHttp {
        retrofitBuilder.callFactory(factory)
        return this
    }

    /**
     * 添加全局公共请求参数
     */
    fun addCommonParams(commonParams: HttpParams): FlyHttp {
        if (this.commonParams == null) this.commonParams = HttpParams()
        this.commonParams?.put(commonParams)
        return this
    }

    /**
     * 添加全局公共请求参数
     */
    fun addCommonHeaders(commonHeaders: HttpHeaders): FlyHttp {
        if (this.commonHeaders == null) this.commonHeaders = HttpHeaders()
        this.commonHeaders?.put(commonHeaders)
        return this
    }

    /**
     * 全局设置OkHttp的缓存，默认是3天
     */
    fun setHttpCache(cache: Cache): FlyHttp {
        this.cache = cache
        return this
    }

    /**
     * 全局的缓存模式
     */
    fun setCacheMode(cacheMode: CacheMode): FlyHttp {
        this.cacheMode = cacheMode
        return this
    }

    /**
     * 全局的缓存过期时间
     */
    fun setCacheTime(cacheTime: Long): FlyHttp {
        var newCacheTime = cacheTime
        if (newCacheTime <= -1) newCacheTime = DEFAULT_CACHE_NEVER_EXPIRE
        this.cacheTime = newCacheTime
        return this
    }

    /**
     * 全局设置缓存的路径，默认是应用包下面的缓存
     */
    fun setCacheDirectory(directory: File?): FlyHttp {
        this.cacheDirectory = directory
        globalRxCacheBuilder.diskDir(directory)
        return this
    }

    /**
     * 全局的缓存大小,默认50M
     */
    fun setCacheMaxSize(maxSize: Long): FlyHttp {
        this.cacheMaxSize = maxSize
        return this
    }

    /**
     * 全局设置缓存的版本，默认为1，缓存的版本号
     */
    fun setCacheVersion(cacheVersion: Int): FlyHttp {
        check(cacheVersion >= 0) { "cacheVersion must > 0" }
        globalRxCacheBuilder.appVersion(cacheVersion)
        return this
    }

    /**
     * 全局设置缓存的转换器
     */
    fun setCacheDiskConverter(converter: IDiskConverter): FlyHttp {
        globalRxCacheBuilder.diskConverter(converter)
        return this
    }

    /**
     * 是否进行全局错误统一处理
     */
    fun setGlobalErrorHandle(isGlobalErrorHandle: Boolean): FlyHttp {
        this.isGlobalErrorHandle = isGlobalErrorHandle
        return this
    }

    /**
     * 是否打印数据请求结果
     * 注意：执行应用下载时（[download]）需要关闭输出，否则影响文件下载进度判断
     */
    fun setPrintEnable(
        isEnable: Boolean = false,
        interceptor: HttpLoggingInterceptor? = null
    ): FlyHttp {
        if (isEnable) {
            httpLoggingInterceptor = interceptor ?: HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        }
        return this
    }

    companion object {
        const val DEFAULT_RETRY_COUNT = 0           //默认重试次数
        const val DEFAULT_RETRY_DELAY = 2           //默认重试延时
        const val DEFAULT_CACHE_NEVER_EXPIRE = -1L   //缓存过期时间，默认永久缓存

        val instance by lazy { FlyHttp() }

        /**
         * get请求
         */
        @JvmStatic
        operator fun get(url: String): GetRequest {
            return GetRequest(url)
        }

        /**
         * post请求
         */
        @JvmStatic
        fun post(url: String): PostRequest {
            return PostRequest(url)
        }

        /**
         * put请求
         */
        @JvmStatic
        fun put(url: String): PutRequest {
            return PutRequest(url)
        }

        /**
         * delete请求
         */
        @JvmStatic
        fun delete(url: String): DeleteRequest {
            return DeleteRequest(url)
        }

        /**
         * 文件下载
         */
        @JvmStatic
        fun download(url: String): DownloadRequest {
            return DownloadRequest(url)
        }

        /**
         * 文件上传
         */
        @JvmStatic
        fun upload(url: String): UploadRequest {
            return UploadRequest(url)
        }

        /**
         * 自定求请求
         */
        @JvmStatic
        fun custom(url: String): CustomRequest {
            return CustomRequest(url)
        }

        internal fun getOkHttpClient(): OkHttpClient {
            return instance.okHttpClientBuilder.build()
        }

        internal fun getRetrofitBuilder(): Retrofit.Builder {
            return instance.retrofitBuilder
        }

        internal fun getRetrofit(): Retrofit {
            return instance.retrofitBuilder.build()
        }

        internal fun getHttpLoggingInterceptor(): HttpLoggingInterceptor? {
            return instance.httpLoggingInterceptor
        }

        /**
         * 获取全局的cookie实例
         */
        @JvmStatic
        fun getCookieJar(): CookieManger? {
            return instance.cookieJar
        }

        /**
         * 获取全局 BaseUrl
         */
        @JvmStatic
        fun getBaseUrl(): String? {
            var baseUrl = instance.baseUrl
            if (TextUtils.isEmpty(baseUrl)) {
                val httpUrl = getRetrofit().baseUrl()
                baseUrl = httpUrl.toUrl().toString()
            }
            return baseUrl
        }

        /**
         * 超时重试次数
         */
        @JvmStatic
        fun getRetryCount(): Int {
            return instance.retryCount
        }

        /**
         * 超时重试延迟时间
         */
        @JvmStatic
        internal fun getRetryDelay(): Int {
            return instance.retryDelay
        }

        /**
         * 获取全局公共请求参数
         */
        @JvmStatic
        fun getCommonParams(): HttpParams? {
            return instance.commonParams
        }

        /**
         * 获取全局公共请求头
         */
        @JvmStatic
        fun getCommonHeaders(): HttpHeaders? {
            return instance.commonHeaders
        }

        /**
         * 获取OkHttp的缓存
         */
        @JvmStatic
        fun getHttpCache(): Cache? {
            return instance.cache
        }

        /**
         * 获取全局的缓存模式
         */
        @JvmStatic
        fun getCacheMode(): CacheMode {
            return instance.cacheMode
        }

        /**
         * 获取全局的缓存过期时间
         */
        @JvmStatic
        fun getCacheTime(): Long {
            return instance.cacheTime
        }

        /**
         * 获取缓存的路劲
         */
        @JvmStatic
        fun getCacheDirectory(): File? {
            return instance.cacheDirectory ?: getHttpCacheFile()
        }

        /**
         * 获取全局的缓存大小
         */
        @JvmStatic
        fun getCacheMaxSize(): Long {
            return instance.cacheMaxSize
        }

        /**
         * 对外暴露RxCache，方便自定义
         */
        @JvmStatic
        fun getRxCacheBuilder(): RxCache.Builder {
            return instance.globalRxCacheBuilder
        }

        @JvmStatic
        fun getRxCache(): RxCache {
            return instance.globalRxCacheBuilder.build()
        }

        @JvmStatic
        fun isGlobalErrorHandle(): Boolean {
            return instance.isGlobalErrorHandle
        }

        /**
         * 清空缓存
         */
        @SuppressLint("CheckResult")
        @JvmStatic
        fun clearCache() {
            getRxCache().clear()
                .compose(main())
                .subscribe(
                    { FlyHttpLog.iLog("clearCache success!!!") },
                    { FlyHttpLog.iLog("clearCache err!!!") })
        }

        /**
         * 移除缓存（key）
         */
        @SuppressLint("CheckResult")
        @JvmStatic
        fun removeCache(key: String?) {
            getRxCache().remove(key)
                .compose(main())
                .subscribe(
                    { FlyHttpLog.iLog("removeCache success!!!") },
                    { FlyHttpLog.iLog("removeCache err!!!") })
        }

        /**
         * 取消订阅
         */
        @JvmStatic
        fun cancelSubscription(disposable: Disposable?) {
            if (disposable?.isDisposed == false) {
                disposable.dispose()
            }
        }

        /**
         * 自定义Schedulers的线程池，在频繁使用Rxjava的时候仅使用单个调用度的实例
         */
        internal val scheduler: Scheduler by lazy {
            getAppComponent().executorService().let { Schedulers.from(it) }
        }
    }
}