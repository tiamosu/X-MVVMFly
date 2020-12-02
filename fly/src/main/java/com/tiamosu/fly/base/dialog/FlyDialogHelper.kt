package com.tiamosu.fly.base.dialog

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
object FlyDialogHelper {

    @JvmStatic
    fun safeShowDialog(dialog: BaseFlyDialog?) {
        try {
            if (dialog?.isShowing == false) {
                dialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun safeCloseDialog(dialog: BaseFlyDialog?) {
        try {
            if (dialog?.isShowing == true) {
                dialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun safeShowDialog(dialogFragment: BaseFlyDialogFragment?) {
        try {
            dialogFragment?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun safeCloseDialog(dialogFragment: BaseFlyDialogFragment?) {
        try {
            dialogFragment?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}