package com.tiamosu.fly.core.base

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.base.BaseFlyVmDbFragment
import com.tiamosu.fly.core.bridge.SharedViewModel
import com.tiamosu.fly.ext.lazyAppViewModel

/**
 * @author tiamosu
 * @date 2020/5/15.
 */
abstract class BaseVmDbFragment : BaseFlyVmDbFragment() {
    val sharedViewModel: SharedViewModel by lazyAppViewModel()

    override fun initParameters(bundle: Bundle?) {}

    override fun initView(rootView: View?) {}

    override fun initEvent() {}

    override fun createObserver() {}
}