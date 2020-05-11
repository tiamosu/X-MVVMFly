package com.tiamosu.fly.demo.ui.fragments

import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.ext.init
import com.tiamosu.fly.demo.R
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseFragment() {

    private val fragments by lazy {
        arrayListOf<Fragment>().apply {
            add(HomeFragment())
            add(SearchFragment())
            add(MyFragment())
        }
    }

    override fun getLayoutId() = R.layout.fragment_main

    override fun initView(rootView: View?) {
        main_viewPager.also {
            it.offscreenPageLimit = 1
            it.init(this, fragments, true)
            main_tabBarLayout.setViewPager2(it)
        }
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

    override fun doBusiness() {
    }
}