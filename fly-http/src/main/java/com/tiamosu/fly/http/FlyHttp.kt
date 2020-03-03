package com.tiamosu.fly.http

import android.annotation.SuppressLint
import android.text.TextUtils
import com.tiamosu.fly.http.cache.RxCache
import com.tiamosu.fly.http.cache.converter.IDiskConverter
import com.tiamosu.fly.http.cache.converter.SerializableDiskConverter
import com.tiamosu.fly.http.cache.model.CacheMode
import com.tiamosu.fly.http.cookie.CookieManger
import com.tiamosu.fly.http.interceptors.HttpLoggingInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.HttpParams
import com.tiamosu.fly.http.request.GetRequest
import com.tiamosu.fly.http.ssl.HttpsUtils
import com.tiamosu.fly.http.utils.FlyHttpLog
import com.tiamosu.fly.http.utils.RxUtil
import io.reactivex.disposables.Disposable
import okhttp3.*
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.io.InputStream
import java.net.Proxy
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * 描述：网络请求入口类
 * 主要功能：
 * 1.全局设置超时时间
 * 2.支持请求错误重试相关参数，包括重试次数、重试延时时间
 * 3.支持缓存支持6种缓存模式、时间、大小、缓存目录
 * 4.支持支持GET、post、delete、put请求
 * 5.支持支持自定义请求
 * 6.支持文件上传、下载
 * 7.支持全局公共请求头
 * 8.支持全局公共参数
 * 9.支持okhttp相关参数，包括拦截器
 * 10.支持Retrofit相关参数
 * 11.支持Cookie管理
 *
 * @author tiamosu
 * @date 2020/2/26.
 */
class FlyHttp {
    //Cookie管理
    private var cookieJar: CookieManger? = null
    //Okhttp缓存对象
    private var cache: Cache? = null
    //缓存类型，默认无缓存
    private var cacheMode: CacheMode = CacheMode.NO_CACHE
    //缓存时间
    private var cacheTime = -1L
    //缓存目录
    private var cacheDirectory: File? = null
    //缓存大小
    private var cacheMaxSize = 0
    //全局 BaseUrl
    private var baseUrl1: String? = null
    //超时重试次数，默认3次
    private var retryCount = DEFAULT_RETRY_COUNT
    //超时重试延时，单位 ms
    private var retryDelay = DEFAULT_RETRY_DELAY
    //超时重试叠加延时，单位 ms
    private var retryIncreaseDelay = DEFAULT_RETRY_INCREASEDELAY
    //全局公共请求头
    private var commonHeaders: HttpHeaders? = null
    //全局公共请求参数
    private var commonParams: HttpParams? = null
    //OkHttpClient请求的Builder
    private var okHttpClientBuilder: OkHttpClient.Builder
    //Retrofit请求Builder
    private var retrofitBuilder: Retrofit.Builder
    //RxCache请求的Builder
    private var rxCacheBuilder: RxCache.Builder

    init {
        okHttpClientBuilder = OkHttpClient.Builder().apply {
            hostnameVerifier(DefaultHostnameVerifier())
            connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
        }
        retrofitBuilder = Retrofit.Builder().apply {
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }
        rxCacheBuilder = RxCache.Builder().init()
            .diskConverter(SerializableDiskConverter()) //目前只支持Serializable和Gson缓存其它可以自己扩展
    }

    /**
     * 调试模式,默认打开所有的异常调试
     */
    fun debug(tag: String?): FlyHttp {
        debug(tag, true)
        return this
    }

