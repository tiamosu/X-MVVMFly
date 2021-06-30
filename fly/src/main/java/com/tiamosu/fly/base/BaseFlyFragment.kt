package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import com.tiamosu.databinding.page.FlyDataBindingFragment
import com.tiamosu.fly.base.action.*
import com.tiamosu.fly.base.dialog.loading.LoadingConfig
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onAttach] → [onCreate] → [initParameters] → [onCreateView]
 * → [onViewCreated] → [initView] → [createObserver] → [onActivityCreated] → [onResume]
 * → [onFlySupportVisible] → [onFlyLazyInitView] → [onFlyLazyInitView2] → [initEvent]
 * → [doBusiness] → [onPause] → [onFlySupportInvisible] → [onDestroyView] → [onDestroy]
 * → [onDetach]
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
        initParameters(bundle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)

        initView(rootView)
        createObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallbacks()
    }

    override fun onFlyLazyInitView2() {
        super.onFlyLazyInitView2()
        initEvent()
        doBusiness()
    }

    override fun showLoading(config: LoadingConfig?) {
        (context as? BaseFlyActivity)?.showLoading(config)
    }

    override fun hideLoading() {
        (context as? BaseFlyActivity)?.hideLoading()
    }
}