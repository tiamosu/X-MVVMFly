package com.tiamosu.fly.fragmentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * @author tiamosu
 * @date 2020/4/13.
 */
open class FlySupportFragment : Fragment(), IFlySupportFragment {
    protected val fragmentTag: String by lazy { this.javaClass.simpleName }
    private val delegate by lazy { FlySupportFragmentDelegate(this) }
    internal lateinit var activity: AppCompatActivity

    override fun getSupportDelegate() = delegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(fragmentTag, "onAttach")
        delegate.onAttach()
        activity = context as AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(fragmentTag, "onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(fragmentTag, "onCreateView")
        super.onViewCreated(view, savedInstanceState)
        Log.e(fragmentTag, "onViewCreated")
    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(fragmentTag, "onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        Log.e(fragmentTag, "onResume")
        delegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.e(fragmentTag, "onPause")
        delegate.onPause()
    }

    override fun onDestroyView() {
        Log.e(fragmentTag, "onDestroyView")
        delegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(fragmentTag, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(fragmentTag, "onDetach")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.e(fragmentTag, "onHiddenChanged：$hidden")
    }

    override fun onLazyInitView() {}
    override fun onSupportVisible() {}
    override fun onSupportInvisible() {}

    /**
     * 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载
     */
    override fun onFlyLazyInitView() {
        Log.e(
            fragmentTag,
            "onFlyLazyInitView 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载"
        )
    }

    /**
     * Fragment 对用户可见时
     */
    override fun onFlySupportVisible() {
        Log.e(fragmentTag, "onFlySupportVisible 真正的 Resume，开始相关操作")
    }

    /**
     * Fragment 对用户不可见时
     */
    override fun onFlySupportInvisible() {
        Log.e(fragmentTag, "onFlySupportInvisible 真正的 Pause，结束相关操作")
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