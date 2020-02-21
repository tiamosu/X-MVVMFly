package com.tiamosu.fly.demo

import com.tiamosu.fly.base.BaseProxyActivity
import com.tiamosu.fly.demo.page.SplashFragment
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

class MainActivity : BaseProxyActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getRootFragment(): Class<out ISupportFragment> {
        return SplashFragment::class.java
    }

    override fun loadRootFragment() {
        super.loadRootFragment()
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator? {
        return DefaultVerticalAnimator()
    }
}
