package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import com.tiamosu.databinding.page.FlyDataBindingFragment
import com.tiamosu.fly.base.action.*
import com.tiamosu.fly.base.dialog.loading.LoadingConfig
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onAttach] → [onCreate] → [initParameters] → [onCreateView]
 * → [onViewCreated] → [initView] → [initEvent] → [createObserver] → [onActivityCreated]
 * → [onResume] → [onFlySupportVisible] → [onFlyLazyInitView] → [onFlyLazyInitView2]
 * → [doBusiness] → [onPause] → [onFlySupportInvisible] → [onDestroyView] → [onDestroy]
 * → [onDetach]
 *
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyFragment : FlyDataBindingFragment(),
    FragmentAction, BundleAction, HandlerAction, KeyboardAction, NetAction {

    private val networkDelegate by lazy { NetworkDelegate(this) }

    final override val bundle
        get() = arguments

    final override fun getContext() = activity

    private var isViewCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParameters(bundle)

        if (getDataBindingConfig().layoutId <= 0) {
            onViewCreated()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated()
    }

    private fun onViewCreated() {
        if (isViewCreated) return
        isViewCreated = true

        //添加网络状态监听
        if (isCheckNetChanged()) {
            networkDelegate.addNetworkObserve(this)
        }

        initView(rootView)
        initEvent()
        createObserver()
    }

    override fun onDestroyView() {
        isViewCreated = false
        removeCallbacks()
        super.onDestroyView()
    }

    override fun onFlyLazyInitView2() {
        super.onFlyLazyInitView2()
        doBusiness()
    }

    override fun showLoading(config: LoadingConfig?) {
        (context as? BaseFlyActivity)?.showLoading(config)
    }

    override fun hideLoading() {
        (context as? BaseFlyActivity)?.hideLoading()
    }
}