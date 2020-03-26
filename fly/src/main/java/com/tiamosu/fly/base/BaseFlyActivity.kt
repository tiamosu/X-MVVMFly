package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import com.tiamosu.fly.http.manager.NetworkDelegate
import me.yokeyword.fragmentation.SupportActivity

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : SupportActivity(), IFlyBaseView {
    var rootView: View? = null

    private val networkDelegate by lazy { NetworkDelegate() }

    //防止多次加载数据
    private var isDataLoaded = false

    override fun isNeedReload(): Boolean {
        return false
    }

    override fun isCheckNetChanged(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        initAny()

        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)

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

    private fun tryLoadData(isCreate: Boolean) {
        if (isCreate) {
            doBusiness()
            isDataLoaded = true
        } else if (isNeedReload()) {
            if (!isDataLoaded) {
                doBusiness()
            }
            isDataLoaded = false
        }
    }

    override fun onNetworkStateChanged(isConnected: Boolean) {}
    override fun onNetReConnect() {}

    override fun onDestroy() {
        isDataLoaded = false
        super.onDestroy()
    }
}