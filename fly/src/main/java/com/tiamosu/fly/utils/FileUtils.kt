@file:JvmName("FileUtils")

package com.tiamosu.fly.utils

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 * 根据文件目录和文件名创建文件
 */
fun createFile(fileDirName: String?, fileName: String): File {
    return File(createDir(fileDirName), fileName)
}

/**
 * 创建未存在的文件夹
 */
fun createDir(fileDirName: String?, type: String? = null): File? {
    val fileDirPath = Utils.getApp().getExternalFilesDir(type)?.absolutePath
        ?: Utils.getApp().cacheDir.absolutePath
    var dir = "$fileDirPath/"
    val folder = fileDirName ?: ""
    if (folder.isNotBlank()) {
        dir += "$folder/"
    }
    return createDir(File(dir))
}

/**
 * 创建未存在的文件夹
 */
fun createDir(file: File?): File? {
    return if (FileUtils.createOrExistsDir(file)) file else null
}

/**
 * 返回缓存文件夹
 * 应用在卸载后，会将 App-specific 目录下的数据删除。
 * Android Q 前提下，如果在 AndroidManifest.xml 中声明：android:hasFragileUserData="true" 用户可以选择是否保留。
 */
fun getCacheFile(): File {
    //获取应用程序内的外部缓存路劲
    var cacheFile = Utils.getApp().externalCacheDir
    //如果获取的文件为空,就使用自己定义的缓存文件夹做缓存路径
    cacheFile = cacheFile ?: createDir("")
    //如果获取的文件为空，就使用应用程序内的内部缓存路劲
    return cacheFile ?: Utils.getApp().cacheDir
}

/**
 * 获取Glide缓存目录文件
 */
fun getGlideCacheFile(): File? {
    return createDir(File(getAppComponent().cacheFile(), "glide"))
}

/**
 * 获取Glide缓存大小
 */
fun getGlideCacheSize(): Long {
    return FileUtils.getLength(getGlideCacheFile())
}

/**
 * 获取http数据缓存目录文件
 */
fun getHttpCacheFile(): File? {
    return createDir(File(getAppComponent().cacheFile(), "http"))
}

/**
 * 获取http数据缓存大小
 */
fun getHttpCacheSize(): Long {
    return FileUtils.getLength(getHttpCacheFile())
}
