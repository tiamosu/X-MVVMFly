package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.ext.init
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseFragment() {

    private val fragments by lazy {
        arrayListOf<Class<out Fragment>>().apply {
            add(HomeFragment::class.java)
            add(NewsFragment::class.java)
            add(SearchFragment::class.java)
            add(MyFragment::class.java)
        }
    }

    override fun getLayoutId() = R.layout.fragment_main

    override fun initView(rootView: View?) {
        main_viewPager.init(this, fragments)
            .let(main_tabBarLayout::setViewPager2)
    }

    override fun initEvent() {
        main_tabBarLayout.setOnItemSelectedListener(onItemSelected = { position, prePosition ->
            Log.e("xia", "onItemSelected:$position  prePosition:$prePosition")
        }, onItemUnselected = { position ->
            Log.e("xia", "onItemUnselected:$position")
        }, onItemReselected = { position ->
            Log.e("xia", "onItemReselected:$position")
        })
    }

    override fun doBusiness() {}

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