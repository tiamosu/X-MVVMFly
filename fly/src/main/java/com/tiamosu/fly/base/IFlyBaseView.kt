package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
interface IFlyBaseView {

    /**
     * 用于布局加载，如果[getLayoutId]返回0，则不会生产视图
     */
    fun getLayoutId(): Int

    /**
     * 生成布局视图
     */
    fun setContentView()

    /**
     * 可自定义用于初始相关数据
     */
    fun initAny(savedInstanceState: Bundle?)

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
     * 再次可见时，是否重新请求数据，默认为false则只请求一次数据
     */
    fun isNeedReload(): Boolean

    /**
     * 用于加载数据、处理相关业务逻辑等（Fragment懒加载）
     */
    fun doBusiness()

    /**
     * 是否开启网络状态变化监听
     */
    fun isCheckNetChanged(): Boolean

    /**
     * 网络状态变化监听，是否连接可用
     */
    fun onNetworkStateChanged(isConnected: Boolean)

    /**
     * 用于网络连接恢复后加载
     */
    fun onNetReConnect()
}
