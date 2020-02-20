package com.tiamosu.fly.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

import com.blankj.utilcode.util.Utils

/**
 * @author tiamosu
 * @date 2018/9/13.
 *
 * 剪贴板相关工具类
 */
@Suppress("unused")
object ClipboardUtils {

    private val clipboardManager: ClipboardManager by lazy {
        Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    @JvmStatic
    fun copyText(text: CharSequence) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text))
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    @JvmStatic
    fun getText(): CharSequence? {
        val clip: ClipData? = clipboardManager.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).coerceToText(Utils.getApp())
        } else null
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    @JvmStatic
    fun copyUri(uri: Uri) {
        clipboardManager.setPrimaryClip(ClipData.newUri(Utils.getApp().contentResolver, "uri", uri))
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    @JvmStatic
    fun getUri(): Uri? {
        val clip: ClipData? = clipboardManager.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).uri
        } else null
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    @JvmStatic
    fun copyIntent(intent: Intent) {
        clipboardManager.setPrimaryClip(ClipData.newIntent("intent", intent))
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    @JvmStatic
    fun getIntent(): Intent? {
        val clip: ClipData? = clipboardManager.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).intent
        } else null
    }
}
