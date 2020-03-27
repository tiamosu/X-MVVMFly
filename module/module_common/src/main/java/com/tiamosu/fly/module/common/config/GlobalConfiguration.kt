package com.tiamosu.fly.module.common.config

import android.content.Context
import com.google.gson.GsonBuilder
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.di.module.AppModule
import com.tiamosu.fly.di.module.ClientModule
import com.tiamosu.fly.di.module.GlobalConfigModule
import com.tiamosu.fly.http.interceptors.HeadersInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.imageloader.glide.GlideImageLoaderStrategy
import com.tiamosu.fly.integration.ConfigModule
import com.tiamosu.fly.utils.RxJavaErrorHandlerCallback
import com.tiamosu.fly.utils.setRxJavaErrorHandler
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
@Suppress("unused")
class GlobalConfiguration : ConfigModule {

    private val headers by lazy {
        HttpHeaders().apply {
            put(HttpHeaders.HEAD_KEY_ACCEPT, "*/*")
        }
    }

    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
        //RxJava2 取消订阅后，抛出的异常无法捕获，将导致程序崩溃
        setRxJavaErrorHandler(object : RxJavaErrorHandlerCallback {
            override fun onCallback(throwable: Throwable?): Boolean {
                //自行拦截处理，默认返回 false； 若返回 true，则不继续往下执行
                return false
            }
        })

        builder
            .baseurl(Api.APP_DOMAIN)
            .addInterceptor(HeadersInterceptor(headers))
            .imageLoaderStrategy(GlideImageLoaderStrategy())
            .okhttpConfiguration(object : ClientModule.OkHttpConfiguration {
                override fun configOkHttp(context: Context, okHttpBuilder: OkHttpClient.Builder) {
                }
            })
            .retrofitConfiguration(object : ClientModule.RetrofitConfiguration {
                override fun configRetrofit(context: Context, retrofitBuilder: Retrofit.Builder) {
                }
            })
            .gsonConfiguration(object : AppModule.GsonConfiguration {
                override fun configGson(context: Context, builder: GsonBuilder) {
                }
            })
            .responseErrorListener(ResponseErrorListenerImpl())
    }

    override fun injectAppLifecycle(context: Context, lifecycles: MutableList<IFlyAppLifecycles>) {
        lifecycles.add(AppLifecyclesImpl())
    }
}