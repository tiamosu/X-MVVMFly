package com.tiamosu.fly.core.config

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import com.kingja.loadsir.core.LoadSir
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.core.ui.loadsir.CustomCallback
import com.tiamosu.fly.core.ui.loadsir.EmptyCallback
import com.tiamosu.fly.core.ui.loadsir.ErrorCallback
import com.tiamosu.fly.core.ui.loadsir.LoadingCallback
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.https.HttpsUtils
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.imageloader.glide.GlideFly
import com.tiamosu.fly.utils.getAppComponent
import okhttp3.ConnectionPool
import okhttp3.logging.HttpLoggingInterceptor
import java.net.Proxy

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class AppLifecyclesImpl : IFlyAppLifecycles {

    override fun attachBaseContext(context: Context) {
    }

    override fun onCreate(application: Application) {
        if (ProcessUtils.isMainProcess()) {
            initFlyHttp()
            initLoadSir()
        }
    }

    private fun initFlyHttp() {
        val httpHeaders = HttpHeaders()
        httpHeaders.put(HttpHeaders.HEAD_KEY_USER_AGENT, HttpHeaders.userAgent)
        FlyHttp.instance
            .setBaseUrl(Api.APP_DOMAIN)
            .setReadTimeOut(60 * 1000)
            .setWriteTimeOut(60 * 1000)
            .setConnectTimeout(60 * 1000)
            .setOkHttpProxy(Proxy.NO_PROXY)
            .setHostnameVerifier(HttpsUtils.DefaultHostnameVerifier())
            .setCertificates()
            .setOkHttpConnectionPool(ConnectionPool())
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .setCallbackExecutor(getAppComponent().executorService())
            .addCommonHeaders(httpHeaders)
    }

    private fun initLoadSir() {
        LoadSir.beginBuilder()
            .addCallback(EmptyCallback())
            .addCallback(ErrorCallback())
            .addCallback(CustomCallback())
            .addCallback(LoadingCallback())
            .setDefaultCallback(LoadingCallback::class.java)
            .commit()
    }

    override fun onTerminate(application: Application) {
    }

    override fun onConfigurationChanged(configuration: Configuration) {
    }

    override fun onLowMemory() {
        GlideFly.get(Utils.getApp()).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            GlideFly.get(Utils.getApp()).clearMemory()
        }
        GlideFly.get(Utils.getApp()).trimMemory(level)
    }
}