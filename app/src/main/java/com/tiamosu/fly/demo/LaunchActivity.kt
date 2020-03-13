package com.tiamosu.fly.demo

import com.tiamosu.fly.module.common.base.ProxyActivity
import com.tiamosu.fly.module.main.ui.MainFragment
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

class LaunchActivity : ProxyActivity() {

    override fun getRootFragment(): Class<out ISupportFragment> {
        return MainFragment::class.java
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator? {
        return DefaultVerticalAnimator()
    }
}
