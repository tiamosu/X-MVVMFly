package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import me.yokeyword.fragmentation.SupportActivity

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : SupportActivity(), IBaseView {
    var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        initAny(savedInstanceState)
        doBusiness()
    }

    @CallSuper
    override fun initAny(savedInstanceState: Bundle?) {
        initData(intent.extras)
        initView(savedInstanceState, rootView)
        initEvent()
    }

    override fun setContentView() {
        if (getLayoutId() > 0) {
            rootView = View.inflate(this, getLayoutId(), null)
            setContentView(rootView)
        }
    }
}