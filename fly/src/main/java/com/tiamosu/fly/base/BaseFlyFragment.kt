package com.tiamosu.fly.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.tiamosu.fly.fragmentation.FlySupportFragment
import com.tiamosu.fly.http.manager.NetworkDelegate

/**
 * @author tiamosu
 * @date 2020/2/18.
 */
abstract class BaseFlyFragment : FlySupportFragment(), IFlyBaseView {
    private val networkDelegate by lazy { NetworkDelegate() }
    var inflater: LayoutInflater? = null
    var container: ViewGroup? = null
    var rootView: View? = null

    //防止多次加载数据
    private var isDataLoaded = false

    override fun getContext(): AppCompatActivity = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //添加网络状态监听
        networkDelegate.addNetworkObserve(this)
        initParameters(arguments)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        this.container = container
        setContentView()
        return rootView
    }

    override fun setContentView() {
        if (getLayoutId() > 0) {
            rootView = inflater?.inflate(getLayoutId(), container, false)
        }
    }

    override fun onLazyInitView() {
        super.onLazyInitView()
        initView(rootView)
        initEvent()
        tryLoadData()
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        if (isCheckNetChanged()) {
            networkDelegate.hasNetWork(this)
        }
        tryLoadData()
    }

    private fun tryLoadData() {
        if (isNeedReload() || !isDataLoaded) {
            doBusiness()
            isDataLoaded = true
        }
    }
}