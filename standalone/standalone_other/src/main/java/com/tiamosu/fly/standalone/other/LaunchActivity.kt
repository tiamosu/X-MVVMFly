package com.tiamosu.fly.standalone.other

import android.os.Bundle
import com.tiamosu.fly.module.common.base.ProxyActivity
import com.tiamosu.fly.module.common.integration.router.Router
import me.yokeyword.fragmentation.ISupportFragment

/**
 * @author tiamosu
 * @date 2020/3/24.
 */
class LaunchActivity : ProxyActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }

    override fun getRootFragment(): Class<out ISupportFragment?> {
        return Router.obtainFragmentOtherCls()
    }
}