package com.tiamosu.fly.base

import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * @author tiamosu
 * @date 2020/5/15.
 */
abstract class BaseFlyVmDbActivity : BaseFlyActivity() {

    protected abstract fun getDataBindingConfig(): DataBindingConfig
    protected var binding: ViewDataBinding? = null

    override fun setContentView() {
        if (getLayoutId() > 0) {
            val dataBindingConfig = getDataBindingConfig()
            val dataBinding: ViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
            dataBinding.lifecycleOwner = this
            val bindingParams = dataBindingConfig.getBindingParams()
            bindingParams.forEach { key, value ->
                dataBinding.setVariable(key, value)
            }
            binding = dataBinding
            rootView = dataBinding.root
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.unbind()
        binding = null
    }
}