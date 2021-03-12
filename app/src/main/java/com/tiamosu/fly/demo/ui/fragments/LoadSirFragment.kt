package com.tiamosu.fly.demo.ui.fragments

import android.view.View
import com.tiamosu.fly.core.base.BaseFragment
import com.tiamosu.fly.core.ext.loadServiceInit
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.databinding.FragmentLoadsirBinding

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class LoadSirFragment : BaseFragment() {
    private val dataBinding by lazy { binding as FragmentLoadsirBinding }

    override fun getLayoutId() = R.layout.fragment_loadsir

    override fun initView(rootView: View?) {
        loadServiceInit(dataBinding.loadLl) {
            showViewLoading()
            postDelayed({
                dataBinding.loadSirTvResult.text = "加载成功"
                showViewSuccess()
            }, 1000)
        }
    }

    /**
     * 模拟数据请求
     */
    private fun requestData() {
        showLoading()
        postDelayed({
            hideLoading()
            showViewError()
        }, 1500)
    }

    override fun doBusiness() {
        requestData()
    }
}