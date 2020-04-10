package com.tiamosu.fly.module.main.ui.fragments

import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.module.common.base.BaseDBFragment
import com.tiamosu.fly.module.common.router.Router
import com.tiamosu.fly.module.common.utils.lazyViewModel
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.module.main.bridge.MainViewModel
import com.tiamosu.fly.module.main.databinding.FragmentMainBinding
import com.tiamosu.fly.utils.newInstance
import me.yokeyword.fragmentation.SupportFragment

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseDBFragment<FragmentMainBinding>() {
    private val mainViewModel: MainViewModel by lazyViewModel("进行传参测试~~~ ^_^")

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initData(bundle: Bundle?) {
        viewDataBinding?.click = ClickProxy(this)
    }

    override fun doBusiness() {
        mainViewModel.print()
    }

    class ClickProxy(private val fragment: SupportFragment) {

        fun startOther() {
            fragment.start(newInstance(Router.obtainFragmentOtherCls()))
        }

        fun startShared() {
            fragment.start(newInstance(SharedFragment::class.java))
        }

        fun startBus() {
            fragment.start(newInstance(BusFragment::class.java))
        }

        fun startHttp() {
            fragment.start(newInstance(HttpFragment::class.java))
        }

        fun startGlide() {
            fragment.start(newInstance(GlideFragment::class.java))
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