    /**
     * 调试模式,第二个参数表示所有catch住的log是否需要打印<br></br>
     * 一般来说,这些异常是由于不标准的数据格式,或者特殊需要主动产生的,
     * 并不是框架错误,如果不想每次打印,这里可以关闭异常显示
     */
    fun debug(tag: String?, isPrintException: Boolean): FlyHttp {
        val tempTag = if (TextUtils.isEmpty(tag)) "FlyHttp_" else tag!!
        if (isPrintException) {
            val loggingInterceptor = HttpLoggingInterceptor(tempTag, isPrintException)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        FlyHttpLog.customTagPrefix = tempTag
        FlyHttpLog.allowE = isPrintException
        FlyHttpLog.allowD = isPrintException
        FlyHttpLog.allowI = isPrintException
        FlyHttpLog.allowV = isPrintException
        return this
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    class DefaultHostnameVerifier : HostnameVerifier {
        private val verifyHostNameArray = arrayOf<String>()

        override fun verify(hostname: String, session: SSLSession): Boolean {
            return if (TextUtils.isEmpty(hostname)) {
                false
            } else !listOf(*verifyHostNameArray).contains(hostname)
        }
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
    fun setOkproxy(proxy: Proxy): FlyHttp {
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
     * https的全局自签名证书
     */
    fun setCertificates(certificates: Array<InputStream>): FlyHttp {
        val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory(certificates)
        okHttpClientBuilder.sslSocketFactory(sslParams.sslSocketFactory!!, sslParams.trustManager!!)
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
        okHttpClientBuilder.sslSocketFactory(sslParams.sslSocketFactory!!, sslParams.trustManager!!)
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
     * 全局设置缓存的转换器
     */
    fun setCacheDiskConverter(converter: IDiskConverter): FlyHttp {
        rxCacheBuilder.diskConverter(converter)
        return this
    }

    /**
     * 全局的缓存大小，默认50M
     */
    fun setCacheMaxSize(maxSize: Int): FlyHttp {
        cacheMaxSize = maxSize
        return this
    }

    /**
     * 全局设置缓存的版本，默认为1，缓存的版本号
     */
    fun setCacheVersion(cacheersion: Int): FlyHttp {
        rxCacheBuilder.appVersion(cacheersion)
        return this
    }

    /**
     * 全局设置缓存的路径，默认是应用包下面的缓存
     */
    fun setCacheDirectory(directory: File): FlyHttp {
        cacheDirectory = directory
        rxCacheBuilder.diskDir(directory)
        return this
    }

    /**
     * 全局设置baseurl
     */
    fun setBaseUrl(baseUrl: String): FlyHttp {
        baseUrl1 = baseUrl
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
     * 超时重试延迟时间，单位 ms
     */
    fun setRetryDelay(retryDelay: Int): FlyHttp {
        this.retryDelay = retryDelay
        return this
    }

    /**
     * 超时重试延迟叠加时间，单位 ms
     */
    fun setRetryIncreaseDelay(retryIncreaseDelay: Int): FlyHttp {
        this.retryIncreaseDelay = retryIncreaseDelay
        return this
    }

    /**
     * 全局设置请求的连接池
     */
    fun setOkconnectionPool(connectionPool: ConnectionPool): FlyHttp {
        okHttpClientBuilder.connectionPool(connectionPool)
        return this
    }

    /**
     * 全局为Retrofit设置自定义的OkHttpClient
     */
    fun setOkclient(client: OkHttpClient): FlyHttp {
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
    fun addCommonParams(commonParams: HttpParams?): FlyHttp {
        if (this.commonParams == null) this.commonParams = HttpParams()
        this.commonParams?.put(commonParams)
        return this
    }

    /**
     * 获取全局公共请求参数
     */
    fun getCommonParams(): HttpParams? {
        return commonParams
    }

    /**
     * 添加全局公共请求参数
     */
    fun addCommonHeaders(commonHeaders: HttpHeaders?): FlyHttp {
        if (this.commonHeaders == null) this.commonHeaders = HttpHeaders()
        this.commonHeaders?.put(commonHeaders)
        return this
    }

    /**
     * 获取全局公共请求头
     */
    fun getCommonHeaders(): HttpHeaders? {
        return commonHeaders
    }

    /**
     * get请求
     */
    operator fun get(url: String): GetRequest {
        return GetRequest(url)
    }

//    /**
//     * post请求
//     */
//    fun post(url: String?): PostRequest? {
//        return PostRequest(url)
//    }
//
//
//    /**
//     * delete请求
//     */
//    fun delete(url: String?): DeleteRequest? {
//        return DeleteRequest(url)
//    }
//
//    /**
//     * 自定义请求
//     */
//    fun custom(): CustomRequest? {
//        return CustomRequest()
//    }
//
//    fun downLoad(url: String?): DownloadRequest? {
//        return DownloadRequest(url)
//    }
//
//    fun put(url: String?): PutRequest? {
//        return PutRequest(url)
//    }

    /**
     * 取消订阅
     */
    fun cancelSubscription(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    /**
     * 清空缓存
     */
    @SuppressLint("CheckResult")
    fun clearCache() {
        getRxCache().clear().compose(RxUtil.io2main()).subscribe(
            { FlyHttpLog.i("clearCache success!!!") },
            { FlyHttpLog.i("clearCache err!!!") })
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

    companion object {
        const val DEFAULT_MILLISECONDS = 60_000L //默认的超时时间
        const val DEFAULT_RETRY_COUNT = 3 //默认重试次数
        const val DEFAULT_RETRY_INCREASEDELAY = 0 //默认重试叠加时间
        const val DEFAULT_RETRY_DELAY = 500 //默认重试延时
        const val DEFAULT_CACHE_NEVER_EXPIRE = -1L //缓存过期时间，默认永久缓存

        val instance = Holder.INSTANCE

        fun getOkHttpClient(): OkHttpClient {
            return instance.okHttpClientBuilder.build()
        }

        fun getRetrofit(): Retrofit {
            return instance.retrofitBuilder.build()
        }

        fun getRxCache(): RxCache {
            return instance.rxCacheBuilder.build()
        }

        /**
         * 对外暴露 OkHttpClient,方便自定义
         */
        fun getOkHttpClientBuilder(): OkHttpClient.Builder {
            return instance.okHttpClientBuilder
        }

        /**
         * 对外暴露 Retrofit,方便自定义
         */
        fun getRetrofitBuilder(): Retrofit.Builder {
            return instance.retrofitBuilder
        }

        /**
         * 对外暴露 RxCache,方便自定义
         */
        fun getRxCacheBuilder(): RxCache.Builder {
            return instance.rxCacheBuilder
        }

        /**
         * 获取全局的cookie实例
         */
        fun getCookieJar(): CookieManger? {
            return instance.cookieJar
        }

        /**
         * 获取缓存的路劲
         */
        fun getCacheDirectory(): File? {
            return instance.cacheDirectory
        }

        /**
         * 获取全局的缓存大小
         */
        fun getCacheMaxSize(): Int {
            return instance.cacheMaxSize
        }

        /**
         * 获取全局的缓存过期时间
         */
        fun getCacheTime(): Long {
            return instance.cacheTime
        }

        /**
         * 获取OkHttp的缓存
         */
        fun getHttpCache(): Cache? {
            return instance.cache
        }

        /**
         * 获取全局的缓存模式
         */
        fun getCacheMode(): CacheMode {
            return instance.cacheMode
        }

        /**
         * 获取全局baseurl
         */
        fun getBaseUrl(): String? {
            return instance.baseUrl1
        }

        /**
         * 超时重试次数
         */
        fun getRetryCount(): Int {
            return instance.retryCount
        }

        /**
         * 超时重试延迟时间
         */
        fun getRetryDelay(): Int {
            return instance.retryDelay
        }

        /**
         * 超时重试延迟叠加时间
         */
        fun getRetryIncreaseDelay(): Int {
            return instance.retryIncreaseDelay
        }
    }

    private object Holder {
        val INSTANCE = FlyHttp()
    }
}