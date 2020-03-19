package com.tiamosu.fly.module.common.router

import com.alibaba.android.arouter.launcher.ARouter
import com.tiamosu.fly.module.common.ui.fragment.EmptyFragment
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
object Router {

    fun obtainFragmentOtherCls(): Class<out ISupportFragment> {
        return obtainFragmentCls(getProviderOther()?.obtainOtherCls())
    }

    private fun getProviderOther(): IProviderOther? {
        return ARouter.getInstance()
            .build(RouterConstant.PROVIDER_OTHER).navigation() as? IProviderOther
    }

    private fun obtainFragmentCls(cls: Class<out ISupportFragment>?): Class<out ISupportFragment> {
        return cls ?: EmptyFragment::class.java
    }
}