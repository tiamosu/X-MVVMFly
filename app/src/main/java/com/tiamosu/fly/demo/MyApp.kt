package com.tiamosu.fly.demo

import com.tiamosu.fly.base.BaseFlyApplication
import com.tiamosu.fly.demo.bridge.callback.SharedViewModel
import com.tiamosu.navigation.ext.lazyAppViewModel

/**
 * @author tiamosu
 * @date 2020/2/27.
 */

val sharedViewModel by lazyAppViewModel<SharedViewModel>()

@Suppress("unused")
class MyApp : BaseFlyApplication()