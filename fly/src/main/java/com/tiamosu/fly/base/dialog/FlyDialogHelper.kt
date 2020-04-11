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
        } catch (ignored: Exception) {
        }
    }

    @JvmStatic
    fun safeCloseDialog(dialog: BaseFlyDialog?) {
        try {
            if (dialog?.isShowing == true) {
                dialog.dismiss()
            }
        } catch (ignored: Exception) {
        }
    }

    @JvmStatic
    fun safeShowDialog(dialogFragment: BaseFlyDialogFragment?) {
        try {
            dialogFragment?.show()
        } catch (ignored: Exception) {
        }
    }

    @JvmStatic
    fun safeCloseDialog(dialogFragment: BaseFlyDialogFragment?) {
        try {
            if (dialogFragment?.dialog?.isShowing == true) {
                dialogFragment.dismiss()
            }
        } catch (ignored: Exception) {
        }
    }
}