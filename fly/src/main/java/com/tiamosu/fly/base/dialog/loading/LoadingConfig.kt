package com.tiamosu.fly.base.dialog.loading

import androidx.annotation.Keep
import com.tiamosu.fly.base.dialog.BaseFlyDialog

/**
 * @author tiamosu
 * @date 2021/2/25.
 */
@Keep
open class LoadingConfig(
    var isDelayedShow: Boolean = false,
    var delayMillis: Long = 200,
    var dialogTag: String? = null,
    var dialog: BaseFlyDialog? = null
)
