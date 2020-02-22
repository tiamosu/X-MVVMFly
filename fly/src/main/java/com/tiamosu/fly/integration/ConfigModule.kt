package com.tiamosu.fly.integration

import android.app.Application
import android.content.Context
import com.tiamosu.fly.base.delegate.IFlyAppLifecycles
import com.tiamosu.fly.di.module.GlobalConfigModule

/**
 * [ConfigModule] 可以给框架配置一些参数,需要实现 [ConfigModule] 后,在 AndroidManifest 中声明该实现类
 * @see [ConfigModule wiki 官方文档](https://github.com/JessYanCoding/MVPArms/wiki#2.1)
 *
 * @author tiamosu
 * @date 2018/9/14.
 */
interface ConfigModule {

    /**
     * 使用 [GlobalConfigModule.Builder] 给框架配置一些配置参数
     *
     * @param context [Context]
     * @param builder [GlobalConfigModule.Builder]
     */
    fun applyOptions(context: Context, builder: GlobalConfigModule.Builder)

    /**
     * 使用 [IFlyAppLifecycles] 在 [Application] 的生命周期中注入一些操作
     *
     * @param context    [Context]
     * @param lifecycles [Application] 的生命周期容器, 可向框架中添加多个 [Application] 的生命周期类
     */
    fun injectAppLifecycle(context: Context, lifecycles: MutableList<IFlyAppLifecycles>)
}
