package com.tiamosu.fly.base.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ViewUtils.runOnUiThread

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFlyDialogFragment : DialogFragment() {
    protected var dialogLayoutCallback: IFlyDialogLayoutCallback? = null
    protected var dialogCallback: IFlyDialogCallback? = null
    protected var fragmentActivity: FragmentActivity? = null

    fun init(
        context: Context,
        layoutCallback: IFlyDialogLayoutCallback?
    ): BaseFlyDialogFragment {
        fragmentActivity = getFragmentActivity(context)
        dialogLayoutCallback = layoutCallback
        return this
    }

    fun init(
        context: Context,
        dialogCallback: IFlyDialogCallback?
    ): BaseFlyDialogFragment {
        fragmentActivity = getFragmentActivity(context)
        this.dialogCallback = dialogCallback
        return this
    }

    private fun getFragmentActivity(context: Context): FragmentActivity? {
        val activity = ActivityUtils.getActivityByContext(context) ?: return null
        if (activity is FragmentActivity) {
            return activity
        }
        throw IllegalArgumentException(context.toString() + "not instanceof FragmentActivity")
    }

    override fun getTheme(): Int {
        if (dialogLayoutCallback != null) {
            val theme = dialogLayoutCallback!!.bindTheme()
            if (theme != View.NO_ID) {
                return theme
            }
        }
        return super.getTheme()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = if (dialogCallback != null) {
            dialogCallback!!.bindDialog(fragmentActivity!!)
        } else {
            super.onCreateDialog(savedInstanceState)
        }

        val window = dialog.window ?: return dialog
        if (dialogCallback != null) {
            dialogCallback!!.setWindowStyle(window)
        } else if (dialogLayoutCallback != null) {
            dialogLayoutCallback!!.setWindowStyle(window)
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (dialogLayoutCallback != null) {
            inflater.inflate(dialogLayoutCallback!!.bindLayout(), container, false)
        } else super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialogLayoutCallback?.initView(this, view)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dialogLayoutCallback?.onCancel(this)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogLayoutCallback?.onDismiss(this)
    }

    @JvmOverloads
    fun show(tag: String? = javaClass.simpleName) {
        runOnUiThread {
            if (ActivityUtils.isActivityAlive(fragmentActivity)) {
                val fm = fragmentActivity!!.supportFragmentManager
                val prev = fm.findFragmentByTag(tag)
                if (prev != null) {
                    fm.beginTransaction().remove(prev)
                }
                super@BaseFlyDialogFragment.showNow(fm, tag)
            }
        }
    }

    override fun dismiss() {
        runOnUiThread {
            if (ActivityUtils.isActivityAlive(fragmentActivity)) {
                super@BaseFlyDialogFragment.dismissAllowingStateLoss()
            }
        }
    }
}