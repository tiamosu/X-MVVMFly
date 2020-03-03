package com.tiamosu.fly.integration

import androidx.annotation.Nullable
import retrofit2.Retrofit

/**
 * 用来管理网络请求层,以及数据缓存层,以后可能添加数据库请求层
 * @see [RepositoryManager wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki#2.3)
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
interface IRepositoryManager {

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     *
     * @param serviceClass Retrofit service class
     * @param retrofit 传入新的 Retrofit
     * @param useCache 是否缓存 Retrofit service
     * @return Retrofit service
     */
    fun <T> obtainRetrofitService(
        serviceClass: Class<T>,
        retrofit: Retrofit? = null,
        useCache: Boolean = true
    ): T?

    interface ObtainServiceDelegate {
        @Nullable
        fun <T> createRetrofitService(retrofit: Retrofit?, serviceClass: Class<T>?): T?
    }
}
