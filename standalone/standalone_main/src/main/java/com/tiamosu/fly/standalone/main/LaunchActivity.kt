package com.tiamosu.fly.standalone.main

import com.tiamosu.fly.module.common.base.ProxyActivity
import com.tiamosu.fly.module.main.ui.fragments.MainFragment
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author tiamosu
 * @date 2020/3/24.
 */
class LaunchActivity : ProxyActivity() {

    override fun getRootFragment(): Class<out ISupportFragment?> {
        return MainFragment::class.java
    }
}