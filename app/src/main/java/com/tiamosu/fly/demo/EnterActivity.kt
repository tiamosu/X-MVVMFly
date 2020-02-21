package com.tiamosu.fly.demo

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.fly.base.BaseFlyActivity
import kotlinx.android.synthetic.main.activity_enter.*

/**
 * @author tiamosu
 * @date 2020/2/21.
 */
class EnterActivity : BaseFlyActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_enter
    }

    override fun initData(bundle: Bundle?) {}
    override fun initView(savedInstanceState: Bundle?, contentView: View?) {
    }

    override fun initEvent() {
        btn_enter_page_main.setOnClickListener {
            ActivityUtils.startActivity(MainActivity::class.java)
        }
    }

    override fun doBusiness() {
    }
}