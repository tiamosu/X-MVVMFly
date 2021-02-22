package com.tiamosu.fly.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.appcompat.app.AppCompatActivity
import com.tiamosu.fly.fragmentation.FlySupportFragment
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onAttach] → [onCreate] → [initParameters] → [onCreateView]
 * → [onViewCreated] → [initView] → [createObserver] → [onActivityCreated] → [onResume]
 * → [initEvent] → [onFlySupportVisible] → [onFlyLazyInitView] → [doBusiness]
 * → [onPause] → [onFlySupportInvisible] → [onDestroyView] → [onDestroy] → [onDetach]
 *
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyFragment : FlySupportFragment(), IFlyBaseView {
    private val networkDelegate by lazy { NetworkDelegate() }
    private val lazyHandler by lazy { Handler(Looper.getMainLooper()) }
    var inflater: LayoutInflater? = null
    var container: ViewGroup? = null
    var rootView: View? = null

    //是否是第一次加载数据，防止多次加载数据
    private var isFirstLoadData = true

    /**
     * 延迟加载：防止切换动画还没执行完毕时进行数据加载，造成渲染卡顿
     * 用于第一次懒加载 [onFlyLazyInitView]、[doBusiness]
     * 默认延迟时间为100，单位ms
     */
    open fun lazyLoadTime() = 100L

    final override fun getContext(): AppCompatActivity = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        isFirstLoadData = true
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
        initView(rootView)
        createObserver()
    }

    final override fun onLazyInitView() {
        super.onLazyInitView()
        initEvent()

        lazyHandler.postDelayed({
            onFlyLazyInitView()
            if (isFirstLoadData) {
                doBusiness()
                isFirstLoadData = false
            }
        }, lazyLoadTime())
    }

    final override fun onSupportVisible() {
        super.onSupportVisible()
        onFlySupportVisible()
        Log.e("susu", "$fragmentTag   onSupportVisible")

        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
        if (!isFirstLoadData && isNeedReload()) {
            doBusiness()
        }
    }

    final override fun onSupportInvisible() {
        super.onSupportInvisible()
        onFlySupportInvisible()
        Log.e("susu", "$fragmentTag   onSupportInvisible")
    }

    override fun onDestroy() {
        super.onDestroy()
        lazyHandler.removeCallbacksAndMessages(null)
    }
}