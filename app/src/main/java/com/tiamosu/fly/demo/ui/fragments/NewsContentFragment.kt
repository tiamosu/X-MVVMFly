package com.tiamosu.fly.demo.ui.fragments

import android.annotation.SuppressLint
import android.view.View
import com.tiamosu.databinding.delegate.lazyDataBinding
import com.tiamosu.databinding.page.DataBindingConfig
import com.tiamosu.fly.demo.R
import com.tiamosu.fly.demo.base.BaseFragment
import com.tiamosu.fly.demo.databinding.FragmentNewsContentBinding

/**
 * @author tiamosu
 * @date 2020/10/16.
 */
class NewsContentFragment : BaseFragment() {
    private val dataBinding: FragmentNewsContentBinding by lazyDataBinding()
    private var position = 0

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_news_content)
    }

    fun updateContent(position: Int) {
        this.position = position
    }

    @SuppressLint("SetTextI18n")
    override fun initView(rootView: View?) {
        dataBinding.newsContentTvContent.text = "新闻$position"
    }

    override fun doBusiness() {
    }
}