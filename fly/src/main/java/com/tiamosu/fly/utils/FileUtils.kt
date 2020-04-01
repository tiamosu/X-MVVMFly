@file:JvmName("FileUtils")

package com.tiamosu.fly.utils

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 * @author tiamosu
 * @date 2020/3/18.
 */

fun createFile(fileDirName: String?, fileName: String): File {
    return File(createDir(fileDirName), fileName)
}

/**
 * @return 创建未存在的文件夹
 */
fun createDir(fileDirName: String?): File? {
    val fileDirPath = Utils.getApp().getExternalFilesDir(null)?.absolutePath
        ?: Utils.getApp().cacheDir.absolutePath
    val folder = fileDirName ?: Utils.getApp().packageName
    val dir = "$fileDirPath/$folder/"
    val fileDir = File(dir)
    return createDir(fileDir)
}

/**
 * @return 创建未存在的文件夹
 */
fun createDir(file: File?): File? {
    return if (FileUtils.createOrExistsDir(file)) file else null
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
