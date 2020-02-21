package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.tiamosu.fly.http.manager.NetworkStateManager
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

        //添加网络状态监听
        lifecycle.addObserver(NetworkStateManager.instance)

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
            rootView = View.inflate(getContext(), getLayoutId(), null)
            setContentView(rootView)
        }
    }
}