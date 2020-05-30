package com.tiamosu.fly.core.config

import android.content.Context
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
        setRxJavaErrorHandler(RxJavaErrorHandlerCallback { //自行拦截处理，默认返回 false； 若返回 true，则不继续往下执行
            false
        })

        builder
            .baseurl(Api.APP_DOMAIN)
            .addInterceptor(HeadersInterceptor(headers))
            .imageLoaderStrategy(GlideImageLoaderStrategy())
            .okhttpConfiguration(ClientModule.OkHttpConfiguration { _, _ -> })
            .retrofitConfiguration(ClientModule.RetrofitConfiguration { _, _ -> })
            .gsonConfiguration(AppModule.GsonConfiguration { _, _ -> })
            .responseErrorListener(ResponseErrorListenerImpl())
    }

    override fun injectAppLifecycle(context: Context, lifecycles: MutableList<IFlyAppLifecycles>) {
        lifecycles.add(AppLifecyclesImpl())
    }
}