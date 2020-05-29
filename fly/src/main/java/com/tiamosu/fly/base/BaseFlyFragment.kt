package com.tiamosu.fly.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.appcompat.app.AppCompatActivity
import com.tiamosu.fly.fragmentation.FlySupportFragment
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onAttach] → [onCreate] → [initParameters] → [onCreateView]
 * → [onViewCreated] → [initView] → [onActivityCreated] → [onResume] → [onFlyLazyInitView]
 * → [initEvent] → [createObserver] → [doBusiness] → [onFlySupportVisible] → [onPause]
 * → [onFlySupportInvisible] → [onDestroyView] → [onDestroy] → [onDetach]
 *
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyFragment : FlySupportFragment(), IFlyBaseView {
    private val networkDelegate by lazy { NetworkDelegate() }
    var inflater: LayoutInflater? = null
    var container: ViewGroup? = null
    var rootView: View? = null

    //防止多次加载数据
    private var isDataLoaded = false

    final override fun getContext(): AppCompatActivity = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
        initParameters(arguments)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        this.container = container
        setContentView()
        return rootView
    }

    override fun setContentView() {
        if (rootView == null) {
            if (getLayoutId() > 0) {
                rootView = inflater?.inflate(getLayoutId(), container, false)
            }
        } else {
            // 缓存的 rootView 需要判断是否已经被加过 parent，如果有 parent 需要从 parent 删除，
            // 要不然会发生这个 rootView 已经有 parent 的错误。
            var viewParent: ViewParent
            if (rootView!!.parent.also { viewParent = it } is ViewGroup) {
                (viewParent as ViewGroup).removeView(rootView)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(rootView)
    }

    final override fun onLazyInitView() {
        onFlyLazyInitView()
        initEvent()
        createObserver()
        tryLoadData()
    }

    final override fun onSupportVisible() {
        onFlySupportVisible()

        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
        tryLoadData()
    }

    final override fun onSupportInvisible() {
        onFlySupportInvisible()
    }

    private fun tryLoadData() {
        if (isNeedReload() || !isDataLoaded) {
            doBusiness()
            isDataLoaded = true
        }
    }
}