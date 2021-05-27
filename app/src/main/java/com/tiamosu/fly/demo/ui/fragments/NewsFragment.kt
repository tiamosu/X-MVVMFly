package com.tiamosu.fly.demo.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.databinding.FragmentNewsBinding
import com.tiamosu.fly.demo.ui.adapter.CustomFragmentStateAdapter
import com.tiamosu.fly.ext.clickNoRepeat

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class NewsFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentNewsBinding }

    private val fragments by lazy {
        mutableListOf<Fragment>().apply {
            add(NewsContentFragment())
        }
    }
    private val adapter by lazy {
        CustomFragmentStateAdapter(
            this,
            dataBinding.newsViewPager,
            fragments
        ) { fragment, position ->
            if (fragment is NewsContentFragment) {
                fragment.updateContent(position)
            }
        }
    }

    override fun getLayoutId() = R.layout.fragment_news

    override fun initView(rootView: View?) {
        adapter.updateDataSetChanged(fragments)
    }

    override fun initEvent() {
        var count = 0
        dataBinding.newsBtnAdd.clickNoRepeat {
            count++
            if (fragments.size >= 5) {
                ToastUtils.showLong("Fragment 加载数量太多啦~")
                return@clickNoRepeat
            }
            val fragment = when (count % 3) {
                0 -> NewsContentFragment()
                1 -> NewsContentFragment()
                2 -> NewsContentFragment()
                else -> NewsContentFragment()
            }
            fragments.add(fragment)
            adapter.updateDataSetChanged(fragments)
            dataBinding.newsViewPager.currentItem = fragments.lastIndex
        }

        dataBinding.newsBtnMinus.clickNoRepeat {
            if (fragments.size <= 0) {
                return@clickNoRepeat
            }
            fragments.removeLast()
            adapter.updateDataSetChanged(fragments)
        }
    }

    override fun doBusiness() {}
}