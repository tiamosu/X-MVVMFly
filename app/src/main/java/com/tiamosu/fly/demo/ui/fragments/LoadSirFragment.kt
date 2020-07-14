package com.tiamosu.fly.demo.ui.fragments

import android.os.Handler
import android.os.Looper
import android.view.View
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.ext.loadServiceInit
import com.tiamosu.fly.demo.R
import kotlinx.android.synthetic.main.fragment_loadsir.*

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class LoadSirFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_loadsir

    override fun initView(rootView: View?) {
        loadServiceInit(load_ll) {
            showViewLoading()
            postDelayed({ showViewSuccess() }, 1500)
        }
        postDelayed({ showViewError() })
    }

    private fun postDelayed(runnable: Runnable, delayMillis: Long = 1000L) {
        Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis)
    }

    override fun doBusiness() {}
}