package com.tiamosu.fly.module.main.ui.fragments

import android.os.Handler
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
            postDelayed(Runnable { showSuccess() }, 1500)
        }
        postDelayed(Runnable { showFailure() })
    }

    private fun postDelayed(runnable: Runnable, delayMillis: Long = 1000L) {
        Handler().postDelayed(runnable, delayMillis)
    }

    override fun doBusiness() {}
}