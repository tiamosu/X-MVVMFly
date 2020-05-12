package com.tiamosu.fly.fragmentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * @author tiamosu
 * @date 2020/4/13.
 */
open class FlySupportFragment : Fragment(), IFlySupportFragment {
    protected val fragmentTag by lazy { this.javaClass.simpleName }
    private val delegate by lazy { FlySupportFragmentDelegate(this) }
    internal lateinit var activity: AppCompatActivity

    override fun getSupportDelegate() = delegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(fragmentTag, "onAttach")
        delegate.onAttach()
        activity = context as AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(fragmentTag, "onCreate")
        delegate.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegate.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(fragmentTag, "onCreateView")
        super.onViewCreated(view, savedInstanceState)
        Log.d(fragmentTag, "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(fragmentTag, "onActivityCreated")
        delegate.onActivityCreated()
    }

    override fun onResume() {
        super.onResume()
        Log.d(fragmentTag, "onResume")
        delegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(fragmentTag, "onPause")
        delegate.onPause()
    }

    override fun onDestroyView() {
        Log.d(fragmentTag, "onDestroyView")
        delegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(fragmentTag, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(fragmentTag, "onDetach")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d(fragmentTag, "onHiddenChanged：$hidden")
        delegate.onHiddenChanged(hidden)
    }

    /**
     * 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载
     */
    @CallSuper
    override fun onFlyLazyInitView() {
        Log.d(
            fragmentTag,
            "onFlyLazyInitView 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载"
        )
    }

    /**
     * Fragment 对用户可见时
     */
    @CallSuper
    override fun onFlySupportVisible() {
        Log.d(fragmentTag, "onFlySupportVisible 真正的 Resume，开始相关操作")
    }

    /**
     * Fragment 对用户不可见时
     */
    @CallSuper
    override fun onFlySupportInvisible() {
        Log.d(fragmentTag, "onFlySupportInvisible 真正的 Pause，结束相关操作")
    }

    /**
     * 当 Fragment 对用户可见，执行 [onFlySupportVisible]
     */
    override fun isFlySupportVisible() = delegate.isSupportVisible()

    /**
     * 按返回键触发,前提是 [FlySupportActivity] 的 [FlySupportActivity.onBackPressed] 方法能被调用
     *
     * @return false 则继续向上传递，true 则消费掉该事件
     */
    override fun onBackPressedSupport() = delegate.onBackPressedSupport()
}