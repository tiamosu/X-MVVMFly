package com.tiamosu.fly.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tiamosu.fly.base.delegate.FlyVisibleDelegate

/**
 * 描述：生命周期调用顺序：[onActivityCreated] → [onResume] → [onFlySupportVisible]
 * → [onFlyLazyInitView] → [onFlySupportInvisible] → [onPause]
 *
 * @author tiamosu
 * @date 2020/4/13.
 */
abstract class FlySupportFragment : Fragment() {
    internal val visibleDelegate by lazy { FlyVisibleDelegate(apply { }) }
    internal lateinit var activity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        visibleDelegate.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        visibleDelegate.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e("tiamosu", "onActivityCreated")

        visibleDelegate.onActivityCreated()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        Log.e("tiamosu", "onCreateAnimation")
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onResume() {
        super.onResume()
        Log.e("tiamosu", "onResume")

        visibleDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.e("tiamosu", "onPause")

        visibleDelegate.onPause()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        visibleDelegate.onHiddenChanged(hidden)
    }

    @Suppress("DEPRECATION")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        visibleDelegate.setUserVisibleHint(isVisibleToUser)
    }

    override fun onDestroyView() {
        visibleDelegate.onDestroyView()
        super.onDestroyView()
    }

    /**
     * 当 Fragment 对用户可见，执行 [onFlySupportVisible]
     */
    fun isFlySupportVisible() = visibleDelegate.isSupportVisible()

    /**
     * Fragment 对用户可见时
     */
    @CallSuper
    open fun onFlySupportVisible() {
        Log.e("tiamosu", "onFlySupportVisible")
    }

    /**
     * Fragment 对用户不可见时
     */
    @CallSuper
    open fun onFlySupportInvisible() {
        Log.e("tiamosu", "onFlySupportInvisible")
    }

    /**
     * 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载
     */
    @CallSuper
    open fun onFlyLazyInitView(savedInstanceState: Bundle?) {
        Log.e("tiamosu", "onFlyLazyInitView")
    }
}