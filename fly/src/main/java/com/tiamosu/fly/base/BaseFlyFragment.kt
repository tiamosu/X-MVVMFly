package com.tiamosu.fly.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.annotation.CallSuper
import com.tiamosu.fly.http.manager.NetworkDelegate
import me.yokeyword.fragmentation.SupportFragment

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFlyFragment : SupportFragment(), IFlyBaseView {
    var inflater: LayoutInflater? = null
    var container: ViewGroup? = null
    var rootView: View? = null

    private val networkDelegate by lazy { NetworkDelegate() }

    //保证转场动画的流畅性
    private var isOnLazyInitView = false
    private var isOnEnterAnimationEnd = false

    /**
     * 若当前 Fragment 是 ChildFragment， 且想以父页面（ParentFragment）开启新页面时，
     * 可使用 [getParentDelegate] .startX()、popX()、find/getX() 等相关 api
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : SupportFragment> getParentDelegate(): T {
        return (parentFragment ?: this) as T
    }

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
        initAny(savedInstanceState)
    }

    @CallSuper
    override fun initAny(savedInstanceState: Bundle?) {
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)

        initData(arguments)
        initView(savedInstanceState, rootView)
        initEvent()
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {}
    override fun onNetReConnect() {}

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        isOnEnterAnimationEnd = true
        if (isOnLazyInitView) {
            doBusiness()
        }
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        isOnLazyInitView = true
        if (isOnEnterAnimationEnd) {
            doBusiness()
        }
    }
}