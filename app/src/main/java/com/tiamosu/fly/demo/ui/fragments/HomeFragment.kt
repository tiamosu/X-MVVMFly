package com.tiamosu.fly.demo.ui.fragments

import android.os.Bundle
import com.tiamosu.fly.base.DataBindingConfig
import com.tiamosu.fly.core.base.BaseVmDbFragment
import com.tiamosu.fly.core.ext.lazyViewModel
import com.tiamosu.fly.demo.BR
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.HomeViewModel
import com.tiamosu.fly.ext.navigate

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class HomeFragment : BaseVmDbFragment() {
    private val viewModel: HomeViewModel by lazyViewModel("ViewModel 初始化入参测试~~~ ^_^")

    override fun getLayoutId() = R.layout.fragment_home

    override fun isCheckNetChanged() = true

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig()
            .addBindingParam(BR.click, ClickProxy())
    }

    override fun doBusiness() {
        viewModel.print()
    }

    inner class ClickProxy {

        fun startShared() {
            sharedViewModel.param.value = "SharedViewModel 共享数据传参测试~ ^_^"
            val bundle = Bundle().apply {
                putString(SharedFragment.KEY, "Fragment 跳转传参测试~~~")
            }
            navigate(R.id.action_to_sharedFragment, args = bundle)
        }

        fun startHttp() {
            navigate(R.id.action_to_httpFragment)
        }

        fun startGlide() {
            navigate(R.id.action_to_glideFragment)
        }

        fun startLoadSir() {
            navigate(R.id.action_to_loadSirFragment)
        }

        fun startViewPager2() {
            navigate(R.id.action_to_viewPager2Fragment)
        }
    }
}