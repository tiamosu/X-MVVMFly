package com.tiamosu.fly.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.appcompat.app.AppCompatActivity
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyFragment : FlySupportFragment(), IFlyBaseView {
    private val networkDelegate by lazy { NetworkDelegate() }
    var inflater: LayoutInflater? = null
    var container: ViewGroup? = null
    var rootView: View? = null

    //保证转场动画的流畅性，进行延迟加载
    private var isOnSupportVisible = false
    private var isOnEnterAnimationEnd = false

    //防止多次加载数据
    private var isDataLoaded = false

    override fun getContext(): AppCompatActivity = activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        this.container = container

        if (rootView == null) {
            setContentView()
        } else {
            // 缓存的 rootView 需要判断是否已经被加过 parent，如果有 parent 需要从 parent 删除，
            // 要不然会发生这个 rootView 已经有 parent 的错误。
            var viewParent: ViewParent
            if (rootView!!.parent.also { viewParent = it } is ViewGroup) {
                (viewParent as ViewGroup).removeView(rootView)
            }
        }
        return rootView
    }

    override fun setContentView() {
        if (getLayoutId() > 0) {
            rootView = inflater?.inflate(getLayoutId(), container, false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initAny()
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
    }

    override fun onFlySupportVisible() {
        super.onFlySupportVisible()
        isOnSupportVisible = true
        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
        if (isOnEnterAnimationEnd) {
            tryLoadData()
        }
    }

//    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
//        isOnEnterAnimationEnd = true
//        if (isOnSupportVisible) {
//            tryLoadData()
//        }
//    }

    private fun tryLoadData() {
        if (isNeedReload() || !isDataLoaded) {
            doBusiness()
            isDataLoaded = true
        }
    }
}