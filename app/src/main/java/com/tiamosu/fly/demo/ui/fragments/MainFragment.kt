package com.tiamosu.fly.demo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.tiamosu.fly.core.base.BaseDBFragment
import com.tiamosu.fly.core.utils.lazyViewModel
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.bridge.MainViewModel
import com.tiamosu.fly.demo.databinding.FragmentMainBinding

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseDBFragment<FragmentMainBinding>() {
    private val mainViewModel: MainViewModel by lazyViewModel("进行传参测试~~~ ^_^")

    override fun getLayoutId() = R.layout.fragment_main

    override fun initData(bundle: Bundle?) {
        viewDataBinding?.click = ClickProxy(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isStarted = lifecycle.currentState == Lifecycle.State.STARTED
        Log.e("tiamosu", "isStarted1:$isStarted")
    }

    override fun onResume() {
        super.onResume()
        val isStarted = lifecycle.currentState == Lifecycle.State.STARTED
        Log.e("tiamosu", "isStarted2:$isStarted")
    }

    override fun doBusiness() {
        mainViewModel.print()
    }

    class ClickProxy(private val fragment: Fragment) {

        fun startOther() {
//            fragment.start(newInstance(Router.obtainFragmentOtherCls()))
        }

        fun startShared() {
//            fragment.start(newInstance(SharedFragment::class.java))
        }

        fun startBus() {
//            fragment.start(newInstance(BusFragment::class.java))
        }

        fun startHttp() {
//            fragment.start(newInstance(HttpFragment::class.java))
        }

        fun startGlide() {
//            fragment.start(newInstance(GlideFragment::class.java))
        }

        fun startLoadSir() {
//            fragment.start(newInstance(LoadSirFragment::class.java))
        }
    }

//    override fun onBackPressedSupport(): Boolean {
//        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
//            context.finish()
//        } else {
//            TOUCH_TIME = System.currentTimeMillis()
//            ToastUtils.showShort("再按一次退出")
//        }
//        return true
//    }

    companion object {
        // 再点一次退出程序时间设置
        private const val WAIT_TIME = 2000L
        private var TOUCH_TIME: Long = 0
    }
}