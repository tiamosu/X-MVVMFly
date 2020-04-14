package com.tiamosu.fly.core.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * @author tiamosu
 * @date 2020/3/26.
 */
abstract class BaseDBActivity<VDB : ViewDataBinding> : BaseActivity() {
    protected var viewDataBinding: VDB? = null

    override fun setContentView() {
        if (getLayoutId() > 0) {
            viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
            viewDataBinding?.lifecycleOwner = this
            viewDataBinding?.executePendingBindings()
            rootView = viewDataBinding?.root
        }
    }
}