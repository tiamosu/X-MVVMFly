package com.tiamosu.fly.di.module

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tiamosu.fly.integration.IRepositoryManager
import com.tiamosu.fly.integration.RepositoryManager
import com.tiamosu.fly.integration.cache.Cache
import com.tiamosu.fly.integration.cache.CacheType
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * 提供一些框架必须的实例的 [Module]
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
@Suppress("unused")
@Module
abstract class AppModule {

    @Binds
    abstract fun bindRepositoryManager(repositoryManager: RepositoryManager): IRepositoryManager

    interface GsonConfiguration {
        fun configGson(context: Context, builder: GsonBuilder)
    }

    @Module
    companion object {

        @JvmStatic
        @Singleton
        @Provides
        fun provideGson(application: Application, configuration: GsonConfiguration?): Gson {
            val builder = GsonBuilder()
            configuration?.configGson(application, builder)
            return builder.create()
        }

        @JvmStatic
        @Singleton
        @Provides
        fun provideExtras(cacheFactory: Cache.Factory<String, Any?>): Cache<String, Any?> {
            return cacheFactory.build(CacheType.EXTRAS)
        }
    }
}
