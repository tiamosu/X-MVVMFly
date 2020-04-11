package com.tiamosu.fly.module.main.ui.fragments

import android.view.View
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.main.R
import kotlinx.android.synthetic.main.fragment_loadsir.*

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class LoadSirFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_loadsir

    override fun initView(rootView: View?) {
        setLoadSir(load_ll) {
            showLoading()
            showSuccess()
        }
        showFailure()
    }

    override fun doBusiness() {}
}