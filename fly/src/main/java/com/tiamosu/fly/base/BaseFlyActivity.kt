package com.tiamosu.fly.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import com.tiamosu.fly.http.manager.NetworkDelegate
import com.tiamosu.fly.http.manager.NetworkStateManager
import me.yokeyword.fragmentation.SupportActivity

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : SupportActivity(), IBaseView {
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
        lifecycle.addObserver(NetworkStateManager.instance)
        networkDelegate.addNetworkObserve(this)
    }

    override fun onNetworkStateChanged(isAvailable: Boolean) {
        Log.e("xia", "页面====：${javaClass.simpleName}   网络状态=====：$isAvailable")
    }

    override fun onNetReConnect() {
        Log.e("xia", "页面====：${javaClass.simpleName}   进行重新连接")
    }
}