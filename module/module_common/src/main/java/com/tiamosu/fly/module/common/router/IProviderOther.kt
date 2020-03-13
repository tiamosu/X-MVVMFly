package com.tiamosu.fly.module.common.router

import com.alibaba.android.arouter.facade.template.IProvider
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
interface IProviderOther : IProvider {

    fun obtainOtherCls(): Class<out ISupportFragment>
}