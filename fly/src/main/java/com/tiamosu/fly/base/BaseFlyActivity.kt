package com.tiamosu.fly.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.AppUtils
import com.tiamosu.fly.fragmentation.FlySupportActivity
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * 描述：生命周期调用顺序：[onCreate] → [initParameters] → [initView] → [initEvent]
 * → [createObserver] → [doBusiness] → [onStart] → [onResume] → [onPause]
 * → [onStop] → [onDestroy]
 *
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyActivity : FlySupportActivity(), IFlyBaseView {
    private val networkDelegate by lazy { NetworkDelegate() }
    var rootView: View? = null

    //防止多次加载数据
    private var isDataLoaded = false

    //是否在应用被杀死恢复时重启应用
    open fun isRelaunchApp() = false

    final override fun getContext(): AppCompatActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isRelaunchApp() && savedInstanceState != null) {
            AppUtils.relaunchApp()
            return
        }
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)

        initParameters(intent.extras)
        setContentView()
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