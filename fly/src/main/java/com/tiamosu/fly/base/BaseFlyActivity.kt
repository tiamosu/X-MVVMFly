package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import me.yokeyword.fragmentation.SupportActivity

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : SupportActivity(), IBaseView {
    var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(intent.extras)
        setContentView()
        initView(savedInstanceState, rootView)
        initEvent()
        doBusiness()
    }

    override fun setContentView() {
        if (getLayoutId() > 0) {
            rootView = View.inflate(this, getLayoutId(), null)
            setContentView(rootView)
        }
    }
}