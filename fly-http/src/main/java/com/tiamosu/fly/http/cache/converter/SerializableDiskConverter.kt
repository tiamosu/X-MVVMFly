package com.tiamosu.fly.http.cache.converter

import com.blankj.utilcode.util.CloseUtils
import com.tiamosu.fly.http.utils.FlyHttpLog
import java.io.*

/**
 * 描述：序列化对象的转换器
 * 使用改转换器，对象&对象中的其它所有对象都必须是要实现Serializable接口（序列化）
 *
 * @author tiamosu
 * @date 2020/3/10.
 */
class SerializableDiskConverter : IDiskConverter {

    @Suppress("UNCHECKED_CAST")
    override fun <T> load(source: InputStream): T? {
        //序列化的缓存不需要用到clazz
        var value: T? = null
        var oin: ObjectInputStream? = null
        try {
            oin = ObjectInputStream(source)
            value = oin.readObject() as? T
        } catch (e: IOException) {
            FlyHttpLog.eLog(e.message)
        } catch (e: ClassNotFoundException) {
            FlyHttpLog.eLog(e.message)
        } finally {
            CloseUtils.closeIO(oin)
        }
        return value
    }

    override fun writer(sink: OutputStream, data: Any?): Boolean {
        var oos: ObjectOutputStream? = null
        try {
            oos = ObjectOutputStream(sink)
            oos.writeObject(data)
            oos.flush()
            return true
        } catch (e: IOException) {
            FlyHttpLog.eLog(e.message)
        } finally {
            CloseUtils.closeIO(oos)
        }
        return false
    }
}