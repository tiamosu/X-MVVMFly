package com.tiamosu.fly.demo.ui.fragments

import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.R

/**
 * @author tiamosu
 * @date 2020/5/12.
 */
class SearchFragment : BaseFragment() {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_search)
    }

    override fun doBusiness() {}
}