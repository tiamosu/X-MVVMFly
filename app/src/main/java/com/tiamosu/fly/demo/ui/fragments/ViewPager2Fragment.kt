package com.tiamosu.fly.demo.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
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
    private val fragments by lazy {
        mutableListOf<Fragment>().apply {
            add(MyFragment())
        }
    }
    private val adapter by lazy {
        CustomFragmentStateAdapter(
            this,
            viewPager2_viewPager,
            fragments
        )
    }

    override fun getLayoutId() = R.layout.fragment_viewpager2

    override fun initView(rootView: View?) {
        adapter.updateDataSetChanged(fragments)
    }

    override fun initEvent() {
        var count = 0
        viewPager2_add.clickNoRepeat {
            count++
            if (fragments.size >= 5) {
                ToastUtils.showLong("Fragment 加载数量太多啦~")
                return@clickNoRepeat
            }
            val fragment = when (count % 3) {
                0 -> NewsFragment()
                1 -> SearchFragment()
                2 -> MyFragment()
                else -> MyFragment()
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