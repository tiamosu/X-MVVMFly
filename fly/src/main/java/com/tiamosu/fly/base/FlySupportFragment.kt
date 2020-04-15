package com.tiamosu.fly.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tiamosu.fly.base.delegate.FlyVisibleDelegate

/**
 * 描述：生命周期调用顺序：[onAttach] → [onCreate] → [onCreateView] →
 * [onViewCreated] → [onFlyLazyInitView] → [onFlySupportVisible] → [onActivityCreated]
 * → [onResume] → [onPause] → [onFlySupportInvisible]
 *
 * @author tiamosu
 * @date 2020/4/13.
 */
abstract class FlySupportFragment : Fragment() {
    private val fragmentTag by lazy { this.javaClass.simpleName }
    internal val visibleDelegate by lazy { FlyVisibleDelegate(apply { }) }
    internal lateinit var activity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(fragmentTag, "onAttach")
        activity = context as AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(fragmentTag, "onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(fragmentTag, "onCreateView")
        visibleDelegate.onCreateView()
        super.onViewCreated(view, savedInstanceState)

        Log.d(fragmentTag, "onViewCreated")
        visibleDelegate.onViewCreated()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(fragmentTag, "onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        Log.d(fragmentTag, "onResume")
        visibleDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(fragmentTag, "onPause")
        visibleDelegate.onPause()
    }

    override fun onDestroyView() {
        visibleDelegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d(fragmentTag, "onHiddenChanged：$hidden")
        visibleDelegate.onHiddenChanged(hidden)
    }

    @Suppress("DEPRECATION")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d(fragmentTag, "setUserVisibleHint：$isVisibleToUser")
        visibleDelegate.setUserVisibleHint(isVisibleToUser)
    }

    /**
     * 当 Fragment 对用户可见，执行 [onFlySupportVisible]
     */
    fun isFlySupportVisible() = visibleDelegate.isSupportVisible()

    /**
     * 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载
     */
    @CallSuper
    open fun onFlyLazyInitView() {
        Log.d(fragmentTag, "onFlyLazyInitView 第一次可见，可执行 View 初始化等")
    }

    /**
     * Fragment 对用户可见时
     */
    @CallSuper
    open fun onFlySupportVisible() {
        Log.d(fragmentTag, "onFlySupportVisible 真正的 Resume，开始相关操作")
    }

    /**
     * Fragment 对用户不可见时
     */
    @CallSuper
    open fun onFlySupportInvisible() {
        Log.d(fragmentTag, "onFlySupportInvisible 真正的 Pause，结束相关操作")
    }
}