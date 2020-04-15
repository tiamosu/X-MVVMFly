package com.tiamosu.fly.integration

import com.tiamosu.fly.integration.cache.Cache
import com.tiamosu.fly.integration.cache.CacheType
import dagger.Lazy
import retrofit2.Retrofit
import java.lang.reflect.Proxy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用来管理网络请求层
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
@Singleton
class RepositoryManager @Inject
constructor() : IRepositoryManager {

    @JvmField
    @Inject
    internal var retrofit: Lazy<Retrofit>? = null

    @JvmField
    @Inject
    internal var cacheFactory: Cache.Factory<String, Any?>? = null

    @JvmField
    @Inject
    internal var obtainServiceDelegate: IRepositoryManager.ObtainServiceDelegate? = null

    private val retrofitServiceCache: Cache<String, Any?>? by lazy {
        cacheFactory?.build(CacheType.RETROFIT_SERVICE_CACHE)
    }

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     *
     * @param serviceClass ApiService class
     * @param <T>          ApiService class
     * @return ApiService
     */
    @Suppress("UNCHECKED_CAST")
    @Synchronized
    override fun <T> obtainRetrofitService(serviceClass: Class<T>, retrofit: Retrofit?): T? {
        val oldRetrofit = this.retrofit?.get()
        val newRetrofit = retrofit ?: oldRetrofit
        val canonicalName = serviceClass.canonicalName ?: ""
        var retrofitService: T? = null

        if (newRetrofit == oldRetrofit) {
            retrofitService = retrofitServiceCache?.get(canonicalName) as? T
        }
        if (retrofitService == null) {
            retrofitService =
                obtainServiceDelegate?.createRetrofitService(newRetrofit, serviceClass)
            if (retrofitService == null) {
                retrofitService = Proxy.newProxyInstance(
                    serviceClass.classLoader, arrayOf(serviceClass),
                    RetrofitServiceProxyHandler(newRetrofit, serviceClass)
                ) as? T
            }
            if (newRetrofit == oldRetrofit) {
                retrofitServiceCache?.put(canonicalName, retrofitService)
            }
        }
        return retrofitService
    }
}
