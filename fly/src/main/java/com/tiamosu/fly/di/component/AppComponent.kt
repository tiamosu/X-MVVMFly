package com.tiamosu.fly.di.component

import android.app.Application
import com.google.gson.Gson
import com.tiamosu.fly.base.delegate.FlyAppDelegate
import com.tiamosu.fly.di.module.AppModule
import com.tiamosu.fly.di.module.ClientModule
import com.tiamosu.fly.di.module.GlobalConfigModule
import com.tiamosu.fly.integration.IRepositoryManager
import com.tiamosu.fly.integration.cache.Cache
import com.tiamosu.fly.http.imageloader.ImageLoader
import dagger.BindsInstance
import dagger.Component
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.ExecutorService
import javax.inject.Singleton

/**
 * 可通过 [com.tiamosu.fly.utils.FlyUtils.getAppComponent] 拿到此接口的实现类
 * 拥有此接口的实现类即可调用对应的方法拿到 Dagger 提供的对应实例
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
@Singleton
@Component(modules = [AppModule::class, ClientModule::class, GlobalConfigModule::class])
interface AppComponent {

    /**
     * 用于管理网络请求层, 以及数据缓存层
     *
     * @return [IRepositoryManager]
     */
    fun repositoryManager(): IRepositoryManager

    /**
     * RxJava 错误处理管理类
     *
     * @return [RxErrorHandler]
     */
    fun rxErrorHandler(): RxErrorHandler

    /**
     * 图片加载管理器, 用于加载图片的管理类, 使用策略者模式, 可在运行时动态替换任何图片加载框架
     * 需要在 [com.tiamosu.fly.integration.ConfigModule.applyOptions] 中
     * 手动注册 [com.tiamosu.fly.http.imageloader.BaseImageLoaderStrategy], [ImageLoader] 才能正常使用
     */
    fun imageLoader(): ImageLoader

    /**
     * [OkHttpClient.Builder] 自定义扩展
     */
    fun okHttpBuilder(): OkHttpClient.Builder

    /**
     * 网络请求框架
     *
     * @return [OkHttpClient]
     */
    fun okHttpClient(): OkHttpClient

    fun retrofit(): Retrofit

    /**
     * Json 序列化库
     *
     * @return [Gson]
     */
    fun gson(): Gson

    /**
     * 缓存文件根目录 (Glide 的缓存都已经作为子文件夹放在这个根目录下), 应该将所有缓存都统一放到这个根目录下
     * 便于管理和清理, 可在 [com.tiamosu.fly.integration.ConfigModule.applyOptions] 中配置
     *
     * @return [File]
     */
    fun cacheFile(): File

    /**
     * 用来存取一些整个 App 公用的数据, 切勿大量存放大容量数据, 这里的存放的数据和 [Application] 的生命周期一致
     *
     * @return [Cache]
     */
    fun extras(): Cache<String, Any?>

    /**
     * 用于创建框架所需缓存对象的工厂
     *
     * @return [Cache.Factory]
     */
    fun cacheFactory(): Cache.Factory<String, Any?>

    /**
     * 返回一个全局公用的线程池,适用于大多数异步需求。
     * 避免多个线程池创建带来的资源消耗。
     *
     * @return [ExecutorService]
     */
    fun executorService(): ExecutorService

    fun inject(delegate: FlyAppDelegate)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun globalConfigModule(globalConfigModule: GlobalConfigModule): Builder

        fun build(): AppComponent
    }
}
