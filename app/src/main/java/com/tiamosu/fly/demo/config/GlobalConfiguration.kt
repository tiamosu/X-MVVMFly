package com.tiamosu.fly.demo.config

import android.content.Context
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.di.module.ClientModule
import com.tiamosu.fly.di.module.GlobalConfigModule
import com.tiamosu.fly.http.interceptors.HeadersInterceptor
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.integration.ConfigModule
import okhttp3.OkHttpClient

/**
 * @author tiamosu
 * @date 2020/2/27.
 */
@Suppress("unused")
class GlobalConfiguration : ConfigModule {

    private val headers by lazy {
        HttpHeaders().apply {
            put(HttpHeaders.HEAD_KEY_ACCEPT, "*/*")
        }
    }

    override fun applyOptions(context: Context, builder: GlobalConfigModule.Builder) {
        builder
            .baseurl("https://www.wanandroid.com")
            .addInterceptor(HeadersInterceptor(headers))
            .okhttpConfiguration(object : ClientModule.OkHttpConfiguration {
                override fun configOkHttp(context: Context, okHttpBuilder: OkHttpClient.Builder) {
                }
            })
    }

    override fun injectAppLifecycle(context: Context, lifecycles: MutableList<IFlyAppLifecycles>) {
    }
}