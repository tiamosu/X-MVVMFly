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
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.tiamosu.fly.base.dialog.weak.WeakDialog

/**
 * @author tiamosu
 * @date 2020/2/19.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BaseFlyDialogFragment : DialogFragment() {
    protected var dialogCallback: IFlyDialogCallback? = null
    protected var fragmentActivity: FragmentActivity? = null

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
        var theme = View.NO_ID
        if (dialogCallback?.bindTheme()?.also { theme = it } != View.NO_ID) {
            return theme
        }
        return super.getTheme()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //解决内存泄漏问题（注意：无法监听 Dialog 的 onShow、onDismiss 事件；直接在 DialogFragment 层面进行监听）
        val dialog = WeakDialog(requireContext(), theme)
        val window = dialog.window ?: return dialog
        dialogCallback?.setWindowStyle(window)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (dialogCallback != null) {
            inflater.inflate(dialogCallback!!.bindLayout(), container, false)
        } else super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialogCallback?.initView(this, view)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        dialogCallback?.onCancel(this)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogCallback?.onDismiss(this)
    }

    @JvmOverloads
    fun show(tag: String? = javaClass.simpleName) {
        runOnUiThread {
            if (ActivityUtils.isActivityAlive(fragmentActivity)) {
                fragmentActivity?.supportFragmentManager?.apply {
                    findFragmentByTag(tag)?.also {
                        beginTransaction().remove(it)
                    }
                    showAllowingLoss(this, tag)
                }
            }
        }
    }

    /**
     * 解决 Can not perform this action after onSaveInstanceState问题（当前Activity不在栈顶时显示错误的问题）
     */
    private fun showAllowingLoss(manager: FragmentManager, tag: String?) {
        try {
            val cls = DialogFragment::class.java
            val mDismissed = cls.getDeclaredField("mDismissed")
            mDismissed.isAccessible = true
            mDismissed.set(this, false)
            val mShownByMe = cls.getDeclaredField("mShownByMe")
            mShownByMe.isAccessible = true
            mShownByMe.set(this, true)

            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        } catch (e: Exception) {
            //调系统的show()方法
            show(manager, tag)
            return
        }
    }

    override fun dismiss() {
        runOnUiThread {
            if (ActivityUtils.isActivityAlive(fragmentActivity)) {
                dismissAllowingStateLoss()
            }
        }
    }
}