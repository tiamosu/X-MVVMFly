package com.tiamosu.fly.di.module

import android.app.Application
import android.content.Context
import com.tiamosu.fly.http.GlobalHttpHandler
import com.tiamosu.fly.utils.checkNotNull
import dagger.Module
import dagger.Provides
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 提供一些三方库客户端实例的 [Module]
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
@Suppress("unused")
@Module
abstract class ClientModule {

    /**
     * [Retrofit] 自定义配置接口
     */
    interface RetrofitConfiguration {
        fun configRetrofit(context: Context, retrofitBuilder: Retrofit.Builder)
    }

    /**
     * [OkHttpClient] 自定义配置接口
     */
    interface OkHttpConfiguration {
        fun configOkHttp(context: Context, okHttpBuilder: OkHttpClient.Builder)
    }

    @Module
    companion object {
        private const val TIME_OUT = 60_000L

        /**
         * 提供 [Retrofit]
         *
         * @param application   [Application]
         * @param configuration [RetrofitConfiguration]
         * @param builder       [Retrofit.Builder]
         * @param client        [OkHttpClient]
         * @param httpUrl       [HttpUrl]
         *
         * @return [Retrofit]
         */
        @JvmStatic
        @Singleton
        @Provides
        fun provideRetrofit(
            application: Application,
            configuration: RetrofitConfiguration?,
            builder: Retrofit.Builder,
            client: OkHttpClient,
            httpUrl: HttpUrl?
        ): Retrofit {

            checkNotNull(httpUrl, "baseUrl == null")

            builder
                .baseUrl(httpUrl!!)//域名
                .client(client)//设置 OkHttp
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用 RxJava

            configuration?.configRetrofit(application, builder)
            return builder.build()
        }

        /**
         * 提供 [OkHttpClient]
         *
         * @param application     [Application]
         * @param configuration   [OkHttpConfiguration]
         * @param builder         [OkHttpClient.Builder]
         * @param interceptors    [MutableList<Interceptor>]
         * @param handler         [GlobalHttpHandler]
         * @param executorService [ExecutorService]
         * @return [OkHttpClient]
         */
        @JvmStatic
        @Singleton
        @Provides
        fun provideClient(
            application: Application,
            configuration: OkHttpConfiguration?,
            builder: OkHttpClient.Builder,
            interceptors: MutableList<Interceptor>?,
            handler: GlobalHttpHandler?,
            executorService: ExecutorService
        ): OkHttpClient {
            builder
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)

            if (handler != null) {
                builder.addInterceptor { chain ->
                    chain.proceed(handler.onHttpRequestBefore(chain, chain.request()))
                }
            }

            //如果外部提供了interceptor的集合则遍历添加
            if (interceptors?.isNotEmpty() == true) {
                for (interceptor in interceptors) {
                    builder.addInterceptor(interceptor)
                }
            }

            //为 OkHttp 设置默认的线程池
            builder.dispatcher(Dispatcher(executorService))

            configuration?.configOkHttp(application, builder)
            return builder.build()
        }

        @JvmStatic
        @Singleton
        @Provides
        fun provideRetrofitBuilder(): Retrofit.Builder {
            return Retrofit.Builder()
        }

        @JvmStatic
        @Singleton
        @Provides
        fun provideClientBuilder(): OkHttpClient.Builder {
            return OkHttpClient.Builder()
        }

        /**
         * 提供处理 RxJava 错误的管理器
         *
         * @param application [Application]
         * @param listener    [ResponseErrorListener]
         * @return [RxErrorHandler]
         */
        @JvmStatic
        @Singleton
        @Provides
        fun proRxErrorHandler(
            application: Application,
            listener: ResponseErrorListener
        ): RxErrorHandler {
            return RxErrorHandler
                .builder()
                .with(application)
                .responseErrorListener(listener)
                .build()
        }
    }
}
