package com.tiamosu.fly.demo.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.ui.adapter.CustomFragmentStateAdapter
import com.tiamosu.fly.ext.clickNoRepeat
import kotlinx.android.synthetic.main.fragment_viewpager2.*

/**
 * @author tiamosu
 * @date 2020/7/18.
 */
class ViewPager2Fragment : BaseFragment() {
    private val fragments by lazy { mutableListOf<Fragment>() }
    private var count = 0
    private val adapter by lazy {
        CustomFragmentStateAdapter(
            this,
            viewPager2_viewPager,
            fragments
        )
    }

    override fun getLayoutId() = R.layout.fragment_viewpager2

    override fun initView(rootView: View?) {
//        viewPager2_viewPager.apply {
//            isUserInputEnabled = true
//            this@ViewPager2Fragment.adapter =
//                CustomFragmentStateAdapter(this@ViewPager2Fragment, this, fragments)
//        }
    }

    override fun initEvent() {
        viewPager2_add.clickNoRepeat {
            count++
            val fragment = when (count % 3) {
                0 -> NewsFragment()
                1 -> SearchFragment()
                2 -> MyFragment()
                else -> HomeFragment()
            }
            fragments.add(fragment)
            adapter.updateDataSetChanged(fragments)
        }

        viewPager2_minus.clickNoRepeat {
            if (fragments.size <= 0) {
                return@clickNoRepeat
            }
            fragments.removeLast()
            adapter.updateDataSetChanged(fragments)
        }
    }

    override fun doBusiness() {}
}