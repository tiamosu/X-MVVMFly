package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ClickUtils
import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.databinding.FragmentMainBinding
import com.tiamosu.fly.demo.ext.init
import com.tiamosu.fly.demo.sharedViewModel
import com.tiamosu.fly.ext.addObserve

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseFragment() {
    private val dataBinding: FragmentMainBinding by lazyDataBinding()

    private val fragments by lazy {
        arrayListOf<Class<out Fragment>>().apply {
            add(HomeFragment::class.java)
            add(NewsFragment::class.java)
            add(SearchFragment::class.java)
            add(MyFragment::class.java)
        }
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_main)
    }

    override fun initView(rootView: View?) {
        dataBinding.mainViewPager.init(this, fragments)
            .let(dataBinding.mainTabBarLayout::setViewPager2)
    }

    override fun initEvent() {
        dataBinding.mainTabBarLayout.setOnItemSelectedListener {
            onItemSelectBefore { position ->
                Log.e("susu", "onItemSelectBefore:$position")
                true
            }
            onItemReselected { position ->
                Log.e("susu", "onItemReselected:$position")
            }
            onItemSelected { position, prePosition ->
                Log.e("susu", "onItemSelected:$position  prePosition:$prePosition")
            }
            onItemUnselected { prePosition ->
                Log.e("susu", "onItemUnselected:$prePosition")
            }
        }
    }

    override fun createObserver() {
        addObserve(sharedViewModel.selectTabItem) {
            dataBinding.mainTabBarLayout.setCurrentItem(it ?: 0)
        }
    }

    override fun doBusiness() {}

    override fun onFlySupportVisible() {
        super.onFlySupportVisible()
        val fragments = parentFragmentManager.fragments
        Log.e("susu", "fragments:$fragments")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("susu", "onDestroy")
    }

    override fun onBackPressedSupport(): Boolean {
        ClickUtils.back2HomeFriendly("再按一次退出")
        return true
    }
}