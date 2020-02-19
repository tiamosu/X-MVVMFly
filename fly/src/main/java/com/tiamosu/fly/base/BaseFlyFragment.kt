package com.tiamosu.fly.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import me.yokeyword.fragmentation.SupportFragment
import java.lang.ref.WeakReference

/**
 * @author weixia
 * @date 2020/2/18.
 */
abstract class BaseFlyFragment : SupportFragment(), IBaseView {
    private var weakReferenceRootView: WeakReference<View>? = null
    //保证转场动画的流畅性
    private var isonLazyInitView = false
    private var isOnEnterAnimationEnd = false

    @Suppress("MemberVisibilityCanBePrivate")
    var rootView: View? = null
        get() {
            return weakReferenceRootView?.get()
        }
        internal set

    @Suppress("UNCHECKED_CAST")
    fun <T : SupportFragment> getParentDelegate(): T {
        return (parentFragment ?: this) as T
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contentView = rootView
        if (contentView == null) {
            if (getLayoutId() > 0) {
                val view = inflater.inflate(getLayoutId(), container, false)
                weakReferenceRootView = WeakReference(view)
            }
        } else {
            // 缓存的 rootView 需要判断是否已经被加过 parent，如果有 parent 需要从 parent 删除，
            // 要不然会发生这个 rootview 已经有 parent 的错误。
            var viewParent: ViewParent
            if (contentView.parent.also { viewParent = it } is ViewGroup) {
                (viewParent as ViewGroup).removeView(contentView)
            }
        }
        return contentView ?: rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData(arguments)
        initView(savedInstanceState, rootView)
        initEvent()
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        isOnEnterAnimationEnd = true
        if (isonLazyInitView) {
            doBusiness()
        }
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        isonLazyInitView = true
        if (isOnEnterAnimationEnd) {
            doBusiness()
        }
    }
}