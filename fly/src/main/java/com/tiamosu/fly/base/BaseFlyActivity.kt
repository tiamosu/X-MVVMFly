package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.tiamosu.fly.http.manager.NetworkDelegate
import me.yokeyword.fragmentation.SupportActivity

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : SupportActivity(), IFlyBaseView {
    var rootView: View? = null

    private val networkDelegate by lazy { NetworkDelegate() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        initAny(savedInstanceState)
        doBusiness()
    }

    override fun setContentView() {
        if (getLayoutId() > 0) {
            rootView = View.inflate(getContext(), getLayoutId(), null)
            setContentView(rootView)
        }
    }

    @CallSuper
    override fun initAny(savedInstanceState: Bundle?) {
        initData(intent.extras)
        initView(savedInstanceState, rootView)
        initEvent()

        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
    }

    override fun onNetworkStateChanged(isAvailable: Boolean) {}
    override fun onNetReConnect() {}
}