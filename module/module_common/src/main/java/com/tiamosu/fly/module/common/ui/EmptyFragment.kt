package com.tiamosu.fly.module.common.ui

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.module.common.R
import com.tiamosu.fly.module.common.base.BaseFragment

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class EmptyFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_empty
    }

    override fun initData(bundle: Bundle?) {
    }

    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
    }

    override fun initEvent() {
    }

    override fun doBusiness() {
    }
}