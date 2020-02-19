package com.tiamosu.fly.base.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseDialogFragment : DialogFragment() {
    protected var mDialogLayoutCallback: IDialogLayoutCallback? = null
    protected var mDialogCallback: IDialogCallback? = null
    protected var mActivity: FragmentActivity? = null

    fun init(@NonNull activity: FragmentActivity, layoutCallback: IDialogLayoutCallback?): BaseDialogFragment {
        mActivity = activity
        mDialogLayoutCallback = layoutCallback
        return this
    }

    fun init(@NonNull activity: FragmentActivity, dialogCallback: IDialogCallback?): BaseDialogFragment {
        mActivity = activity
        mDialogCallback = dialogCallback
        return this
    }

    override fun getTheme(): Int {
        if (mDialogLayoutCallback != null) {
            val theme = mDialogLayoutCallback!!.bindTheme()
            if (theme != View.NO_ID) {
                return theme
            }
        }
        return super.getTheme()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (mDialogCallback != null) {
            mDialogCallback!!.bindDialog(mActivity!!)
        } else super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (mDialogLayoutCallback != null) {
            inflater.inflate(mDialogLayoutCallback!!.bindLayout(), container, false)
        } else super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mDialogLayoutCallback?.initView(this, view)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val window = dialog.window ?: return
        if (mDialogCallback != null) {
            mDialogCallback!!.setWindowStyle(window)
        } else if (mDialogLayoutCallback != null) {
            mDialogLayoutCallback!!.setWindowStyle(window)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        mDialogLayoutCallback?.onCancel(this)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mDialogLayoutCallback?.onDismiss(this)
    }

    @JvmOverloads
    fun show(tag: String? = javaClass.simpleName) {
        Utils.runOnUiThread {
            if (ActivityUtils.isActivityAlive(mActivity)) {
                val fm = mActivity!!.supportFragmentManager
                val prev = fm.findFragmentByTag(tag)
                if (prev != null) {
                    fm.beginTransaction().remove(prev)
                }
                super@BaseDialogFragment.showNow(fm, tag)
            }
        }
    }

    override fun dismiss() {
        Utils.runOnUiThread {
            if (ActivityUtils.isActivityAlive(mActivity)) {
                super@BaseDialogFragment.dismissAllowingStateLoss()
            }
        }
    }
}