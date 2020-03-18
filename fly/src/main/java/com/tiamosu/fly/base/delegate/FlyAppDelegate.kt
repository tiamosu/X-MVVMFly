package com.tiamosu.fly.base.delegate

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.ComponentCallbacks2
import android.content.ContentProvider
import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.Fragment
import com.tiamosu.fly.base.IFlyApp
import com.tiamosu.fly.di.component.AppComponent
import com.tiamosu.fly.di.component.DaggerAppComponent
import com.tiamosu.fly.di.module.GlobalConfigModule
import com.tiamosu.fly.integration.ConfigModule
import com.tiamosu.fly.integration.ManifestParser
import com.tiamosu.fly.integration.cache.IntelligentCache
import com.tiamosu.fly.utils.checkNotNull
import java.util.*

/**
 * [FlyAppDelegate] 可以代理 [Application] 的生命周期, 在对应的生命周期, 执行对应的逻辑, 因为 Java 只能单继承,
 * 所以当遇到某些三方库需要继承于它的 [Application] 的时候, 就只有自定义 [Application] 并继承于三方库的 [Application],
 * 这时就不用再继承 [com.tiamosu.fly.base.BaseFlyApplication], 只用在自定义 [Application] 中对应的生命周期调用 [FlyAppDelegate] 的对应方法
 * ([Application] 一定要实现 [IFlyApp] 接口), 框架就能照常运行, 并且 [Application] 中对应的生命周期可使用以下方式扩展
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
class FlyAppDelegate(context: Context) : IFlyApp, IFlyAppLifecycles {
    private var mApplication: Application? = null
    private var mAppComponent: AppComponent? = null

    private var mConfigModules: List<ConfigModule>? = null
    private var mAppLifecycles: MutableList<IFlyAppLifecycles>? = ArrayList()
    private var mComponentCallback: ComponentCallbacks2? = null

    init {
        //用反射, 将 AndroidManifest.xml 中带有 ConfigModule 标签的 class 转成对象集合（List<ConfigModule>）
        this.mConfigModules = ManifestParser(context).parse()

        //遍历之前获得的集合, 执行每一个 ConfigModule 实现类的某些方法
        for (module in mConfigModules!!) {
            //将框架外部, 开发者实现的 Application 的生命周期回调 (AppLifecycles) 存入 mAppLifecycles 集合 (此时还未注册回调)
            module.injectAppLifecycle(context, mAppLifecycles!!)
        }
    }

    override fun attachBaseContext(context: Context) {
        //遍历 mAppLifecycles, 执行所有已注册的 AppLifecycles 的 attachBaseContext() 方法 (框架外部, 开发者扩展的逻辑)
        if (mAppLifecycles?.isNotEmpty() == true) {
            for (lifecycle in mAppLifecycles!!) {
                lifecycle.attachBaseContext(context)
            }
        }
    }

    override fun onCreate(application: Application) {
        this.mApplication = application
        mAppComponent = DaggerAppComponent
            .builder()
            .application(application)//提供application
            .globalConfigModule(getGlobalConfigModule(application, mConfigModules))//全局配置
            .build()
        mAppComponent!!.inject(this)

        //将 ConfigModule 的实现类的集合存放到缓存 Cache, 可以随时获取
        //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
        //否则存储在 LRU 算法的存储空间中 (大于或等于缓存所能允许的最大 size, 则会根据 LRU 算法清除之前的条目)
        //前提是 extras 使用的是 IntelligentCache (框架默认使用)
        mConfigModules?.apply {
            mAppComponent!!.extras()
                .put(IntelligentCache.getKeyOfKeep(ConfigModule::class.java.name), this)
        }
        this.mConfigModules = null

        mComponentCallback = AppComponentCallbacks(application, mAppComponent!!)
        //注册回掉: 内存紧张时释放部分内存
        application.registerComponentCallbacks(mComponentCallback)

        //执行框架外部, 开发者扩展的 App onCreate 逻辑
        if (mAppLifecycles?.isNotEmpty() == true) {
            for (lifecycle in mAppLifecycles!!) {
                lifecycle.onCreate(application)
            }
        }
    }

    override fun onTerminate(application: Application) {
        if (mComponentCallback != null) {
            mApplication?.unregisterComponentCallbacks(mComponentCallback)
        }
        if (mAppLifecycles?.isNotEmpty() == true && mApplication != null) {
            for (lifecycle in mAppLifecycles!!) {
                lifecycle.onTerminate(mApplication!!)
            }
        }
        this.mApplication = null
        this.mAppComponent = null
        this.mAppLifecycles = null
        this.mComponentCallback = null
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        if (mAppLifecycles?.isNotEmpty() == true) {
            for (lifecycle in mAppLifecycles!!) {
                lifecycle.onConfigurationChanged(configuration)
            }
        }
    }

    override fun onLowMemory() {
        if (mAppLifecycles?.isNotEmpty() == true) {
            for (lifecycle in mAppLifecycles!!) {
                lifecycle.onLowMemory()
            }
        }
    }

    override fun onTrimMemory(level: Int) {
        if (mAppLifecycles?.isNotEmpty() == true) {
            for (lifecycle in mAppLifecycles!!) {
                lifecycle.onTrimMemory(level)
            }
        }
    }

    /**
     * 将app的全局配置信息封装进module(使用Dagger注入到需要配置信息的地方)
     * 需要在AndroidManifest中声明 [ConfigModule] 的实现类,和Glide的配置方式相似
     *
     * @return [GlobalConfigModule]
     */
    private fun getGlobalConfigModule(
        context: Context,
        modules: List<ConfigModule>?
    ): GlobalConfigModule {
        val builder = GlobalConfigModule.builder()

        //遍历 ConfigModule 集合, 给全局配置 GlobalConfigModule 添加参数
        modules?.apply {
            for (module in modules) {
                module.applyOptions(context, builder)
            }
        }
        return builder.build()
    }

    /**
     * 将 [AppComponent] 返回出去, 供其它地方使用, [AppComponent] 接口中声明的方法返回的实例,
     * 在 [getAppComponent] 拿到对象后都可以直接使用
     *
     * @return AppComponent
     * @see com.tiamosu.fly.utils.getAppComponent
     */
    override fun getAppComponent(): AppComponent {
        checkNotNull(
            mAppComponent,
            "%s == null, first call %s#onCreate(Application) in %s#onCreate()",
            AppComponent::class.java.name, javaClass.name, if (mApplication == null)
                Application::class.java.name
            else
                mApplication!!.javaClass.name
        )
        return mAppComponent!!
    }

    /**
     * [ComponentCallbacks2] 是一个细粒度的内存回收管理回调
     * [Application]、[Activity]、[Service]、[ContentProvider]、[Fragment] 实现了 [ComponentCallbacks2] 接口
     * 开发者应该实现 [ComponentCallbacks2.onTrimMemory] 方法, 细粒度 release 内存, 参数的值不同可以体现出不同程度的内存可用情况
     * 响应 [ComponentCallbacks2.onTrimMemory] 回调, 开发者的 App 会存活的更持久, 有利于用户体验
     * 不响应 [ComponentCallbacks2.onTrimMemory] 回调, 系统 kill 掉进程的几率更大
     */
    @Suppress("unused")
    private class AppComponentCallbacks(
        private val mApplication: Application,
        private val mAppComponent: AppComponent
    ) : ComponentCallbacks2 {

        /**
         * 在你的 App 生命周期的任何阶段, [ComponentCallbacks2.onTrimMemory] 发生的回调都预示着你设备的内存资源已经开始紧张
         * 你应该根据 [ComponentCallbacks2.onTrimMemory] 发生回调时的内存级别来进一步决定释放哪些资源
         * [ComponentCallbacks2.onTrimMemory] 的回调可以发生在 [Application]、[Activity]、[Service]、[ContentProvider]、[Fragment]
         *
         * @param level 内存级别
         */
        override fun onTrimMemory(level: Int) {
        }

        override fun onConfigurationChanged(newConfig: Configuration) {}

        /**
         * 当系统开始清除 LRU 列表中的进程时, 尽管它会首先按照 LRU 的顺序来清除, 但是它同样会考虑进程的内存使用量, 因此消耗越少的进程则越容易被留下来
         * [ComponentCallbacks2.onTrimMemory] 的回调是在 API 14 才被加进来的, 对于老的版本, 你可以使用 [ComponentCallbacks2.onLowMemory] 方法来进行兼容
         * [ComponentCallbacks2.onLowMemory] 相当于 `onTrimMemory(TRIM_MEMORY_COMPLETE)`
         *
         * @see ComponentCallbacks2.TRIM_MEMORY_COMPLETE
         */
        override fun onLowMemory() {
            //系统正运行于低内存的状态并且你的进程正处于 LRU 列表中最容易被杀掉的位置, 你应该释放任何不影响你的 App 恢复状态的资源
        }
    }
}
