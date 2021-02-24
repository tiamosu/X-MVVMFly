package com.tiamosu.fly.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
    var inflater: LayoutInflater? = null
    var container: ViewGroup? = null
    var rootView: View? = null

    //是否是第一次加载数据，防止多次加载数据
    private var isFirstLoadData = true

    //懒加载初始化优化，防止页面切换动画还未执行完毕时进行数据加载导致渲染卡顿现象
    private var hasCreateAnimation = false
    private var isLazyInitView = false
    private var isAnimationEnd = false

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
            (rootView?.parent as? ViewGroup)?.removeView(rootView)
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
        isLazyInitView = true
        initEvent()
    }

    final override fun onSupportVisible() {
        super.onSupportVisible()
        onFlySupportVisible()
        Log.e("susu", "$fragmentTag   onSupportVisible")

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
        Log.e("susu", "$fragmentTag   onSupportInvisible")
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
}