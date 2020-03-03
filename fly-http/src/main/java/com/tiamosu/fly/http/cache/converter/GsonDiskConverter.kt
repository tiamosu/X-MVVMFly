package com.tiamosu.fly.http.cache.converter

import com.blankj.utilcode.util.CloseUtils
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.tiamosu.fly.http.utils.FlyHttpLog
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.reflect.Type
import java.util.*

/**
 * 描述：GSON-数据转换器
 * 1.GSON-数据转换器其实就是存储字符串的操作
 * 2.如果你的Gson有特殊处理，可以自己创建一个，否则用默认
 *
 * 优点：
 * 相对于 [SerializableDiskConverter] 转换器，存储的对象不需要进行序列化（Serializable），
 * 特别是一个对象中又包含很多其它对象，每个对象都需要Serializable，比较麻烦
 *
 * 缺点：
 * 就是存储和读取都要使用Gson进行转换，object -> String -> Object的给一个过程，
 * 相对来说每次都要转换性能略低，但是以现在的手机性能可以忽略不计了
 *
 * @author tiamosu
 * @date 2020/2/29.
 */
class GsonDiskConverter : IDiskConverter {
    private lateinit var gson: Gson

    fun GsonDiskConverter() {
        gson = Gson()
    }

    fun GsonDiskConverter(gson: Gson) {
        this.gson = gson
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> load(source: InputStream, type: Type?): T? {
        type ?: return null
        var value: T? = null
        try {
            val adapter = gson.getAdapter(TypeToken.get(type))
            val jsonReader = gson.newJsonReader(InputStreamReader(source))
            value = adapter.read(jsonReader) as? T
        } catch (e: JsonIOException) {
            FlyHttpLog.e(e.message)
        } catch (e: IOException) {
            FlyHttpLog.e(e.message)
        } catch (e: ConcurrentModificationException) {
            FlyHttpLog.e(e.message)
        } catch (e: JsonSyntaxException) {
            FlyHttpLog.e(e.message)
        } catch (e: Exception) {
            FlyHttpLog.e(e.message)
        } finally {
            CloseUtils.closeIO(source)
        }
        return value
    }

    override fun writer(sink: OutputStream, data: Any): Boolean {
        try {
            val json = gson.toJson(data)
            val bytes = json.toByteArray()
            sink.write(bytes, 0, bytes.size)
            sink.flush()
            return true
        } catch (e: JsonIOException) {
            FlyHttpLog.e(e.message)
        } catch (e: JsonSyntaxException) {
            FlyHttpLog.e(e.message)
        } catch (e: ConcurrentModificationException) {
            FlyHttpLog.e(e.message)
        } catch (e: IOException) {
            FlyHttpLog.e(e.message)
        } catch (e: Exception) {
            FlyHttpLog.e(e.message)
        } finally {
            CloseUtils.closeIO(sink)
        }
        return false
    }
}