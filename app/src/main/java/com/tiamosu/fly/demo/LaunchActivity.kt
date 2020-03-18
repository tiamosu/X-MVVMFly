package com.tiamosu.fly.demo

import com.tiamosu.fly.module.common.base.ProxyActivity
import com.tiamosu.fly.module.main.ui.fragments.MainFragment
import com.tiamosu.fly.module.main.ui.fragments.SplashFragment
import com.tiamosu.fly.utils.newInstance
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

class LaunchActivity : ProxyActivity() {

    override fun getRootFragment(): Class<out ISupportFragment> {
        return SplashFragment::class.java
    }

    override fun loadRootFragment(containerId: Int) {
        if (findFragment(getRootFragment()) == null
            && findFragment(MainFragment::class.java) == null
        ) {
            loadRootFragment(containerId, newInstance(getRootFragment()))
        }
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator? {
        return DefaultVerticalAnimator()
    }
}
