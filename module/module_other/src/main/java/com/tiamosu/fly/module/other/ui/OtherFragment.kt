package com.tiamosu.fly.module.other.ui

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.other.R

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class OtherFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_other
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
    }

    override fun initEvent() {}
    override fun doBusiness() {}
}