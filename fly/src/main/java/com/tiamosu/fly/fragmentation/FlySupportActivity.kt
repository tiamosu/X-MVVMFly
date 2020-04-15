package com.tiamosu.fly.fragmentation

import androidx.appcompat.app.AppCompatActivity

/**
 * @author tiamosu
 * @date 2020/4/15.
 */
open class FlySupportActivity : AppCompatActivity(), IFlySupportActivity {
    private val delegate by lazy { FlySupportActivityDelegate(this) }

    override fun getSupportDelegate() = delegate

    /**
     * 不建议复写该方法，请使用 [onBackPressedSupport] 代替
     */
    override fun onBackPressed() {
        delegate.onBackPressed()
    }

    /**
     * 该方法回调时机为，Activity 回退栈内 Fragment 的数量小于等于1时，默认 finish Activity
     * 请尽量复写该方法，避免复写 [onBackPressed]，以保证 [FlySupportFragment]
     * 内的 [FlySupportFragment.onBackPressedSupport] 回退事件正常执行
     */
    override fun onBackPressedSupport() {
        delegate.onBackPressedSupport()
    }
}