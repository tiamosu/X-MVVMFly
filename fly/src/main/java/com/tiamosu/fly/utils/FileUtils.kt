@file:JvmName("FileUtils")

package com.tiamosu.fly.utils

import android.net.Uri
import com.blankj.utilcode.util.CloseUtils
import com.blankj.utilcode.util.Utils
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * @author tiamosu
 * @date 2020/3/18.
 */

fun createFile(fileDirName: String?, fileName: String): File {
    return File(createDir(fileDirName), fileName)
}

fun createDir(fileDirName: String?): File? {
    val fileDirPath = Utils.getApp().getExternalFilesDir(null)?.absolutePath
        ?: Utils.getApp().cacheDir.absolutePath
    val folder = fileDirName ?: Utils.getApp().packageName
    val dir = "$fileDirPath/$folder/"
    val fileDir = File(dir)
    return createOrExistsDir(fileDir)
}

/**
 * @return 创建未存在的文件夹
 */
fun createOrExistsDir(file: File?): File? {
    val isExist = file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    return if (isExist) file else null
}

/**
 * 返回缓存文件夹
 * 应用在卸载后，会将App-specific目录下的数据删除，如果在AndroidManifest.xml中声明：android:hasFragileUserData="true"用户可以选择是否保留。
 */
fun getCacheFile(): File {
    //获取应用程序内的外部缓存路劲
    var cacheFile = Utils.getApp().externalCacheDir
    //如果获取的文件为空,就使用自己定义的缓存文件夹做缓存路径
    cacheFile = cacheFile ?: createDir(Utils.getApp().packageName)
    //如果获取的文件为空，就使用应用程序内的内部缓存路劲
    return cacheFile ?: Utils.getApp().cacheDir
}

/**
 * 对于图片选择进行适配（对于媒体资源的访问，返回Uri后，再转为File）
 */
fun uriToFile(uri: Uri): File? {
    val imgFile = createOrExistsDir(Utils.getApp().getExternalFilesDir("image"))
    val file = File(imgFile?.absolutePath + File.separator + System.currentTimeMillis() + ".jpg")
    var bis: InputStream? = null
    var fos: FileOutputStream? = null
    var bos: BufferedOutputStream? = null

    try {
        // 使用openInputStream(uri)方法获取字节输入流
        bis = Utils.getApp().contentResolver.openInputStream(uri)
        fos = FileOutputStream(file)
        bos = BufferedOutputStream(fos)

        val bytes = ByteArray(1024)
        var read: Int
        do {
            read = bis?.read(bytes) ?: -1
            if (read == -1) break

            bos.write(bytes, 0, read)
        } while (true)

        bos.flush()
        fos.flush()
    } catch (e: Exception) {
        return null
    } finally {
        CloseUtils.closeIO(bos, fos, bis)
    }
    return file
}