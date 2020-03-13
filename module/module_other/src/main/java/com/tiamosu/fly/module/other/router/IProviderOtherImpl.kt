package com.tiamosu.fly.module.other.router

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.tiamosu.fly.module.common.router.IProviderOther
import com.tiamosu.fly.module.common.router.RouterConstant
import com.tiamosu.fly.module.other.ui.OtherFragment
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
@Suppress("unused")
@Route(path = RouterConstant.PROVIDER_OTHER)
class IProviderOtherImpl : IProviderOther {

    override fun obtainOtherCls(): Class<out ISupportFragment> {
        return OtherFragment::class.java
    }

    override fun init(context: Context) {}
}