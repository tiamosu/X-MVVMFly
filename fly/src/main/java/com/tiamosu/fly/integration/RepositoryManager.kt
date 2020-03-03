package com.tiamosu.fly.integration

import com.tiamosu.fly.integration.cache.Cache
import com.tiamosu.fly.integration.cache.CacheType
import dagger.Lazy
import retrofit2.Retrofit
import java.lang.reflect.Proxy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用来管理网络请求层,以及数据缓存层,以后可能添加数据库请求层
 * @see [RepositoryManager wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki#2.3)
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
@Singleton
class RepositoryManager @Inject
constructor() : IRepositoryManager {

    @JvmField
    @Inject
    internal var lazyRetrofit: Lazy<Retrofit>? = null
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
    override fun <T> obtainRetrofitService(
        serviceClass: Class<T>,
        retrofit: Retrofit?,
        useCache: Boolean
    ): T? {
        val newRetrofit = retrofit ?: lazyRetrofit?.get()
        val canonicalName = serviceClass.canonicalName ?: ""
        var retrofitService: T? = null
        if (useCache) {
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
            if (useCache) {
                retrofitServiceCache?.put(canonicalName, retrofitService)
            }
        }
        return retrofitService
    }
}
