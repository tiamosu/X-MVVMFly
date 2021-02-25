package com.tiamosu.fly.base

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Lifecycle
import com.tiamosu.fly.base.action.*
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
abstract class BaseFlyFragment : FlySupportFragment(),
    FragmentAction, BundleAction, HandlerAction, KeyboardAction, NetAction {

    private val networkDelegate by lazy { NetworkDelegate() }

    //是否是第一次加载数据，防止多次加载数据
    private var isFirstLoadData = true

    //懒加载初始化优化，防止页面切换动画还未执行完毕时进行数据加载导致渲染卡顿现象
    private var hasCreateAnimation = false
    private var isLazyInitView = false
    private var isAnimationEnd = false

    var inflater: LayoutInflater? = null
    var container: ViewGroup? = null
    var rootView: View? = null

    final override val bundle
        get() = arguments

    final override fun getContext() = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParameters(bundle)
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
            (rootView?.parent as? ViewGroup)?.removeView(rootView)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirstLoadData = true
        initView(rootView)
        createObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        removeCallbacks()
    }

    final override fun onLazyInitView() {
        super.onLazyInitView()
        isLazyInitView = true
        initEvent()
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
    }

    final override fun onSupportVisible() {
        super.onSupportVisible()
        onFlySupportVisible()

        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
        if (isFirstLoadData) {
            if (!hasCreateAnimation || isAnimationEnd) {
                tryLazyLoad()
            }
        } else {
            if (isNeedReload()) {
                doBusiness()
            }
        }
    }

    final override fun onSupportInvisible() {
        super.onSupportInvisible()
        onFlySupportInvisible()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim <= 0) {
            isAnimationEnd = true
            return super.onCreateAnimation(transit, enter, nextAnim)
        }

        hasCreateAnimation = true
        return AnimationUtils.loadAnimation(context, nextAnim).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    isAnimationEnd = true
                    if (enter && isLazyInitView && isFirstLoadData) {
                        tryLazyLoad()
                    }
                }
            })
        }
    }

    private fun tryLazyLoad() {
        onFlyLazyInitView()
        doBusiness()
        isFirstLoadData = false
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