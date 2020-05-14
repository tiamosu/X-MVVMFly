package com.tiamosu.fly.demo.ui.fragments

import android.os.Bundle
import android.util.Log
import com.tiamosu.fly.core.base.BaseVmDbFragment
import com.tiamosu.fly.core.base.DataBindingConfig
import com.tiamosu.fly.core.ext.lazyViewModel
import com.tiamosu.fly.demo.BR
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.HomeViewModel
import com.tiamosu.fly.integration.ext.navigate

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class HomeFragment : BaseVmDbFragment() {
    private val viewModel: HomeViewModel by lazyViewModel("ViewModel 初始化入参测试~~~ ^_^")
    private var isReloadData = false

    override fun getLayoutId() = R.layout.fragment_home

    override fun isNeedReload() = isReloadData

    override fun isCheckNetChanged() = true

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig()
            .addBindingParam(BR.click, ClickProxy())
    }

    override fun doBusiness() {
        if (isReloadData) {
            isReloadData = false
        }
        viewModel.print()
        Log.e(fragmentTag, "doBusiness")
    }

    inner class ClickProxy {

        fun startShared() {
            sharedViewModel.param.value = "SharedViewModel 共享数据传参测试~ ^_^"
            val bundle = Bundle().apply {
                putString(SharedFragment.KEY, "Fragment 跳转传参测试~~~")
            }
            navigate(R.id.action_mainFragment_to_sharedFragment, args = bundle)
        }

        fun startBus() {
            navigate(R.id.action_mainFragment_to_busFragment)
            isReloadData = true
            Log.e("xia", "isReloadData:$isReloadData")
        }

        fun startHttp() {
            navigate(R.id.action_mainFragment_to_httpFragment)
        }

        fun startGlide() {
            navigate(R.id.action_mainFragment_to_glideFragment)
        }

        fun startLoadSir() {
            navigate(R.id.action_mainFragment_to_loadSirFragment)
        }
    }
}