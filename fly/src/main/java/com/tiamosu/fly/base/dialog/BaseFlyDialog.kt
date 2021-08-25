package com.tiamosu.fly.base.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import com.blankj.utilcode.util.ActivityUtils
import com.tiamosu.fly.utils.launchMain

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
abstract class BaseFlyDialog @JvmOverloads constructor(
    context: Context,
    themeResId: Int = 0
) : Dialog(context, themeResId) {

    protected abstract fun bindLayout(): Int
    protected abstract fun initView(dialog: BaseFlyDialog, contentView: View)
    protected abstract fun setWindowStyle(window: Window?)

    val activity by lazy {
        val activity = ActivityUtils.getActivityByContext(context)
        if (activity !is Activity) {
            Log.e("BaseFlyDialog", "context is not instance of Activity")
        }
        activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityUtils.isActivityAlive(activity)) {
            val contentView = View.inflate(context, bindLayout(), null)
            setContentView(contentView)
            initView(this, contentView)
            setWindowStyle(window)
        }
    }

    override fun show() {
        launchMain {
            if (ActivityUtils.isActivityAlive(activity)) {
                super@BaseFlyDialog.show()
            }
        }
    }

    override fun dismiss() {
        launchMain {
            if (ActivityUtils.isActivityAlive(activity)) {
                super@BaseFlyDialog.dismiss()
            }
        }
    }

    fun showDialog() {
        FlyDialogHelper.safeShowDialog(this)
    }

    fun hideDialog() {
        FlyDialogHelper.safeCloseDialog(this)
    }
}