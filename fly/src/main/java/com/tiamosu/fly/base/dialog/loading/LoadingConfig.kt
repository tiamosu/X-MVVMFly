package com.tiamosu.fly.base.dialog.loading

import androidx.annotation.Keep
import com.tiamosu.fly.base.dialog.BaseFlyDialog

/**
 * @author tiamosu
 * @date 2021/2/25.
 */
@Keep
open class LoadingConfig(
    var delayMillis: Long = 0,          //弹框延迟展示时间
    var dialogTag: String? = null,      //弹框标识
    var dialog: BaseFlyDialog? = null   //loading弹框，优先级最高
)
