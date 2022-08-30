package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import androidx.core.os.bundleOf
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.BR
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.bridge.request.HomeViewModel
import com.tiamosu.fly.demo.ext.jumpFragment
import com.tiamosu.fly.demo.ext.lazyViewModel
import com.tiamosu.fly.demo.sharedViewModel

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class HomeFragment : BaseFragment() {
    private val viewModel: HomeViewModel by lazyViewModel("ViewModel 初始化入参测试~~~ ^_^")

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_home).apply {
            addBindingParam(BR.click, ClickProxy())
        }
    }

    override fun doBusiness() {
        viewModel.print()
    }

    inner class ClickProxy {

        fun startShared() {
            sharedViewModel.param.value = "SharedViewModel 共享数据传参测试~ ^_^"
            jumpFragment(
                resId = R.id.sharedFragment,
                args = bundleOf(SharedFragment.KEY to "Fragment 跳转传参测试~~~")
            )
        }

        fun startHttp() {
            jumpFragment(resId = R.id.httpFragment)
        }

        fun startGlide() {
            jumpFragment(resId = R.id.glideFragment)
        }

        fun startLoadSir() {
            jumpFragment(resId = R.id.loadSirFragment)
        }
    }

    override fun isCheckNetChanged() = true

    override fun onNetworkStateChanged(isConnected: Boolean) {
        Log.e("susu", "onNetworkStateChanged:$isConnected")
    }

    override fun onNetReConnect() {
        Log.e("susu", "onNetReConnect")
    }
}