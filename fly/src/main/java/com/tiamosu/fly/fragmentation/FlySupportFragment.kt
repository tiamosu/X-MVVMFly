package com.tiamosu.fly.fragmentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.LogUtils

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
        LogUtils.dTag(fragmentTag, "onAttach")
        delegate.onAttach()
        activity = context as AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.dTag(fragmentTag, "onCreate")
        delegate.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegate.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        LogUtils.dTag(fragmentTag, "onCreateView")
        super.onViewCreated(view, savedInstanceState)
        LogUtils.dTag(fragmentTag, "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtils.dTag(fragmentTag, "onActivityCreated")
        delegate.onActivityCreated()
    }

    override fun onResume() {
        super.onResume()
        LogUtils.dTag(fragmentTag, "onResume")
        delegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        LogUtils.dTag(fragmentTag, "onPause")
        delegate.onPause()
    }

    override fun onDestroyView() {
        LogUtils.dTag(fragmentTag, "onDestroyView")
        delegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.dTag(fragmentTag, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        LogUtils.dTag(fragmentTag, "onDetach")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtils.dTag(fragmentTag, "onHiddenChanged：$hidden")
        delegate.onHiddenChanged(hidden)
    }

    override fun onLazyInitView() {}
    override fun onSupportVisible() {}
    override fun onSupportInvisible() {}

    /**
     * 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载
     */
    override fun onFlyLazyInitView() {
        LogUtils.dTag(
            fragmentTag,
            "onFlyLazyInitView 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载"
        )
    }

    /**
     * Fragment 对用户可见时
     */
    override fun onFlySupportVisible() {
        LogUtils.dTag(fragmentTag, "onFlySupportVisible 真正的 Resume，开始相关操作")
    }

    /**
     * Fragment 对用户不可见时
     */
    override fun onFlySupportInvisible() {
        LogUtils.dTag(fragmentTag, "onFlySupportInvisible 真正的 Pause，结束相关操作")
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