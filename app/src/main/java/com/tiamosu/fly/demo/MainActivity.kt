package com.tiamosu.fly.demo

import com.tiamosu.fly.base.FlyProxyActivity
import com.tiamosu.fly.demo.page.SplashFragment
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

class MainActivity : FlyProxyActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getRootFragment(): Class<out ISupportFragment> {
        return SplashFragment::class.java
    }

    override fun loadRootFragment(containerId: Int) {
        super.loadRootFragment(R.id.main_container_fl)
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator? {
        return DefaultVerticalAnimator()
    }
}
