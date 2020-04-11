package com.tiamosu.fly.module.common.config

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import com.kingja.loadsir.callback.ProgressCallback
import com.kingja.loadsir.core.LoadSir
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.http.FlyHttp
import com.tiamosu.fly.http.https.HttpsUtils
import com.tiamosu.fly.http.model.HttpHeaders
import com.tiamosu.fly.imageloader.glide.GlideFly
import com.tiamosu.fly.module.common.BuildConfig
import com.tiamosu.fly.module.common.integration.loadsir.CustomCallback
import com.tiamosu.fly.module.common.integration.loadsir.EmptyCallback
import com.tiamosu.fly.module.common.integration.loadsir.ErrorCallback
import com.tiamosu.fly.utils.getAppComponent
import me.yokeyword.fragmentation.Fragmentation
import me.yokeyword.fragmentation.helper.ExceptionHandler
import okhttp3.ConnectionPool
import java.net.Proxy

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class AppLifecyclesImpl : IFlyAppLifecycles {

    override fun attachBaseContext(context: Context) {
        MultiDex.install(context)
    }

    override fun onCreate(application: Application) {
        if (ProcessUtils.isMainProcess()) {
            initFragmentation()
            initARouter(application)
            initFlyHttp()
            initLoadSir()
        }
    }

    private fun initFragmentation() {
        Fragmentation.builder()
            //设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
            .stackViewMode(Fragmentation.BUBBLE)
            .debug(BuildConfig.DEBUG)
            .handleException(object : ExceptionHandler {
                override fun onException(e: Exception) {}
            })
            .install()
    }

    private fun initARouter(application: Application) {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application)
    }

    private fun initFlyHttp() {
        val httpHeaders = HttpHeaders()
        httpHeaders.put(HttpHeaders.HEAD_KEY_USER_AGENT, HttpHeaders.userAgent)
        FlyHttp.instance
            .debug("FlyHttp", BuildConfig.DEBUG)
            .setBaseUrl(Api.APP_DOMAIN)
            .setReadTimeOut(60 * 1000)
            .setWriteTimeOut(60 * 1000)
            .setConnectTimeout(60 * 1000)
            .setOkHttpProxy(Proxy.NO_PROXY)
            .setHostnameVerifier(HttpsUtils.DefaultHostnameVerifier())
            .setCertificates()
            .addInterceptor(CustomSignInterceptor())
            .setOkHttpConnectionPool(ConnectionPool())
            .setCallbackExecutor(getAppComponent().executorService())
            .addCommonHeaders(httpHeaders)
    }

    private fun initLoadSir() {
        val progressCallback = ProgressCallback.Builder()
            .setTitle("Loading")
            .build()
        LoadSir.beginBuilder()
            .addCallback(EmptyCallback())
            .addCallback(ErrorCallback())
            .addCallback(CustomCallback())
            .addCallback(progressCallback)
            .setDefaultCallback(ProgressCallback::class.java)
            .commit()
    }

    override fun onTerminate(application: Application) {
        ARouter.getInstance().destroy()
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