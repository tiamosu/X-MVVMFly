package com.tiamosu.fly.base

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Lifecycle
import com.tiamosu.databinding.page.FlyDataBindingFragment
import com.tiamosu.fly.base.action.*
import com.tiamosu.fly.base.dialog.loading.LoadingConfig
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onAttach] → [onCreate] → [initParameters] → [onCreateView]
 * → [onViewCreated] → [initView] → [createObserver] → [onActivityCreated] → [onResume]
 * → [onFlySupportVisible] → [onFlyLazyInitView] → [initEvent] → [doBusiness]
 * → [onPause] → [onFlySupportInvisible] → [onDestroyView] → [onDestroy] → [onDetach]
 *
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyFragment : FlyDataBindingFragment(),
    FragmentAction, BundleAction, HandlerAction, KeyboardAction, NetAction {

    private val networkDelegate by lazy { NetworkDelegate() }

    final override val bundle
        get() = arguments

    final override fun getContext() = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
        initParameters(bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(rootView)
        createObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallbacks()
    }

    override fun onFlyLazyInitView() {
        super.onFlyLazyInitView()
        initEvent()
        doBusiness()
    }

    override fun onFlySupportVisible() {
        super.onFlySupportVisible()
        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
    }

    override val loadingConfig by lazy { LoadingConfig() }

    override fun showLoading(config: LoadingConfig?) {
        val newConfig = config ?: loadingConfig
        (context as? BaseFlyActivity)?.showLoading(newConfig)
    }

    override fun hideLoading() {
        (context as? BaseFlyActivity)?.hideLoading()
    }

    /**
     * Fragment 按键事件派发
     */
    internal fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            //这个子 Fragment 必须是 BaseFlyFragment 的子类，并且处于可见状态
            if (fragment !is BaseFlyFragment ||
                fragment.lifecycle.currentState != Lifecycle.State.RESUMED
            ) {
                continue
            }
            //将按键事件派发给子 Fragment 进行处理
            if (fragment.dispatchKeyEvent(event)) {
                //如果子 Fragment 拦截了这个事件，那么就不交给父 Fragment 处理
                return true
            }
        }
        return when (event?.action) {
            KeyEvent.ACTION_DOWN -> onKeyDown(event.keyCode, event)
            KeyEvent.ACTION_UP -> onKeyUp(event.keyCode, event)
            else -> false
        }
    }

    /**
     * 按键按下事件回调，默认不拦截按键事件
     */
    open fun onKeyDown(keyCode: Int, event: KeyEvent) = false

    /**
     * 按键抬起事件回调，默认不拦截按键事件
     */
    open fun onKeyUp(keyCode: Int, event: KeyEvent) = false
}