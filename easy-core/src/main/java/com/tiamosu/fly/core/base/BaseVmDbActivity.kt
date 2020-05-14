package com.tiamosu.fly.core.base

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.base.BaseFlyVmDbActivity

/**
 * @author tiamosu
 * @date 2020/5/15.
 */
abstract class BaseVmDbActivity : BaseFlyVmDbActivity() {

    override fun initParameters(bundle: Bundle?) {}

    override fun initView(rootView: View?) {}

    override fun initEvent() {}

    override fun createObserver() {}
}