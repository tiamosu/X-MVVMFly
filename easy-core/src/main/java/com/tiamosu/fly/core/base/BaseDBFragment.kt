package com.tiamosu.fly.core.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * @author tiamosu
 * @date 2020/3/26.
 */
abstract class BaseDBFragment<VDB : ViewDataBinding> : BaseFragment() {
    protected var viewDataBinding: VDB? = null

    override fun setContentView() {
        if (getLayoutId() > 0) {
            viewDataBinding = inflater?.let {
                DataBindingUtil.inflate(it, getLayoutId(), container, false)
            }
            viewDataBinding?.lifecycleOwner = this
            viewDataBinding?.executePendingBindings()
            rootView = viewDataBinding?.root
        }
    }
}