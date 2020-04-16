package com.tiamosu.fly.demo.ui.fragments

import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.core.base.BaseDBFragment
import com.tiamosu.fly.core.utils.lazyViewModel
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.MainViewModel
import com.tiamosu.fly.demo.databinding.FragmentMainBinding
import com.tiamosu.fly.integration.ext.navigate

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseDBFragment<FragmentMainBinding>() {
    private val mainViewModel: MainViewModel by lazyViewModel("ViewModel 初始化入参测试~~~ ^_^")
    private var isReloadData = false

    override fun getLayoutId() = R.layout.fragment_main

    override fun isNeedReload() = isReloadData

    override fun initEvent() {
        viewDataBinding?.click = ClickProxy()
    }

    override fun doBusiness() {
        if (isReloadData) {
            isReloadData = false
        }
        mainViewModel.print()
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

    override fun onBackPressedSupport(): Boolean {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            context.finish()
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            ToastUtils.showShort("再按一次退出")
        }
        return true
    }

    companion object {
        // 再点一次退出程序时间设置
        private const val WAIT_TIME = 2000L
        private var TOUCH_TIME: Long = 0
    }
}