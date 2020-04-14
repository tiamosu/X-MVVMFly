package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : AppCompatActivity(), IFlyBaseView {
    private val networkDelegate by lazy { NetworkDelegate() }
    var rootView: View? = null

    //防止多次加载数据
    private var isDataLoaded = false

    override fun getContext(): AppCompatActivity = this

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
}