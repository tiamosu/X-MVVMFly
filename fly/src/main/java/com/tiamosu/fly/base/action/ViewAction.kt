package com.tiamosu.fly.base.action

import android.content.Context
import android.os.Bundle
import android.view.View

/**
 * @author tiamosu
 * @date 2021/2/25.
 */
interface ViewAction {

    /**
     * 上下文
     */
    fun getContext(): Context

    /**
     * 用于布局加载，如果[getLayoutId]返回0，则不会生产视图
     */
    fun getLayoutId(): Int

    /**
     * 生成布局视图
     */
    fun setContentView()

    /**
     * 用于初始化参数
     */
    fun initParameters(bundle: Bundle?)

    /**
     * 用于初始化 View
     */
    fun initView(rootView: View?)

    /**
     * 用于绑定事件
     */
    fun initEvent()

    /**
     * 创建观察者
     */
    fun createObserver()

    /**
     * 再次可见时，是否重新请求数据，默认为false则只请求一次数据
     */
    fun isNeedReload() = false

    /**
     * 用于加载数据、处理相关业务逻辑等（Fragment懒加载）
     */
    fun doBusiness()
}