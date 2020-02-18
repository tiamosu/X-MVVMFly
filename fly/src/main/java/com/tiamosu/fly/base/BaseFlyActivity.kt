package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import me.yokeyword.fragmentation.SupportActivity

/**
 * @author weixia
 * @date 2020/2/18.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFlyActivity : SupportActivity(), IActivity {
    var mContentView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(intent.extras)
        setContentView()
        initView(savedInstanceState, mContentView)
        initEvent()
        doBusiness()
    }

    private fun setContentView() {
        if (getLayoutId() <= 0) {
            return
        }
        mContentView = View.inflate(this, getLayoutId(), null)
        setContentView(mContentView)
    }
}