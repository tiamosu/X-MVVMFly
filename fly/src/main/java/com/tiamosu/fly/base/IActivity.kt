package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View

/**
 * @author weixia
 * @date 2020/2/18.
 */
interface IActivity {

    /**
     * @return 用于布局加载
     * 如果[getLayoutId]返回0，则不会生产视图
     */
    fun getLayoutId(): Int

    /**
     * 用于初始化数据
     */
    fun initData(bundle: Bundle?)

    /**
     * 用于初始化View
     */
    fun initView(savedInstanceState: Bundle?, contentView: View?)

    /**
     * 用于初始化事件
     */
    fun initEvent()

    /**
     * 用于执行相关业务，加载网络数据等
     */
    fun doBusiness()
}