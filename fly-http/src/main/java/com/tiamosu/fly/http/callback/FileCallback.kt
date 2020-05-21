package com.tiamosu.fly.http.callback

import android.text.TextUtils
import com.blankj.utilcode.util.CloseUtils
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.utils.createFile
import com.tiamosu.fly.utils.postOnMain
import io.reactivex.rxjava3.functions.Action
import okhttp3.ResponseBody
import java.io.*

/**
 * 描述：返回文件类型数据
 *
 * @author tiamosu
 * @date 2020/3/7.
 */
abstract class FileCallback : ResultCallback<File> {
    private var destFileDir: String                     //目标文件存储的文件夹路径
    private var destFileName: String                    //目标文件存储的文件名

    constructor() : this(null)

    constructor(destFileName: String?) : this(null, destFileName)

    constructor(destFileDir: String?, destFileName: String?) {
        this.destFileDir = if (!TextUtils.isEmpty(destFileDir)) destFileDir!! else "download"
        this.destFileName =
            if (!TextUtils.isEmpty(destFileName)) destFileName!! else "unknownFile_" + System.currentTimeMillis()
    }

    @Throws(Throwable::class)
    override fun convertResponse(body: ResponseBody): File? {
        val file = createFile(destFileDir, destFileName)
        val inputStream = body.byteStream()
        var bis: BufferedInputStream? = null
        var fos: FileOutputStream? = null
        var bos: BufferedOutputStream? = null

        try {
            val progress = Progress()
            progress.totalSize = body.contentLength()
            progress.fileName = destFileName
            progress.filePath = file.absolutePath

            bis = BufferedInputStream(inputStream)
            fos = FileOutputStream(file)
            bos = BufferedOutputStream(fos)

            val bytes = ByteArray(1024 * 8)
            var read: Int
            while (bis.read(bytes).also { read = it } != -1) {
                bos.write(bytes, 0, read)
                onProgress(progress, read)
            }
            bos.flush()
            fos.flush()
        } catch (e: IOException) {
            return null
        } finally {
            CloseUtils.closeIO(body, bos, fos, bis, inputStream)
        }
        return file
    }

    private fun onProgress(progress: Progress, read: Int) {
        Progress.changeProgress(progress, read.toLong(), object : Progress.Action {
            override fun call(progress: Progress) {
                postOnMain(Action {
                    downloadProgress(progress) //进度回调的方法
                })
            }
        })
    }
}