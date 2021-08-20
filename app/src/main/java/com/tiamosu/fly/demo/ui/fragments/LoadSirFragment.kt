package com.tiamosu.fly.demo.ui.fragments

import android.view.View
import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.base.dialog.loading.Loader
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.databinding.FragmentLoadsirBinding
import com.tiamosu.fly.demo.ext.loadServiceInit

/**
 * @author tiamosu
 * @date 2020/4/11.
 */
class LoadSirFragment : BaseFragment() {
    private val dataBinding: FragmentLoadsirBinding by lazyDataBinding()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_loadsir)
    }

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
        Loader.showLoading()
        postDelayed({
            hideLoading()
            showViewError()
        }, 1500)
    }

    override fun doBusiness() {
        requestData()
    }
}