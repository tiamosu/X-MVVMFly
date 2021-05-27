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
     * 用于加载数据、处理相关业务逻辑等（Fragment懒加载）
     */
    fun doBusiness()
}