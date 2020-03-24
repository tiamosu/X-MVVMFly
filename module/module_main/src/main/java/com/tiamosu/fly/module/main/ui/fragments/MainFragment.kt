package com.tiamosu.fly.module.main.ui.fragments

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.tiamosu.fly.module.common.base.BaseFragment
import com.tiamosu.fly.module.common.router.Router
import com.tiamosu.fly.module.main.R
import com.tiamosu.fly.utils.newInstance
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * @author tiamosu
 * @date 2020/3/13.
 */
class MainFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(
        savedInstanceState: Bundle?,
        contentView: View?
    ) {
    }

    override fun initEvent() {
        btn_start_other.setOnClickListener {
            start(newInstance(Router.obtainFragmentOtherCls()))
        }
        btn_start_shared.setOnClickListener {
            start(newInstance(SharedFragment::class.java))
        }
        btn_start_bus.setOnClickListener {
            start(newInstance(BusFragment::class.java))
        }
        btn_start_http.setOnClickListener {
            start(newInstance(HttpFragment::class.java))
        }
        btn_start_glide.setOnClickListener {
            start(newInstance(GlideFragment::class.java))
        }
    }

    override fun doBusiness() {}

    override fun onBackPressedSupport(): Boolean {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            context.finish()
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            ToastUtils.showShort("再按一次退出")
        }
        return true
    }

    companion object {
        // 再点一次退出程序时间设置
        private const val WAIT_TIME = 2000L
        private var TOUCH_TIME: Long = 0
    }
}