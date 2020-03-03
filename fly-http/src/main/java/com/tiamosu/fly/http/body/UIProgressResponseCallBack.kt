package com.tiamosu.fly.http.body

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.Serializable
import java.lang.ref.WeakReference

/**
 * 描述：可以直接更新UI的回调
 *
 * @author tiamosu
 * @date 2020/3/2.
 */
abstract class UIProgressResponseCallBack : ProgressResponseCallBack {

    //主线程Handler
    private val handler: Handler by lazy {
        UIHandler(Looper.getMainLooper(), this)
    }

    /**
     * UI层回调抽象方法
     *
     * @param bytesRead     当前读取响应体字节长度
     * @param contentLength 总字节长度
     * @param done          是否读取完成
     */
    abstract fun onUIResponseProgress(bytesRead: Long, contentLength: Long, done: Boolean)

    //处理UI层的Handler子类
    private class UIHandler(
        looper: Looper,
        uiProgressResponseListener: UIProgressResponseCallBack
    ) : Handler(looper) {
        //弱引用
        private val weakReference: WeakReference<UIProgressResponseCallBack> =
            WeakReference(uiProgressResponseListener)

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                RESPONSE_UPDATE -> {
                    val uiProgressResponseListener = weakReference.get() ?: return
                    //获得进度实体类
                    val progressModel = msg.obj as ProgressModel
                    //回调抽象方法
                    uiProgressResponseListener.onUIResponseProgress(
                        progressModel.currentBytes,
                        progressModel.contentLength,
                        progressModel.isDone
                    )
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onResponseProgress(bytesWritten: Long, contentLength: Long, done: Boolean) {
        //通过Handler发送进度消息
        val message = Message.obtain()
        message.obj = ProgressModel(bytesWritten, contentLength, done)
        message.what = RESPONSE_UPDATE
        handler.sendMessage(message)
    }

    inner class ProgressModel(
        //当前读取字节长度
        var currentBytes: Long,
        //总字节长度
        var contentLength: Long,
        //是否读取完成
        var isDone: Boolean
    ) : Serializable {

        fun setCurrentBytes(currentBytes: Long): ProgressModel {
            this.currentBytes = currentBytes
            return this
        }

        fun setContentLength(contentLength: Long): ProgressModel {
            this.contentLength = contentLength
            return this
        }

        fun setDone(done: Boolean): ProgressModel {
            isDone = done
            return this
        }

        override fun toString(): String {
            return "ProgressModel{" +
                    "currentBytes=" + currentBytes +
                    ", contentLength=" + contentLength +
                    ", done=" + isDone +
                    '}'
        }
    }

    companion object {
        private const val RESPONSE_UPDATE = 0x02
    }
}