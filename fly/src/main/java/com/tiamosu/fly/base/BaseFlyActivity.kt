package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.base.action.FlyActionActivity
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onCreate] → [initParameters] → [initView] → [initEvent]
 * → [createObserver] → [doBusiness] → [onStart] → [onResume] → [onPause]
 * → [onStop] → [onDestroy]
 *
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : FlyActionActivity(), IFlyBaseView {
    private val networkDelegate by lazy { NetworkDelegate() }
    var rootView: View? = null

    //是否在页面可见时加载数据，防止多次加载数据
    private var isVisibleLoadData = false

    //可用于初始化前做相关处理
    open fun onCreateInit(savedInstanceState: Bundle?) = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!onCreateInit(savedInstanceState)) {
            return
        }
        initParameters(bundle)
        setContentView()

        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
        initView(rootView)
        initEvent()
        createObserver()
        tryLoadData(true)
    }

    override fun setContentView() {
        if (getLayoutId() > 0) {
            rootView = View.inflate(getContext(), getLayoutId(), null)
            setContentView(rootView)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
        tryLoadData(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        isVisibleLoadData = false
    }

    private fun tryLoadData(isCreate: Boolean) {
        if (isCreate) {
            doBusiness()
        } else if (isNeedReload()) {
            if (isVisibleLoadData) {
                doBusiness()
            }
            isVisibleLoadData = true
        }
    }
}