package com.tiamosu.fly.http

import android.text.TextUtils
import com.tiamosu.fly.http.cookie.CookieManger
import com.tiamosu.fly.http.https.HttpsUtils
import com.tiamosu.fly.http.interceptors.HttpLoggingInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.http.model.HttpParams
import com.tiamosu.fly.http.request.*
import com.tiamosu.fly.http.utils.FlyHttpLog
import com.tiamosu.fly.utils.FlyUtils
import okhttp3.Call
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.InputStream
import java.net.Proxy
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

/**
 * 描述：网络请求入口类
 * 主要功能：
 * 1.全局设置超时时间
 * 2.支持请求错误重试相关参数，包括重试次数、重试延时时间
 * 3.支持支持get、post、put、delete请求
 * 4.支持支持自定义请求
 * 5.支持文件上传、下载
 * 6.支持全局公共请求头
 * 7.支持全局公共参数
 * 8.支持okhttp相关参数，包括拦截器
 * 9.支持Retrofit相关参数
 * 10.支持Cookie管理
 *
 * @author tiamosu
 * @date 2020/2/26.
 */
class FlyHttp {
    private var cookieJar: CookieManger? = null                     //Cookie管理
    private var baseUrl: String? = null                             //全局 BaseUrl
    private var retryCount = DEFAULT_RETRY_COUNT                    //超时重试次数，默认3次
    private var retryDelay = DEFAULT_RETRY_DELAY                    //超时重试延时，单位 s
    private var commonHeaders: HttpHeaders? = null                  //全局公共请求头
    private var commonParams: HttpParams? = null                    //全局公共请求参数
    private var okHttpClientBuilder: OkHttpClient.Builder           //OkHttpClient请求的Builder
    private var retrofitBuilder: Retrofit.Builder                   //Retrofit请求Builder

    init {
        okHttpClientBuilder = FlyUtils.getAppComponent().okHttpClient().newBuilder()
            .apply {
                val sslParams: HttpsUtils.SSLParams = HttpsUtils.getSslSocketFactory()
                sslSocketFactory(sslParams.sslSocketFactory, sslParams.trustManager)
                hostnameVerifier(HttpsUtils.DefaultHostnameVerifier())
            }

        retrofitBuilder = FlyUtils.getAppComponent().retrofit().newBuilder()
    }

    /**
     * 调试模式,默认打开所有的异常调试
     */
    fun debug(tag: String?): FlyHttp {
        debug(tag, true)
        return this
    }

    /**
     * 调试模式,第二个参数表示所有catch住的log是否需要打印
     * 一般来说,这些异常是由于不标准的数据格式,或者特殊需要主动产生的,
     * 并不是框架错误,如果不想每次打印,这里可以关闭异常显示
     */
    fun debug(tag: String?, isPrintException: Boolean): FlyHttp {
        val tempTag = if (TextUtils.isEmpty(tag)) "FlyHttp" else tag!!
        if (isPrintException) {
            val loggingInterceptor = HttpLoggingInterceptor(tempTag)
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        FlyHttpLog.debug(tempTag, isPrintException)
        return this
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
     * 全局设置baseurl
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

    companion object {
        const val DEFAULT_RETRY_COUNT = 3 //默认重试次数
        const val DEFAULT_RETRY_DELAY = 2 //默认重试延时

        val instance = Holder.INSTANCE

        internal fun getOkHttpClient(): OkHttpClient {
            return instance.okHttpClientBuilder.build()
        }

        internal fun getRetrofit(): Retrofit {
            return instance.retrofitBuilder.build()
        }

        /**
         * 获取全局的cookie实例
         */
        internal fun getCookieJar(): CookieManger? {
            return instance.cookieJar
        }

        /**
         * 获取全局baseurl
         */
        internal fun getBaseUrl(): String? {
            var baseUrl = instance.baseUrl
            if (TextUtils.isEmpty(baseUrl)) {
                val httpUrl = getRetrofit().baseUrl()
                baseUrl = httpUrl.url().toString()
            }
            return baseUrl
        }

        /**
         * 超时重试次数
         */
        internal fun getRetryCount(): Int {
            return instance.retryCount
        }

        /**
         * 超时重试延迟时间
         */
        internal fun getRetryDelay(): Int {
            return instance.retryDelay
        }

        /**
         * get请求
         */
        operator fun <T> get(url: String): GetRequest<T> {
            return GetRequest(url)
        }

        /**
         * post请求
         */
        fun <T> post(url: String): PostRequest<T> {
            return PostRequest(url)
        }

        /**
         * put请求
         */
        fun <T> put(url: String): PutRequest<T> {
            return PutRequest(url)
        }

        /**
         * delete请求
         */
        fun <T> delete(url: String): DeleteRequest<T> {
            return DeleteRequest(url)
        }

        /**
         * 文件下载
         */
        fun <T> download(url: String): DownloadRequest<T> {
            return DownloadRequest(url)
        }

        /**
         * 自定求请求
         */
        fun <T> custom(url: String): CustomRequest<T> {
            return CustomRequest(url)
        }
    }

    private object Holder {
        val INSTANCE = FlyHttp()
    }
}