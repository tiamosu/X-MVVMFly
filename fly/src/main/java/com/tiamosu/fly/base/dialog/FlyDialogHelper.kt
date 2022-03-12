package com.tiamosu.fly.base.dialog

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
object FlyDialogHelper {

    @JvmStatic
    fun safeShowDialog(dialog: BaseFlyDialog?) {
        kotlin.runCatching {
            if (dialog?.isShowing == false) {
                dialog.show()
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    @JvmStatic
    fun safeCloseDialog(dialog: BaseFlyDialog?) {
        kotlin.runCatching {
            if (dialog?.isShowing == true) {
                dialog.dismiss()
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    @JvmStatic
    fun safeShowDialog(dialogFragment: BaseFlyDialogFragment?) {
        kotlin.runCatching {
            dialogFragment?.show()
        }.onFailure {
            it.printStackTrace()
        }
    }

    @JvmStatic
    fun safeCloseDialog(dialogFragment: BaseFlyDialogFragment?) {
        kotlin.runCatching {
            dialogFragment?.dismiss()
        }.onFailure {
            it.printStackTrace()
        }
    }
}